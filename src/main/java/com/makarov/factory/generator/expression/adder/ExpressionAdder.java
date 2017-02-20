package com.makarov.factory.generator.expression.adder;

import com.makarov.core.TypeConfigurator;
import com.makarov.factory.generator.expression.api.RuleConstruction;
import com.makarov.factory.generator.expression.impl.RuleExpressions;
import com.makarov.mapper.manager.api.TypeManager;

import java.util.List;

/**
 * Class for adding new expressions in query
 *
 * @author Makarov Alexey
 * @version 1.0
 */
public class ExpressionAdder {

    private TypeManager manager = TypeConfigurator.getTypeManager();

    private RuleConstruction rule = new RuleExpressions();

    private List<String> words;

    private int currentParamIndex;

    public ExpressionAdder(List<String> words) {
        this.words = words;
        this.currentParamIndex = 0;
    }

    /**
     * Adding "By" in expression
     *
     * @param currentIndex - word index
     * @param criterion    - new expression
     * @param params       - arguments for use in expression
     */
    public void addBy(int currentIndex, StringBuilder criterion, Object[] params) {
        if (rule.isByCorrect(currentIndex, words)) {
            String parameter = manager.getSavedStringFromObject(params[currentParamIndex++]);
            criterion.append(" ")
                    .append(words.get(currentIndex + 1).toLowerCase())
                    .append("=")
                    .append(parameter);
        }
    }

    /**
     * Adding "And" or "Or" in expression
     *
     * @param currentIndex - word index
     * @param criterion    - new expression
     */
    public void addAndOr(int currentIndex, StringBuilder criterion) {
        if (rule.isAndOrCorrect(currentIndex, words)) {
            criterion.append(" ").append(words.get(currentIndex).toUpperCase());
        }
    }

    /**
     * Adding "Between" in expression
     *
     * @param currentIndex - word index
     * @param criterion    - new expression
     * @param params       - arguments for use in expression
     */
    public void addBetween(int currentIndex,
                           StringBuilder criterion, Object[] params) {
        if (rule.isBetweenCorrect(currentIndex, words)
                && params.length > currentParamIndex + 1) {
            criterion.append(" (")
                    .append(words.get(currentIndex + 1).toLowerCase())
                    .append(" ")
                    .append(words.get(currentIndex).toUpperCase())
                    .append(" ")
                    .append(manager.getSavedStringFromObject(params[currentParamIndex++]).toLowerCase())
                    .append(" AND ")
                    .append(manager.getSavedStringFromObject(params[currentParamIndex++]).toLowerCase())
                    .append(")");
        }
    }

    /**
     * Adding "Set" in expression
     *
     * @param currentIndex - word index
     * @param setter       - new expression
     * @param params       - arguments for use in expression
     */
    public void addSet(int currentIndex,
                       StringBuilder setter, Object[] params) {
        if (rule.isSetCorrect(currentIndex, words)) {
            String parameter = manager.getSavedStringFromObject(params[currentParamIndex++]);
            setter.append(" SET ")
                    .append(words.get(currentIndex + 1).toLowerCase())
                    .append("=")
                    .append(parameter);
        }
    }

    /**
     * Adding "OrderBy" in expression
     *
     * @param currentIndex - word index
     * @param sorter       - new expression
     */
    public void addOrderBy(int currentIndex, StringBuilder sorter) {
        if (rule.isOrderByCorrect(currentIndex, words)) {
            sorter.append(" ORDER BY ")
                    .append(words.get(currentIndex + 2).toLowerCase());
            if (currentIndex + 3 < words.size()
                    && ("Asc".equals(words.get(currentIndex + 3))
                    || "Desc".equals(words.get(currentIndex + 3)))) {
                sorter.append(" ")
                        .append(words.get(currentIndex + 3).toUpperCase());
            }
        }
    }

    /**
     * Adding "Join" in expression
     *
     * @param currentIndex    - word index
     * @param joiner          - new expression
     * @param secondTableName - table name
     */
    public void addJoin(int currentIndex,
                        StringBuilder joiner, String secondTableName) {
        if (rule.isJoinCorrect(currentIndex, words)) {
            String firstTableName = words.get(currentIndex + 2).toLowerCase();
            String joinColumn = words.get(currentIndex + 4).toLowerCase();

            joiner.append(" ")
                    .append(words.get(currentIndex).toUpperCase())
                    .append(" ")
                    .append(words.get(currentIndex + 1).toUpperCase())
                    .append(" ")
                    .append(firstTableName)
                    .append(" ")
                    .append(words.get(currentIndex + 3).toUpperCase())
                    .append(" ")
                    .append(firstTableName)
                    .append(".")
                    .append(joinColumn)
                    .append("=")
                    .append(secondTableName)
                    .append(".")
                    .append(joinColumn);
        }
    }
}
