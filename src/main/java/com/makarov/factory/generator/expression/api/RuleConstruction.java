package com.makarov.factory.generator.expression.api;

import com.makarov.factory.generator.expression.api.rule.DeleteRule;
import com.makarov.factory.generator.expression.api.rule.SelectRule;
import com.makarov.factory.generator.expression.api.rule.UpdateRule;

import java.util.List;

/**
 * Interface for checking key-word in method name
 *
 * @author Makarov Alexey
 * @version 1.0
 */
public interface RuleConstruction extends SelectRule, DeleteRule, UpdateRule {

    /**
     * Check whether word is key-word
     *
     * @param word - word
     * @return false - word is not key-word
     * true - word is key-word
     */
    boolean isNotKeyWord(String word);

    /**
     * Check whether word is By-word
     *
     * @param currentIndex - word index
     * @param words        - list of words
     * @return true - word is correct
     * false - word is not correct
     */
    boolean isByCorrect(int currentIndex, List<String> words);

    /**
     * Check whether word is And-word or Or-word
     *
     * @param currentIndex - word index
     * @param words        - list of words
     * @return true - word is correct
     * false - word is not correct
     */
    boolean isAndOrCorrect(int currentIndex, List<String> words);

    /**
     * Check whether word is Between-word
     *
     * @param currentIndex - word index
     * @param words        - list of words
     * @return true - word is correct
     * false - word is not correct
     */
    boolean isBetweenCorrect(int currentIndex, List<String> words);
}
