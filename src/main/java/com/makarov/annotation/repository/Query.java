package com.makarov.annotation.repository;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for creating query in dynamic interface method
 *
 * @author Makarov Alexey
 * @version 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Query {

    /**
     * @return sql-query
     */
    String query() default "";
}
