package com.makarov.factory;

import com.makarov.annotation.Table;
import com.makarov.annotation.repository.Query;
import com.makarov.factory.generator.impl.QueryGenerator;
import com.makarov.mapper.exception.dbmapper.ClassMappingException;
import com.makarov.mapper.util.MapperUtils;
import com.makarov.util.QueryExecutor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;


public class RepositoriesFactory {

    public static <T> T implement(final Class<T> interfaceMeta) {
        return interfaceMeta.cast(
                Proxy.newProxyInstance(
                        interfaceMeta.getClassLoader(),
                        new Class[]{interfaceMeta},
                        new InvocationHandler() {

                            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                                QueryGenerator generator = new QueryGenerator();
                                MapperUtils utils = new MapperUtils();

                                String query;
                                if (method.isAnnotationPresent(Query.class)) {
                                    Query queryAnnotation = method.getAnnotation(Query.class);
                                    String prepareQuery = queryAnnotation.query();
                                    query = generator.generateQueryFromAnnotation(prepareQuery, method, args);
                                } else {
                                    query = generator.generateQueryFromMethodName(method, args);
                                }

                                QueryExecutor executor = new QueryExecutor();
                                Class<?> returnType = method.getReturnType();

                                if (returnType.isAssignableFrom(void.class)) {
                                    executor.executeQuery(query);
                                    return null;
                                }

                                if (returnType.isAnnotationPresent(Table.class)) {
                                    return executor.findOne(query, returnType);
                                } else {
                                    ParameterizedType genericReturnType = (ParameterizedType) method.getGenericReturnType();
                                    Class<?> genericType = (Class<?>) genericReturnType.getActualTypeArguments()[0];

                                    if (genericType.isAnnotationPresent(Table.class)) {
                                        return utils.getCollection(returnType,
                                                executor.findSome(query, genericType));
                                    } else {
                                        throw new ClassMappingException("Class cannot be mapped with table");
                                    }
                                }
                            }
                        }));
    }
}
