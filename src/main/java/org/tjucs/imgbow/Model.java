package org.tjucs.imgbow;

import java.io.Serializable;
import java.util.List;

import weka.classifiers.Classifier;

/**
 * 训练好的分类器模型描述
 * 
 * @author tess3ract <hty0807@gmail.com>
 */
public class Model implements Serializable {

    private static final long serialVersionUID = 9155043182593064253L;

    /* 分类器包含的类别 */
    private String[] categories;

    /* 词典 */
    private List<Feature> words;

    /* 分类器实例 */
    private Classifier classifier;

    public Model(String[] categories, List<Feature> words, Classifier classifier) {
        this.categories = categories;
        this.words = words;
        this.classifier = classifier;
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

    /**
     * @return the classifier
     */
    public Classifier getClassifier() {
        return classifier;
    }

    /**
     * @param classifier
     *            the classifier to set
     */
    public void setClassifier(Classifier classifier) {
        this.classifier = classifier;
    }

}
