package org.tjucs.imgbow.cluster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.tjucs.imgbow.Feature;
import org.tjucs.imgbow.util.FeatureTree;

/**
 * K-Means 聚类器实现
 * 
 * @author tess3ract <hty0807@gmail.com>
 */
public class KMeansCluster implements Cluster {

    /* 误差阈值 */
    private static final double ERROR_THRESHOLD = 1.2f;

    public ClusterResult getSets(List<Feature> features, int partition) {
        List<Feature> centroids = new ArrayList<Feature>();
        Integer[] belongs = new Integer[features.size()];
        /* generate initial sets */
        Random random = new Random();
        List<Feature> pool = new ArrayList<Feature>(features);
        for (int i = 0; i < partition; i++) {
            int index = random.nextInt(pool.size());
            centroids.add(pool.get(index));
            pool.remove(index);
        }
        while (true) {
            /* initialize a hashtable to store point belongs to each set */
            Map<Integer, List<Feature>> elements = new HashMap<Integer, List<Feature>>();
            for (int i = 0; i < partition; i++) {
                elements.put(i, new ArrayList<Feature>());
            }
            /* build kdtree for centroids */
            FeatureTree tree = new FeatureTree();
            for (int i = 0; i < centroids.size(); i++) {
                tree.add(i, centroids.get(i));
            }
            /* calculate new centroid of each point */
            for (int i = 0; i < features.size(); i++) {
                Feature f = features.get(i);
                belongs[i] = tree.queryNearest(f);
                elements.get(belongs[i]).add(f);
            }
            /* recalcuate centroid */
            double error = 0.0f;
            for (Map.Entry<Integer, List<Feature>> entry: elements.entrySet()) {
                int set = entry.getKey();
                List<Feature> points = entry.getValue();
                Feature newCentroid = new Feature(false);
                for (Feature f: points) {
                    newCentroid.add(f);
                }
                newCentroid.divide(points.size());
                error += newCentroid.distance(centroids.get(set));
                centroids.set(set, newCentroid);
            }
            error /= partition;
            System.out.println("K-Means error: " + error);
            if (error < ERROR_THRESHOLD) {
                break;
            }
        }
        return new ClusterResult(centroids, Arrays.asList(belongs));
    }

}
