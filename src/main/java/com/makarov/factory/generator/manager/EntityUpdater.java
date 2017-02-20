package com.makarov.factory.generator.manager;

import com.makarov.factory.generator.exception.ErrorNameMethodException;
import com.makarov.factory.generator.expression.adder.ExpressionAdder;
import com.makarov.factory.generator.expression.api.RuleConstruction;
import com.makarov.factory.generator.expression.impl.RuleExpressions;

import java.util.List;

/**
 * Class for creating "update" query by method name
 *
 * @author Makarov Alexey
 * @version 1.0
 */
public class EntityUpdater {

    /**
     * Create "select" sql-query by method name
     *
     * @param words  - list words in method name
     * @param params - arguments of method
     * @return sql-query
     */
    public String getUpdateQuery(List<String> words, Object[] params) {
        String query = "UPDATE %s%s%s%s";
        String tableName = words.get(1);

        StringBuilder setter = new StringBuilder();
        StringBuilder criterion = new StringBuilder();
        ExpressionAdder adder = new ExpressionAdder(words);
        RuleConstruction rule = new RuleExpressions();

        if (rule.isNotKeyWord(tableName)) {
            tableName = tableName.toLowerCase();
        } else {
            throw new ErrorNameMethodException("Table name is uncorrected");
        }

        for (int index = 1; index < words.size(); index++) {
            if (setter.length() == 0) {
                adder.addSet(index, setter, params);
            }
            adder.addBy(index, criterion, params);
            adder.addAndOr(index, criterion);
            adder.addBetween(index, criterion, params);
        }

        if (criterion.length() == 1) {
            return String.format(query, tableName, setter.toString(), "", criterion.toString());
        } else {
            return String.format(query, tableName, setter.toString(), " WHERE", criterion.toString());
        }
    }
}
