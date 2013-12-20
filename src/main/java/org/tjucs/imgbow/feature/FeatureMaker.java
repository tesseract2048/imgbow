package org.tjucs.imgbow.feature;

import java.io.IOException;
import java.util.List;

import org.tjucs.imgbow.Feature;

/**
 * 特征提取器接口描述
 * 
 * @author tess3ract <hty0807@gmail.com>
 */
public interface FeatureMaker {

    /* generate features from specified img */
    public List<Feature> getFeatures(String img) throws IOException;

}
