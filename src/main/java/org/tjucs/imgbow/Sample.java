package org.tjucs.imgbow;

/**
 * 样本文件描述
 * 
 * @author tess3ract <hty0807@gmail.com>
 */
public class Sample {

    /* 路径 */
    private String path;

    /* 分类标注 */
    private String category;

    public Sample() {

    }

    public Sample(String path) {
        this.path = path;
    }

    public Sample(String path, String cate) {
        this.path = path;
        this.category = cate;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path
     *            the path to set
     */
    public void setPath(String path) {
        this.path = path;
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

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Sample [path=" + path + ", category=" + category + "]";
    }

}
