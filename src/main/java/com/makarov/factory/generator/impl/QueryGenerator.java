package com.makarov.factory.generator.impl;

import com.makarov.annotation.repository.Param;
import com.makarov.factory.generator.api.Generator;
import com.makarov.factory.generator.exception.ErrorNameMethodException;
import com.makarov.factory.generator.exception.NumberMethodParameterException;
import com.makarov.factory.generator.manager.EntityDeleter;
import com.makarov.factory.generator.manager.EntitySaver;
import com.makarov.factory.generator.manager.EntitySelector;
import com.makarov.factory.generator.manager.EntityUpdater;
import com.makarov.mapper.util.MapperUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Makarov Alexey
 * @version 1.0
 */
public class QueryGenerator implements Generator {

    public String generateQueryFromAnnotation(String query, Method method, Object[] params) {
        List<Parameter> parameters = Arrays.asList(method.getParameters());

        for (int i = 0; i < parameters.size(); i++) {
            Param param = parameters.get(i).getAnnotation(Param.class);
            if (param != null) {
                String parameterName = '{' + param.value() + '}';

                if (parameters.get(i).getType().isAssignableFrom(String.class)) {
                    String value = "'" + params[i].toString() + "'";
                    query = query.replace(parameterName, value);
                } else {
                    query = query.replace(parameterName, params[i].toString());
                }
            }
        }

        return query;
    }

    public String generateQueryFromMethodName(Method method, Object[] params) {
        List<String> words = parseMethodName(method.getName());
        String definedWord = words.get(0);

        if ("save".equals(definedWord)) {
            if (params.length == 0) {
                throw new NumberMethodParameterException("Save entity is absent");
            }
            EntitySaver saver = new EntitySaver();
            return saver.getSaveQuery(params);
        } else if ("delete".equals(definedWord)) {
            EntityDeleter deleter = new EntityDeleter();
            return deleter.getDeleteQuery(words, params);
        } else if ("find".equals(definedWord)) {
            EntitySelector selector = new EntitySelector();
            return selector.getSelectQuery(words, params, MapperUtils.getTableName(method));
        } else if ("update".equals(definedWord)) {
            EntityUpdater updater = new EntityUpdater();
            return updater.getUpdateQuery(words, params);
        } else {
            throw new ErrorNameMethodException("Method name: " + method.getName() + " is unacceptable");
        }
    }

    private List<String> parseMethodName(String methodName) {
        List<StringBuilder> expressionsTemplate = new ArrayList<>();
        expressionsTemplate.add(new StringBuilder());

        int expressionIndex = 0;
        for (char character : methodName.toCharArray()) {
            if (Character.isUpperCase(character)) {
                expressionIndex++;
                expressionsTemplate.add(new StringBuilder());
            }
            expressionsTemplate.get(expressionIndex).append(character);
        }

        List<String> expressions = new ArrayList<>();

        for (StringBuilder stringBuilder : expressionsTemplate) {
            expressions.add(stringBuilder.toString());
        }

        return expressions;
    }
}
