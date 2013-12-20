package org.tjucs.imgbow.main;

import java.util.List;

import org.tjucs.imgbow.Feature;
import org.tjucs.imgbow.Instance;
import org.tjucs.imgbow.InstanceGenerator;
import org.tjucs.imgbow.Model;
import org.tjucs.imgbow.feature.FeatureMaker;
import org.tjucs.imgbow.feature.SIFTFeatureMaker;
import org.tjucs.imgbow.util.ClassifyUtils;
import org.tjucs.imgbow.util.SerializationUtils;

import weka.classifiers.Classifier;

/**
 * 分类程序入口
 * 
 * @author tess3ract <hty0807@gmail.com>
 */
public class Classify {

    private static FeatureMaker featureMaker = new SIFTFeatureMaker();

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: Classify <input_image> <input_model>");
            return;
        }
        String inputImg = args[0];
        String inputModel = args[1];
        Model model = (Model) SerializationUtils.loadObject(inputModel);
        Classifier classifier = model.getClassifier();
        String[] categories = model.getCategories();

        InstanceGenerator instanceGenerator = new InstanceGenerator();
        List<Feature> features = featureMaker.getFeatures(inputImg);
        Instance instance = instanceGenerator.getInstance(features,
                model.getWords());

        double value = classifier.classifyInstance(ClassifyUtils
                .getWekaInstance(instance));
        String category = categories[(int) value];
        System.out.println(category);
    }

}
