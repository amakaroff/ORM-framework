package com.makarov.annotation.repository;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for name parameters in the method with annotation @Query in dynamic interface
 *
 * @author Makarov Alexey
 * @version 1.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Param {

    /**
     * @return column name
     */
    String value() default "";
}
