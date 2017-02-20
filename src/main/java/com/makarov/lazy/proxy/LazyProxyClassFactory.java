package com.makarov.lazy.proxy;

import com.makarov.annotation.relation.ManyToOne;
import com.makarov.annotation.relation.OneToMany;
import com.makarov.annotation.relation.OneToOne;
import com.makarov.core.DataSourceLoader;
import com.makarov.core.exception.QueryExecuteException;
import com.makarov.lazy.proxy.util.ProxyUtils;
import com.makarov.mapper.impl.DBMapper;
import com.makarov.mapper.util.MapperUtils;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory creating generated class with callback methods
 *
 * @author Makarov Alexey
 * @version 1.0
 */
public class LazyProxyClassFactory {

    private static DBMapper mapper = new DBMapper();

    /**
     * Get proxy instance based on another object with callback methods
     *
     * @param clazz            - type of super class
     * @param object           - object of super class
     * @param fields           - fields with lazy initialisation
     * @param joinColumnsValue - values of join columns
     * @return object of generated class
     */
    public static <T> T getProxy(final Class<T> clazz, final Object object,
                                 final List<Field> fields, final List<Object> joinColumnsValue) {
        final List<String> getterNames = ProxyUtils.getNameGettersByFields(fields);
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {

                if (getterNames.contains(method.getName()) && ProxyUtils.isEmptyValue(o, method)) {
                    int numberField = ProxyUtils.getFieldNumberByGetterName(fields, method.getName());
                    Field field = fields.get(numberField);
                    Object joinColumnValue = joinColumnsValue.get(numberField);

                    Object setValue = callBack(field, joinColumnValue, o);
                    MapperUtils.setValueField(field, o, setValue);
                }

                return methodProxy.invokeSuper(o, objects);
            }
        });

        return clazz.cast(ProxyUtils.setFields(object, enhancer.create()));
    }


    /**
     * Create loadable object
     *
     * @param field           - field with lazy initialisation
     * @param joinColumnValue - value of join column
     * @param object          - proxy class
     * @return loadable object
     */
    @SuppressWarnings(value = "unchecked")
    private static Object callBack(Field field, Object joinColumnValue, Object object) {
        String query = MapperUtils.createQuery(joinColumnValue, field, object);
        try (Connection connection = DataSourceLoader.getConnection();
             Statement statement = connection.createStatement()) {
            if (field.isAnnotationPresent(OneToOne.class) || field.isAnnotationPresent(ManyToOne.class)) {
                ResultSet resultSet = statement.executeQuery(query);

                if (resultSet.next()) {
                    return mapper.getObjectFromData(resultSet, field.getType(), object);
                }
            } else if (field.isAnnotationPresent(OneToMany.class)) {
                List list = new ArrayList<>();
                ResultSet resultSet = statement.executeQuery(query);

                while (resultSet.next()) {
                    list.add(mapper.getObjectFromData(resultSet, MapperUtils.getGenericTypeFromField(field), object));
                }
                return MapperUtils.getCollection(field.getType(), list);
            }
            return null;
        } catch (SQLException exception) {
            throw new QueryExecuteException(exception.getMessage());
        }
    }
}
