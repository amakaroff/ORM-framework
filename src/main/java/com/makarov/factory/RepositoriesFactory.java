package com.makarov.factory;

import com.makarov.annotation.Table;
import com.makarov.annotation.repository.Query;
import com.makarov.executor.QueryExecutor;
import com.makarov.factory.generator.impl.QueryGenerator;
import com.makarov.mapper.exception.dbmapper.ClassMappingException;
import com.makarov.mapper.util.MapperUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;

/**
 * Factory creating implementation interface with implemented methods
 *
 * @author Makarov Alexey
 * @version 1.0
 */
public class RepositoriesFactory {

    /**
     * Create object of implemented class
     *
     * @param interfaceMeta - interface type
     * @return object of implemented class
     */
    public static <T> T implement(final Class<T> interfaceMeta) {
        return interfaceMeta.cast(
                Proxy.newProxyInstance(
                        interfaceMeta.getClassLoader(),
                        new Class[]{interfaceMeta},
                        new InvocationHandler() {

                            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                                QueryGenerator generator = new QueryGenerator();

                                String query;
                                if (method.isAnnotationPresent(Query.class)) {
                                    Query queryAnnotation = method.getAnnotation(Query.class);
                                    String prepareQuery = queryAnnotation.query();
                                    query = generator.generateQueryFromAnnotation(prepareQuery, method, args);
                                } else {
                                    query = generator.generateQueryFromMethodName(method, args);
                                }

                                Class<?> returnType = method.getReturnType();

                                if (returnType.isAssignableFrom(void.class)) {
                                    QueryExecutor.executeQuery(query);
                                    return null;
                                }

                                if (MapperUtils.isGeneratedClass(returnType)) {
                                    returnType = MapperUtils.getEnhancerSuperclass(returnType);
                                }

                                if (returnType.isAnnotationPresent(Table.class)) {
                                    return QueryExecutor.findOne(query, returnType);
                                } else {
                                    ParameterizedType genericReturnType = (ParameterizedType) method.getGenericReturnType();
                                    Class<?> genericType = (Class<?>) genericReturnType.getActualTypeArguments()[0];

                                    if (MapperUtils.isGeneratedClass(genericType)) {
                                        genericType = MapperUtils.getEnhancerSuperclass(genericType);
                                    }

                                    if (genericType.isAnnotationPresent(Table.class)) {
                                        return MapperUtils.getCollection(returnType,
                                                QueryExecutor.findSome(query, genericType));
                                    } else {
                                        throw new ClassMappingException("Class cannot be mapped with table");
                                    }
                                }
                            }
                        }));
    }
}
