package com.makarov.factory.generator.manager;

import com.makarov.annotation.Column;
import com.makarov.annotation.Table;
import com.makarov.factory.generator.expression.adder.ExpressionAdder;
import com.makarov.factory.generator.expression.api.RuleConstruction;
import com.makarov.factory.generator.expression.impl.RuleExpressions;
import com.makarov.mapper.util.MapperUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for creating "delete" query by method name
 *
 * @author Makarov Alexey
 * @version 1.0
 */
public class EntityDeleter {

    private final String queryPattern = "DELETE FROM %s%s%s;";

    /**
     * Create query by method name
     *
     * @param words  - list words in method name
     * @param params - arguments of method
     * @return sql-query
     */
    public String getDeleteQuery(List<String> words, Object[] params) {
        ExpressionAdder adder = new ExpressionAdder(words);
        RuleConstruction rule = new RuleExpressions();

        if (params != null && isMappedClass(params[0])) {
            return deleteByObject(params[0]);
        }

        String table = null;
        StringBuilder criterion = new StringBuilder();

        for (int index = 1; index < words.size(); index++) {
            if (table == null && rule.isFromCorrect(index, words)) {
                table = words.get(index + 1).toLowerCase();
            }

            adder.addBy(index, criterion, params);
            adder.addAndOr(index, criterion);
            adder.addBetween(index, criterion, params);
        }

        if (criterion.length() == 0) {
            return String.format(queryPattern, table, "", criterion.toString());
        } else {
            return String.format(queryPattern, table, " WHERE", criterion.toString());
        }
    }

    /**
     * Create query by method name with parameter as an entity
     *
     * @param entity deletable entity
     * @return sql-query
     */
    private String deleteByObject(Object entity) {
        Class<?> clazz = MapperUtils.getEnhancerSuperclass(entity);

        if (!clazz.isAnnotationPresent(Table.class)) {
            return deleteByObjects(entity);
        }

        String table = clazz.getAnnotation(Table.class).name();
        List<String> expressions = new ArrayList<>();

        if (entity.getClass().isAssignableFrom(clazz)) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Column.class)) {
                    String columnName = field.getAnnotation(Column.class).name();
                    String result = columnName + "=" + MapperUtils.getValueField(field, entity);
                    expressions.add(result);
                }
            }
        }

        return String.format(queryPattern, table, " WHERE ",
                String.join(" AND ", expressions));
    }

    /**
     * Create query by method name with parameter as an list or array entities
     *
     * @param entities deletable entities
     * @return sql-query
     */
    private String deleteByObjects(Object entities) {
        Class<?> clazz = entities.getClass();
        StringBuilder query = new StringBuilder();

        if (clazz.isArray()) {
            int length = Array.getLength(entities);

            for (int i = 0; i < length; i++) {
                query.append(deleteByObject(Array.get(entities, i)));
            }
        } else {
            Iterable iterable;
            try {
                iterable = Iterable.class.cast(entities);
            } catch (ClassCastException exception) {
                throw new ClassCastException("Deleted objects have to be a iterable");
            }

            for (Object anIterable : iterable) {
                query.append(deleteByObject(anIterable));
            }
        }
        return query.toString();
    }


    /**
     * Check class for mapping on table
     *
     * @param entity - entity class
     * @return true - class in mapped on table
     * false - class is not mapped on table
     */
    private boolean isMappedClass(Object entity) {
        if (entity == null) {
            return false;
        }

        Class<?> clazz = entity.getClass();

        if (MapperUtils.isGeneratedClass(clazz)) {
            clazz = MapperUtils.getEnhancerSuperclass(entity);
        }

        return clazz.isAnnotationPresent(Table.class)
                || clazz.isArray()
                && clazz.getComponentType().isAnnotationPresent(Table.class)
                || Iterable.class.isAssignableFrom(clazz);
    }
}






