package org.vladproj.alqorithm;

import org.vladproj.validator.KeyValidator;

public class Vizhener {
    private final KeyValidator keyValidator = new KeyValidator();
    private static final String ALPHABET = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
    private String key;

    public Vizhener(String key) {
        String newKey = keyValidator.validateRu(key);
        if (newKey.length() == 0){
            throw new IllegalArgumentException("Ключ не содержит допустимых русских букв");
        }
        this.key = newKey.toUpperCase();
    }

    public String encode(String text) {
        String normalizedText = text.toUpperCase();
        Character[] realKey = keyToArray(normalizedText.length());
        StringBuilder result = new StringBuilder();
        int alphabetSize = ALPHABET.length();

        int realKeyIndex = 0;
        for (int i = 0; i < normalizedText.length(); i++) {
            char ch = normalizedText.charAt(i);
            int textIndex = ALPHABET.indexOf(ch);
            if (textIndex == -1) {
                result.append(ch);
                continue;
            }
            int keyIndex = ALPHABET.indexOf(realKey[realKeyIndex]);
            int cipherIndex = (textIndex + keyIndex) % alphabetSize;

            result.append(ALPHABET.charAt(cipherIndex));
            realKeyIndex++;
        }

        return result.toString();
    }

    public String decode(String text) {
        String normalizedText = text.toUpperCase();
        Character[] realKey = keyToArray(normalizedText.length());
        StringBuilder result = new StringBuilder();
        int alphabetSize = ALPHABET.length();

        int realKeyIndex = 0;
        for (int i = 0; i < normalizedText.length(); i++) {
            char ch = normalizedText.charAt(i);
            int cipherIndex = ALPHABET.indexOf(ch);

            if (cipherIndex == -1) {
                result.append(ch);
                continue;
            }

            int keyIndex = ALPHABET.indexOf(realKey[realKeyIndex]);
            int plainIndex = (cipherIndex - keyIndex + alphabetSize) % alphabetSize;

            result.append(ALPHABET.charAt(plainIndex));
            realKeyIndex++;
        }

        return result.toString();
    }


    private Character[] keyToArray(int size) {
        Character[] keyArray = new Character[size];

        int keyLen = key.length();
        int alphabetSize = ALPHABET.length();

        for (int i = 0; i < size; i++) {
            int baseIndex = i % keyLen;
            int cycle = i / keyLen;

            char baseChar = key.charAt(baseIndex);
            int pos = ALPHABET.indexOf(baseChar);

            int shifted = (pos + cycle) % alphabetSize;
            keyArray[i] = ALPHABET.charAt(shifted);
        }

        return keyArray;
    }
}
