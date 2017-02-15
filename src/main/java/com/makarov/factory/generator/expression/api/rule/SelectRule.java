package com.makarov.factory.generator.expression.api.rule;

import java.util.List;


public interface SelectRule {

    boolean isOrderByCorrect(int currentIndex, List<String> words);

    boolean isJoinCorrect(int currentIndex, List<String> words);
}
