package org.tjucs.imgbow.main;

import java.util.List;

import org.tjucs.imgbow.Instance;
import org.tjucs.imgbow.InstanceGenerator;

import weka.classifiers.functions.MultilayerPerceptron;

/**
 * @author tess3ract <hty0807@gmail.com>
 */
public class Train {

    public static void main(String[] args) throws Exception {
        if (args.length < 5) {
            System.out
                    .println("Usage: Train <img_base> <categories> <cate_sample> <output_dict> <output_arff> <output_mlp>");
            return;
        }
        String imgBase = args[0];
        String categories = args[1];
        int cateSample = Integer.valueOf(args[2]);
        String outputDict = args[3];
        String outputArff = args[4];
        String outputMlp = args[5];
        InstanceGenerator instanceGenerator = new InstanceGenerator(
                categories.split(","));
        List<Instance> instances = instanceGenerator.generateInstances(imgBase,
                cateSample, outputDict);
        System.out.println("dumping arff to " + outputArff);
        instanceGenerator.dumpArff(instances, outputArff);
        System.out.println("running cross-validation using MLP");
        String arguments = "-t " + outputArff + " -d " + outputMlp
                + " -L 0.3 -M 0.2 -N 500 -V 0 -S 0 -E 20 -H a";
        MultilayerPerceptron.main(arguments.split(" "));
    }
}
