package com.makarov.factory.generator.api;

import java.lang.reflect.Method;

/**
 * Class for query generation by annotation or method name
 *
 * @author Makarov Alexey
 * @version 1.0
 */
public interface Generator {

    /**
     * Create query by annotation
     *
     * @param query  - prepare query
     * @param method - method
     * @param params - arguments of method
     * @return sql-query
     */
    String generateQueryFromAnnotation(String query, Method method, Object[] params);

    /**
     * Create query by method name
     *
     * @param method - method
     * @param params - arguments of method
     * @return sql-query
     */
    String generateQueryFromMethodName(Method method, Object[] params);
}
