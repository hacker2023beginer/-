package org.vladproj;

public class LFSR {

    private int register;

    public LFSR(int seed) {
        this.register = seed;
    }

    // возвращает ключевой бит (старший разряд)
    public int nextBit() {

        int output = (register >>> 31) & 1; // старший разряд

        // вычисляем новый бит по многочлену x^32 + x^28 + x^27 + x + 1
        int newBit = ((register >>> 31) ^
                (register >>> 27) ^
                (register >>> 26) ^
                (register >>> 0)) & 1;

        // сдвиг влево
        register = (register << 1) | newBit;

        return output;
    }

    // возвращает байт из 8 ключевых битов
    public byte nextByte() {

        int value = 0;

        for (int i = 0; i < 8; i++)
            value = (value << 1) | nextBit();

        return (byte) value;
    }
}