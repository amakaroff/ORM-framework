package com.makarov.factory.generator.expression.api.rule;

import java.util.List;


public interface DeleteRule {

    boolean isFromCorrect(int currentIndex, List<String> words);
}
