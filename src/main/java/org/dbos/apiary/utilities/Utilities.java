package org.dbos.apiary.utilities;

import org.dbos.apiary.interposition.ApiaryFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.voltdb.VoltTable;
import org.voltdb.VoltType;

import java.io.*;
import java.util.ArrayList;

public class Utilities {

    private static final Logger logger = LoggerFactory.getLogger(Utilities.class);

    public static byte[] objectToByteArray(Serializable obj) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(obj);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Serialization Failed {} {}", obj, e);
            assert(false);
        }
        return bos.toByteArray();
    }

    public static Object byteArrayToObject(byte[] b) {
        ByteArrayInputStream bis = new ByteArrayInputStream(b);
        Object obj = null;
        try {
            ObjectInput in = new ObjectInputStream(bis);
            obj = in.readObject();
            in.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            logger.error("Deserialization Failed {} {}", b, e);
            assert(false);
        }
        return obj;
    }

    public static byte[] toByteArray(int value) {
        return new byte[] {
                (byte)(value >> 24),
                (byte)(value >> 16),
                (byte)(value >> 8),
                (byte)value };
    }

    public static int fromByteArray(byte[] bytes) {
        return ((bytes[0] & 0xFF) << 24) |
                ((bytes[1] & 0xFF) << 16) |
                ((bytes[2] & 0xFF) << 8 ) |
                ((bytes[3] & 0xFF));
    }

    public static byte[] stringArraytoByteArray(String[] strs) {
        int totalLen = 0;
        for (String s: strs) {
            totalLen += s.getBytes().length + 4;
        }
        byte[] bytes = new byte[totalLen];
        int i = 0;
        for (String str: strs) {
            int len = str.getBytes().length;
            byte[] lenArray = toByteArray(len);
            System.arraycopy(lenArray, 0, bytes, i, 4);
            byte[] strArray = str.getBytes();
            System.arraycopy(strArray, 0, bytes, i + 4, len);
            i += len + 4;
        }
        return bytes;
    }

    public static String[] byteArrayToStringArray(byte[] bytes) {
        ArrayList<String> strList = new ArrayList<>();
        for (int i = 0; i < bytes.length;) {
            byte[] lenArray = new byte[4];
            System.arraycopy(bytes, i, lenArray, 0, 4);
            int len = fromByteArray(lenArray);
            byte[] strArray = new byte[len];
            System.arraycopy(bytes, i + 4, strArray, 0, len);
            strList.add(new String(strArray));
            i += len + 4;
        }
        return strList.toArray(new String[0]);
    }

    public static VoltTable.ColumnInfo objectToColumnInfo(int index, Object input) {
        if (input instanceof Integer) {
            return new VoltTable.ColumnInfo(Integer.toString(index), VoltType.BIGINT);
        } else if (input instanceof Double) {
            return new VoltTable.ColumnInfo(Integer.toString(index), VoltType.FLOAT);
        } else if (input instanceof String) {
            return new VoltTable.ColumnInfo(Integer.toString(index), VoltType.STRING);
        } else if (input instanceof String[]) {
            return new VoltTable.ColumnInfo(Integer.toString(index), VoltType.VARBINARY);
        } else if (input instanceof ApiaryFuture) {
            return new VoltTable.ColumnInfo(Integer.toString(index), VoltType.SMALLINT);
        }
        return null;
    }

}
