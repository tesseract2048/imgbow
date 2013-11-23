package org.tjucs.imgbow.feature;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.tjucs.imgbow.Feature;

import com.alibaba.simpleimage.analyze.sift.IPixelConverter;
import com.alibaba.simpleimage.analyze.sift.ImagePixelArray;
import com.alibaba.simpleimage.analyze.sift.SIFT;
import com.alibaba.simpleimage.analyze.sift.render.RenderImage;
import com.alibaba.simpleimage.analyze.sift.scale.KDFeaturePoint;

/**
 * @author tess3ract <hty0807@gmail.com>
 */
public class SIFTFeatureMaker implements FeatureMaker {

    private ImagePixelArray pixels;

    private SIFT sift;

    private IPixelConverter getPixelConverter() {
        return new IPixelConverter() {
            public float convert(int r, int g, int b) {
                return (float)(r + g + b) / 255 / 3;
            }
        };
    }

    public List<Feature> getFeatures(String img) throws IOException {
        BufferedImage in = ImageIO.read(new File(img));
        RenderImage rImg = new RenderImage(in);
        pixels = rImg.toPixelFloatArray(getPixelConverter());
        sift = new SIFT();
        sift.detectFeatures(pixels);
        List<Feature> features = new ArrayList<Feature>();
        for (KDFeaturePoint point: sift.getGlobalKDFeaturePoints()) {
            features.add(new Feature(point));
        }
        return features;
    }

}
