package com.makarov.factory.generator.expression.api.rule;

import java.util.List;


public interface UpdateRule {

    boolean isSetCorrect(int currentIndex, List<String> words);
}
