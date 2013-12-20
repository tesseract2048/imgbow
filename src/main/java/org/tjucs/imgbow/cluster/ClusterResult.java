package org.tjucs.imgbow.cluster;

import java.util.List;

import org.tjucs.imgbow.Feature;

/**
 * 聚类结果描述
 * 
 * @author tess3ract <hty0807@gmail.com>
 */
public class ClusterResult {

    /* 中心点列表 */
    private List<Feature> centroids;

    /* 各个点所属的类别序号 */
    private List<Integer> belongs;

    public ClusterResult(List<Feature> centroids, List<Integer> belongs) {
        this.centroids = centroids;
        this.belongs = belongs;
    }

    /**
     * @return the centroids
     */
    public List<Feature> getCentroids() {
        return centroids;
    }

    /**
     * @param centroids
     *            the centroids to set
     */
    public void setCentroids(List<Feature> centroids) {
        this.centroids = centroids;
    }

    /**
     * @return the belongs
     */
    public List<Integer> getBelongs() {
        return belongs;
    }

    /**
     * @param belongs
     *            the belongs to set
     */
    public void setBelongs(List<Integer> belongs) {
        this.belongs = belongs;
    }

}
