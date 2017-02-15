package com.makarov.factory.generator.api;

import java.lang.reflect.Method;


public interface Generator {

    String generateQueryFromAnnotation(String query, Method method, Object[] params);

    String generateQueryFromMethodName(Method method, Object[] params);
}
