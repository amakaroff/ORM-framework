package com.makarov.factory.generator.expression.api.rule;

import java.util.List;

/**
 * Interface for checking "select" words in method name
 *
 * @author Makarov Alexey
 * @version 1.0
 */
public interface SelectRule {

    /**
     * Check whether word is OrderBy-word
     *
     * @param currentIndex - word index
     * @param words        - list of words
     * @return true - word is correct
     * false - word is not correct
     */
    boolean isOrderByCorrect(int currentIndex, List<String> words);

    /**
     * Check whether word is Join-word
     *
     * @param currentIndex - word index
     * @param words        - list of words
     * @return true - word is correct
     * false - word is not correct
     */
    boolean isJoinCorrect(int currentIndex, List<String> words);
}
