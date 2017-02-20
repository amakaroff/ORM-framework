package com.makarov.factory.generator.manager;

import com.makarov.factory.generator.expression.adder.ExpressionAdder;

import java.util.List;

/**
 * Class for creating "select" query by method name
 *
 * @author Makarov Alexey
 * @version 1.0
 */
public class EntitySelector {

    /**
     * Create "select" sql-query by method name
     *
     * @param words     - list words in method name
     * @param params    - arguments of method
     * @param tableName - table name
     * @return sql-query
     */
    public String getSelectQuery(List<String> words, Object[] params, String tableName) {
        String queryPattern = "SELECT * FROM %s%s%s%s%s";

        StringBuilder criterion = new StringBuilder();
        StringBuilder sorting = new StringBuilder();
        StringBuilder joiner = new StringBuilder();
        ExpressionAdder adder = new ExpressionAdder(words);

        for (int index = 1; index < words.size(); index++) {
            adder.addBy(index, criterion, params);
            adder.addAndOr(index, criterion);
            adder.addBetween(index, criterion, params);

            if (sorting.length() == 0) {
                adder.addOrderBy(index, sorting);
            }

            if (joiner.length() == 0) {
                adder.addJoin(index, joiner, tableName);
            }
        }

        if (criterion.length() == 0) {
            return String.format(queryPattern, tableName,
                    joiner.toString(), "", criterion.toString(), sorting.toString());
        } else {
            return String.format(queryPattern, tableName,
                    joiner.toString(), " WHERE", criterion.toString(), sorting.toString());
        }
    }
}
