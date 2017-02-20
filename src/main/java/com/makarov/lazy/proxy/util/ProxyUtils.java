package com.makarov.lazy.proxy.util;

import com.makarov.mapper.exception.GetterException;
import com.makarov.mapper.util.MapperUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Util class for creating generated classes with callback methods
 *
 * @author Makarov Alexey
 * @version 1.0
 */
public class ProxyUtils {

    /**
     * Fill fields proxy entity from object of super class
     *
     * @param object - old object
     * @param proxy  - proxy object
     * @return filled proxy object
     */
    public static Object setFields(Object object, Object proxy) {
        Class<?> clazz = MapperUtils.getEnhancerSuperclass(object);
        for (Field field : clazz.getDeclaredFields()) {
            MapperUtils.setValueField(field, proxy, getValueField(field, object));
        }
        return proxy;
    }

    /**
     * Get field name by property name
     *
     * @param getterName - property name
     * @return field name
     */
    public static String getFieldNameByMethod(String getterName) {
        int index = 0;
        String fieldName = "";
        for (char character : getterName.toCharArray()) {
            if (Character.isUpperCase(character)) {
                fieldName = getterName.substring(index).toLowerCase();
                break;
            }
            index++;
        }

        return fieldName;
    }

    /**
     * Get field number by property name
     *
     * @param fields     - fields
     * @param getterName - property name
     * @return number field
     */
    public static int getFieldNumberByGetterName(List<Field> fields, String getterName) {
        String fieldName = getFieldNameByMethod(getterName);

        for (int i = 0; i < fields.size(); i++) {
            if (fields.get(i).getName().equals(fieldName)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Transform fields into property names
     *
     * @param fields - fields
     * @return property names
     */
    public static List<String> getNameGettersByFields(List<Field> fields) {
        List<String> list = new ArrayList<>();

        for (Field field : fields) {
            if (field.getType().isAssignableFrom(boolean.class)) {
                list.add(MapperUtils.createMethodName("is", field.getName()));
            } else {
                list.add(MapperUtils.createMethodName("get", field.getName()));
            }
        }

        return list;
    }

    /**
     * Check whether the field value is null
     *
     * @param entity - entity
     * @param method - property name
     * @return true - field value is null
     * false - field value is not null
     */
    public static boolean isEmptyValue(Object entity, Method method) {
        try {
            Class<?> clazz = MapperUtils.getEnhancerSuperclass(entity);
            String fieldName = getFieldNameByMethod(method.getName());
            Field field = clazz.getDeclaredField(fieldName);

            return getValueField(field, entity) == null;
        } catch (NoSuchFieldException exception) {
            throw new GetterException("Field: " + getFieldNameByMethod(method.getName()) + "is not found");
        }
    }

    /**
     * Get value of the field
     *
     * @param field  - field
     * @param entity - entity
     * @return field value
     */
    public static Object getValueField(Field field, Object entity) {
        field.setAccessible(true);
        try {
            return field.get(entity);
        } catch (IllegalAccessException e) {
            throw new GetterException("Variable field : " + field.getName() + " has inadmissible access");
        }
    }
}
