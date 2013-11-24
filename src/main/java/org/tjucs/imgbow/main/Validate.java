package org.tjucs.imgbow.main;

import java.util.List;

import org.tjucs.imgbow.Dict;
import org.tjucs.imgbow.Feature;
import org.tjucs.imgbow.Instance;
import org.tjucs.imgbow.InstanceGenerator;
import org.tjucs.imgbow.Sample;
import org.tjucs.imgbow.feature.FeatureMaker;
import org.tjucs.imgbow.feature.SIFTFeatureMaker;
import org.tjucs.imgbow.util.SerializationUtils;

import weka.classifiers.Classifier;
import weka.core.SparseInstance;

/**
 * @author tess3ract <hty0807@gmail.com>
 */
public class Validate {

    private static InstanceGenerator instanceGenerator;

    private static FeatureMaker featureMaker = new SIFTFeatureMaker();

    private static Classifier classifier;

    private static Dict dict;

    private static Classifier loadClassifier(String input) throws Exception {
        return (Classifier) SerializationUtils.loadObject(input);
    }

    private static weka.core.Instance getWekaInstance(Instance instance) {
        SparseInstance spi = new SparseInstance(0.0f, instance.getFreq());
        return spi;
    }

    private static String getCategory(String[] categories, String image)
            throws Exception {
        List<Feature> features = featureMaker.getFeatures(image);
        Instance instance = instanceGenerator.getInstance(features, dict);
        double value = classifier.classifyInstance(getWekaInstance(instance));
        return categories[(int) value];
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 6) {
            System.out
                    .println("Usage: Validate <img_base> <validate_categories> <start> <end> <input_dict> <input_mlp>");
            return;
        }
        String imgBase = args[0];
        String[] categories = args[1].split(",");
        int start = Integer.valueOf(args[2]);
        int end = Integer.valueOf(args[3]);
        String inputDict = args[4];
        String inputMlp = args[5];
        instanceGenerator = new InstanceGenerator(categories);
        dict = (Dict) SerializationUtils.loadObject(inputDict);
        classifier = loadClassifier(inputMlp);
        List<Sample> samples = instanceGenerator
                .getSamples(imgBase, start, end);
        int correct = 0;
        for (Sample sample: samples) {
            String category = getCategory(categories, sample.getPath());
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
