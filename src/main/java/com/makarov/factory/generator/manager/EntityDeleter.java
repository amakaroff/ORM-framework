package com.makarov.factory.generator.manager;

import com.makarov.factory.generator.expression.adder.ExpressionAdder;
import com.makarov.annotation.Column;
import com.makarov.annotation.Table;
import com.makarov.factory.generator.expression.api.RuleConstruction;
import com.makarov.factory.generator.expression.impl.RuleExpressions;
import com.makarov.mapper.util.MapperUtils;
import com.sun.deploy.util.StringUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class EntityDeleter {

    private MapperUtils mapperUtils = new MapperUtils();

    private final String queryPattern = "DELETE FROM %s%s%s;";

    public String getDeleteQuery(List<String> words, Object[] params) {
        ExpressionAdder adder = new ExpressionAdder(words);
        RuleConstruction rule = new RuleExpressions();

        if (isMappedClass(params[0])) {
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

    public String deleteByObject(Object entity) {
        Class<?> clazz = entity.getClass();

        if (!clazz.isAnnotationPresent(Table.class)) {
            return deleteByObjects(entity);
        }

        String table = clazz.getAnnotation(Table.class).name();
        List<String> expressions = new ArrayList<>();

        if (entity.getClass().isAssignableFrom(clazz)) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Column.class)) {
                    String columnName = field.getAnnotation(Column.class).name();
                    String result = columnName + "=" + mapperUtils.getValueField(field, entity);
                    expressions.add(result);
                }
            }
        }

        return String.format(queryPattern, table, " WHERE ",
                StringUtils.join(expressions, " AND "));
    }

    public String deleteByObjects(Object entity) {
        Class<?> clazz = entity.getClass();
        StringBuilder query = new StringBuilder();

        if (clazz.isArray()) {
            int length = Array.getLength(entity);

            for (int i = 0; i < length; i++) {
                query.append(deleteByObject(Array.get(entity, i)));
            }
        } else {
            Iterable iterable;
            try {
                iterable = Iterable.class.cast(entity);
            } catch (ClassCastException exception) {
                throw new ClassCastException("Saved objects have to be a iterable");
            }

            for (Object anIterable : iterable) {
                query.append(deleteByObject(anIterable));
            }
        }
        return query.toString();
    }


    public boolean isMappedClass(Object entity) {
        if (entity == null) {
            return false;
        }

        Class<?> clazz = entity.getClass();

        return clazz.isAnnotationPresent(Table.class)
                || clazz.isArray()
                && clazz.getComponentType().isAnnotationPresent(Table.class)
                || Iterable.class.isAssignableFrom(clazz);
    }
}






