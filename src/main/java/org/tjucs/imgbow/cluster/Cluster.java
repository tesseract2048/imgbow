package org.tjucs.imgbow.cluster;

import java.util.List;

import org.tjucs.imgbow.Feature;

/**
 * 聚类器接口描述
 * 
 * @author tess3ract <hty0807@gmail.com>
 */
public interface Cluster {

    /* run clustering in features */
    public ClusterResult getSets(List<Feature> features, int partition);

}
