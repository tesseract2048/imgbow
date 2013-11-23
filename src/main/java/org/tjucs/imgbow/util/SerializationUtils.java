package org.tjucs.imgbow.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author tess3ract <hty0807@gmail.com>
 */
public class SerializationUtils {

    /* dump object to output file */
    public static void dumpObject(String output, Object obj) throws Exception {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
                output));
        oos.writeObject(obj);
        oos.close();
    }

    /* load object from input file */
    public static Object loadObject(String input) throws Exception {
        ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(input));
        Object cls = ois.readObject();
        ois.close();
        return cls;
    }
}
