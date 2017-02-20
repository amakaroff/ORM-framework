package com.makarov.annotation.relation;

import com.makarov.mapper.fetch.FetchType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for relation many to one
 *
 * @author Makarov Alexey
 * @version 1.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ManyToOne {

    /**
     * @return type of data loading
     */
    FetchType fetch() default FetchType.EAGER;
}
