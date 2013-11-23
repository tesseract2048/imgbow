package org.tjucs.imgbow;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

import com.alibaba.simpleimage.analyze.sift.scale.KDFeaturePoint;

/**
 * @author tess3ract <hty0807@gmail.com>
 */
public class Feature implements Serializable {

    private static final long serialVersionUID = -4432933599973324376L;

    public static final int DIMENSION = 128;

    private int[] values;

    public Feature() {

    }

    public Feature(KDFeaturePoint point) {
        setValues(point.descriptor);
    }

    public Feature(boolean isRandom) {
        values = new int[DIMENSION];
        if (isRandom) {
            Random random = new Random();
            for (int i = 0; i < DIMENSION; i++) {
                values[i] = random.nextInt(255);
            }
        }
    }

    /**
     * @return the values
     */
    public int[] getValues() {
        return values;
    }

    /**
     * @param values
     *            the values to set
     */
    public void setValues(int[] values) {
        this.values = values;
    }

    public double distance(Feature x) {
        int[] values2 = x.getValues();
        if (values == null || values2 == null) {
            return Double.NaN;
        }
        double tmp = 0.0f;
        for (int i = 0; i < DIMENSION; i++) {
            tmp += Math.pow(values[i] - values2[i], 2);
        }
        return Math.sqrt(tmp);
    }

    public void add(Feature x) {
        int[] values2 = x.getValues();
        if (values == null || values2 == null) {
            return;
        }
        for (int i = 0; i < DIMENSION; i++) {
            values[i] += values2[i];
        }
    }

    public void divide(int x) {
        if (values == null) {
            return;
        }
        for (int i = 0; i < DIMENSION; i++) {
            values[i] /= x;
        }
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Feature [values=" + Arrays.toString(values) + "]";
    }
}
