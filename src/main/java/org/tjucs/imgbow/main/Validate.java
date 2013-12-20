package org.tjucs.imgbow.main;

import java.util.List;

import org.tjucs.imgbow.Feature;
import org.tjucs.imgbow.Instance;
import org.tjucs.imgbow.InstanceGenerator;
import org.tjucs.imgbow.Model;
import org.tjucs.imgbow.Sample;
import org.tjucs.imgbow.feature.FeatureMaker;
import org.tjucs.imgbow.feature.SIFTFeatureMaker;
import org.tjucs.imgbow.util.ClassifyUtils;
import org.tjucs.imgbow.util.SerializationUtils;

import weka.classifiers.Classifier;

/**
 * 评估程序入口
 * 
 * @author tess3ract <hty0807@gmail.com>
 */
public class Validate {

    private static InstanceGenerator instanceGenerator;

    private static FeatureMaker featureMaker = new SIFTFeatureMaker();

    private static Classifier classifier;

    private static Model model;

    private static String getCategory(String[] categories, String image)
            throws Exception {
        List<Feature> features = featureMaker.getFeatures(image);
        Instance instance = instanceGenerator.getInstance(features,
                model.getWords());
        double value = classifier.classifyInstance(ClassifyUtils
                .getWekaInstance(instance));
        return categories[(int) value];
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 5) {
            System.out
                    .println("Usage: Validate <img_base> <validate_categories> <start> <end> <input_model>");
            return;
        }
        String imgBase = args[0];
        String[] validateCategories = args[1].split(",");
        int start = Integer.valueOf(args[2]);
        int end = Integer.valueOf(args[3]);
        String inputModel = args[4];
        instanceGenerator = new InstanceGenerator(validateCategories);
        model = (Model) SerializationUtils.loadObject(inputModel);
        classifier = model.getClassifier();
        List<Sample> samples = instanceGenerator
                .getSamples(imgBase, start, end);
        int correct = 0;
        for (Sample sample: samples) {
            String category = getCategory(model.getCategories(),
                    sample.getPath());
            if (sample.getCategory().equals(category)) {
                correct++;
                System.out.println("POSITIVE: " + sample.getPath());
            } else {
                System.out.println("NEGATIVE: " + sample.getPath()
                        + " -> false " + category);
            }
        }
        System.out.println("Precision: " + ((double) correct / samples.size()));
    }

}
