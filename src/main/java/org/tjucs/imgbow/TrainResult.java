package org.tjucs.imgbow;

import java.util.List;

/**
 * 训练结果的描述
 * 
 * @author tess3ract <hty0807@gmail.com>
 */
public class TrainResult {

    /* 样本列表 */
    private List<Instance> instances;

    /* 词典 */
    private List<Feature> words;

    public TrainResult(List<Instance> instances, List<Feature> words) {
        this.instances = instances;
        this.words = words;
    }

    /**
     * @return the instances
     */
    public List<Instance> getInstances() {
        return instances;
    }

    /**
     * @param instances
     *            the instances to set
     */
    public void setInstances(List<Instance> instances) {
        this.instances = instances;
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
