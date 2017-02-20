package com.makarov.factory.generator.expression.api.rule;

import java.util.List;

/**
 * Interface for checking "update" words in method name
 *
 * @author Makarov Alexey
 * @version 1.0
 */
public interface UpdateRule {

    /**
     * Check whether word is Set-word
     *
     * @param currentIndex - word index
     * @param words        - list of words
     * @return true - word is correct
     * false - word is not correct
     */
    boolean isSetCorrect(int currentIndex, List<String> words);
}
