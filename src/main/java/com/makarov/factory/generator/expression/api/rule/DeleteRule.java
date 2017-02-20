package com.makarov.factory.generator.expression.api.rule;

import java.util.List;

/**
 * Interface for checking "delete" words in method name
 *
 * @author Makarov Alexey
 * @version 1.0
 */
public interface DeleteRule {

    /**
     * Check whether word is From-word
     *
     * @param currentIndex - word index
     * @param words        - list of words
     * @return true - word is correct
     * false - word is not correct
     */
    boolean isFromCorrect(int currentIndex, List<String> words);
}
