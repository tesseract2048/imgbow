package org.tjucs.imgbow;

import java.io.Serializable;
import java.util.List;

public class Dict implements Serializable {

    private static final long serialVersionUID = 140246781084803932L;

    private String[] categories;

    private List<Feature> words;

    public Dict(String[] categories, List<Feature> words) {
        this.categories = categories;
        this.words = words;
    }

    /**
     * @return the categories
     */
    public String[] getCategories() {
        return categories;
    }

    /**
     * @param categories
     *            the categories to set
     */
    public void setCategories(String[] categories) {
        this.categories = categories;
    }

    /**
     * @return the words
     */
    public List<Feature> getWords() {
        return words;
    }

    /**
     * @param words
     *            the words to set
     */
    public void setWords(List<Feature> words) {
        this.words = words;
    }

}
