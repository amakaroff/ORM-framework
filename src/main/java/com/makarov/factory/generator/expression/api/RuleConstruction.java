package com.makarov.factory.generator.expression.api;

import com.makarov.factory.generator.expression.api.rule.SelectRule;
import com.makarov.factory.generator.expression.api.rule.UpdateRule;
import com.makarov.factory.generator.expression.api.rule.DeleteRule;

import java.util.List;


public interface RuleConstruction extends SelectRule, DeleteRule, UpdateRule {

    boolean isNotKeyWord(String word);

    boolean isByCorrect(int currentIndex, List<String> words);

    boolean isAndOrCorrect(int currentIndex, List<String> words);

    boolean isBetweenCorrect(int currentIndex, List<String> words);
}
