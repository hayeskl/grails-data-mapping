package org.grails.datastore.gorm.validation.constraints.factory

import grails.gorm.validation.Constraint
import grails.gorm.validation.exceptions.ValidationConfigurationException
import groovy.transform.CompileStatic
import org.grails.datastore.gorm.validation.constraints.AbstractConstraint
import org.springframework.context.MessageSource

import java.beans.Introspector
import java.lang.reflect.Constructor

/**
 * A default factory for creating constraints
 *
 * @author Graeme Rocher
 * @since 6.0
 */
@CompileStatic
class DefaultConstraintFactory implements ConstraintFactory {

    final Class<? extends Constraint> type
    final String name
    final MessageSource messageSource
    final Class targetType

    protected final Constructor constraintConstructor

    DefaultConstraintFactory(Class<? extends Constraint> constraintClass, MessageSource messageSource, Class targetType = Object) {
        this.type = constraintClass
        this.name = Introspector.decapitalize(constraintClass.simpleName) - "Constraint"
        this.messageSource = messageSource
        this.targetType = targetType

        try {
            constraintConstructor = constraintClass.getConstructor(Class, String, Object, MessageSource)
        } catch (Throwable e) {
            throw new ValidationConfigurationException("Invalid constraint type [$constraintClass] must have a 4 argument constructor accepting the Class, propertyName, constrainedObject and MesssageSource. Message: $e.message", e)
        }
    }

    @Override
    boolean supports(Class targetType) {
        return this.targetType.isAssignableFrom(targetType)
    }

    @Override
    Constraint build(Class owner, String property, Object constrainingValue) {
        return type.newInstance(owner, property, constrainingValue, messageSource)
    }
}
