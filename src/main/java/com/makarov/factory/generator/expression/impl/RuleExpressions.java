package com.makarov.factory.generator.expression.impl;

import com.makarov.factory.generator.expression.api.RuleConstruction;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Makarov Alexey
 * @version 1.0
 */
public class RuleExpressions implements RuleConstruction {

    private List<String> keyWords = new ArrayList<String>() {
        {
            add("Find");
            add("Save");
            add("Delete");
            add("Update");
            add("From");
            add("And");
            add("Or");
            add("By");
            add("Between");
            add("Set");
            add("Left");
            add("Right");
            add("Inner");
            add("Join");
            add("On");
            add("Order");
            add("Asc");
            add("Desc");
        }
    };


    public boolean isNotKeyWord(String word) {
        return !keyWords.contains(word);
    }

    public boolean isByCorrect(int currentIndex, List<String> words) {
        return "By".equals(words.get(currentIndex))
                && !"Order".equals(words.get(currentIndex - 1))
                && currentIndex + 1 < words.size()
                && isNotKeyWord(words.get(currentIndex + 1));
    }

    public boolean isAndOrCorrect(int currentIndex, List<String> words) {
        return ("And".equals(words.get(currentIndex)) || "Or".equals(words.get(currentIndex)))
                && currentIndex - 2 > 0
                && isByCorrect(currentIndex - 2, words)
                && currentIndex + 2 < words.size()
                && (isByCorrect(currentIndex + 1, words)
                && isNotKeyWord(words.get(currentIndex + 2))
                || isBetweenCorrect(currentIndex + 1, words));
    }

    public boolean isBetweenCorrect(int currentIndex, List<String> words) {
        return "Between".equals(words.get(currentIndex))
                && currentIndex + 1 < words.size()
                && isNotKeyWord(words.get(currentIndex + 1));
    }

    public boolean isFromCorrect(int currentIndex, List<String> words) {
        return "From".equals(words.get(currentIndex))
                && currentIndex + 1 < words.size()
                && isNotKeyWord(words.get(currentIndex + 1));

    }

    public boolean isSetCorrect(int currentIndex, List<String> words) {
        return "Set".equals(words.get(currentIndex))
                && currentIndex + 1 < words.size()
                && isNotKeyWord(words.get(currentIndex + 1));
    }

    public boolean isOrderByCorrect(int currentIndex, List<String> words) {
        return "Order".equals(words.get(currentIndex))
                && currentIndex + 2 < words.size()
                && "By".equals(words.get(currentIndex + 1))
                && isNotKeyWord(words.get(currentIndex + 2));
    }

    public boolean isJoinCorrect(int currentIndex, List<String> words) {
        return ("Left".equals(words.get(currentIndex))
                || "Inner".equals(words.get(currentIndex))
                || "Right".equals(words.get(currentIndex)))
                && currentIndex + 4 < words.size()
                && "Join".equals(words.get(currentIndex + 1))
                && isNotKeyWord(words.get(currentIndex + 2))
                && "On".equals(words.get(currentIndex + 3))
                && isNotKeyWord(words.get(currentIndex + 4));
    }
}
