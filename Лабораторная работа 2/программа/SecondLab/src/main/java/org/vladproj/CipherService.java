package org.vladproj;

import java.io.*;

public class CipherService {

    public static byte[] process(File input, File output, int seed,
                                 StringBuilder keyStream) throws Exception {

        LFSR lfsr = new LFSR(seed);

        FileInputStream fis = new FileInputStream(input);
        FileOutputStream fos = new FileOutputStream(output);

        ByteArrayOutputStream originalBytes = new ByteArrayOutputStream();

        int b;

        while ((b = fis.read()) != -1) {

            byte key = lfsr.nextByte();

            for (int i = 7; i >= 0; i--)
                keyStream.append((key >> i) & 1);

            byte encrypted = (byte) (b ^ key);

            originalBytes.write(b);
            fos.write(encrypted);
        }

        fis.close();
        fos.close();

        return originalBytes.toByteArray();
    }
}