package com.makarov.factory.generator.expression.adder;

import com.makarov.factory.generator.expression.api.RuleConstruction;
import com.makarov.factory.generator.expression.impl.RuleExpressions;
import com.makarov.mapper.util.MapperUtils;

import java.util.List;

public class ExpressionAdder {

    private MapperUtils utils = new MapperUtils();

    private RuleConstruction rule = new RuleExpressions();

    private List<String> words;

    private int currentParamIndex;

    public ExpressionAdder(List<String> words) {
        this.words = words;
        this.currentParamIndex = 0;
    }

    public void addBy(int currentIndex, StringBuilder criterion, Object[] params) {
        if (rule.isByCorrect(currentIndex, words)) {
            String parameter = utils.getParameter(params[currentParamIndex++]);
            criterion.append(" ")
                    .append(words.get(currentIndex + 1).toLowerCase())
                    .append("=")
                    .append(parameter);
        }
    }

    public void addAndOr(int currentIndex, StringBuilder criterion) {
        if (rule.isAndOrCorrect(currentIndex, words)) {
            criterion.append(" ").append(words.get(currentIndex).toUpperCase());
        }
    }

    public void addBetween(int currentIndex,
                           StringBuilder criterion, Object[] params) {
        if (rule.isBetweenCorrect(currentIndex, words)
                && params.length > currentParamIndex + 1) {
            criterion.append(" (")
                    .append(words.get(currentIndex + 1).toLowerCase())
                    .append(" ")
                    .append(words.get(currentIndex).toUpperCase())
                    .append(" ")
                    .append(utils.getParameter(params[currentParamIndex++]).toLowerCase())
                    .append(" AND ")
                    .append(utils.getParameter(params[currentParamIndex++]).toLowerCase())
                    .append(")");
        }
    }

    public void addSet(int currentIndex,
                       StringBuilder setter, Object[] params) {
        if (rule.isSetCorrect(currentIndex, words)) {
            String parameter = utils.getParameter(params[currentParamIndex++]);
            setter.append(" SET ")
                    .append(words.get(currentIndex + 1).toLowerCase())
                    .append("=")
                    .append(parameter);
        }
    }

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
