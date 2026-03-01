package org.vladproj.validator;

public class KeyValidator {
    private static final String ALPHABET_RU = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
    private static final String ALPHABET_EN = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public String validateRu(String key){
        String upper = key.toUpperCase();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < upper.length(); i++){
            if (ALPHABET_RU.indexOf(upper.charAt(i)) != -1){
                result.append(upper.charAt(i));
            }
        }
        return result.toString();
    }

    public String validateEn(String key){
        String upper = key.toUpperCase();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < upper.length(); i++){
            if (ALPHABET_EN.indexOf(upper.charAt(i)) != -1){
                result.append(upper.charAt(i));
            }
        }
        return result.toString();
    }
}

