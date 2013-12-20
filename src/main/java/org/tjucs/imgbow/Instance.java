package org.tjucs.imgbow;

import java.util.Arrays;

/**
 * 单个图像样本的描述
 * 
 * @author tess3ract <hty0807@gmail.com>
 */
public class Instance {

    /* 图像文件名 */
    private String image;

    /* 统计得到的词频 */
    private double[] freq;

    /* 标注分类 */
    private String category;

    public Instance(double[] freq) {
        setFreq(freq);
    }

    /**
     * @return the image
     */
    public String getImage() {
        return image;
    }

    /**
     * @param image
     *            the image to set
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category
     *            the category to set
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * @return the freq
     */
    public double[] getFreq() {
        return freq;
    }

    /**
     * @param freq
     *            the freq to set
     */
    public void setFreq(double[] freq) {
        this.freq = freq;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Instance [freq=" + Arrays.toString(freq) + "]";
    }

}
