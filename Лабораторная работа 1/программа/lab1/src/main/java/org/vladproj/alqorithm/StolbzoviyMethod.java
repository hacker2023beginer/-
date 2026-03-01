package org.vladproj.alqorithm;

import org.vladproj.validator.KeyValidator;

public class StolbzoviyMethod {
    private final KeyValidator keyValidator = new KeyValidator();
    private Character[][] textTable;
    private String key;

    public StolbzoviyMethod(Character[][] textTable, String key) {
        this.textTable = textTable;
        String newKey = keyValidator.validateEn(key);
        if (newKey.length() == 0) {
            throw new IllegalArgumentException("Ключ не содержит допустимых английских букв");
        }
        this.key = newKey;
    }

    public String encode(String text) {
        int stringIndex = 0;
        int tableRowIndex = 0;
        while (stringIndex < text.length()) {
            for (int j = 0; j < textTable[0].length; j++) {
                if (stringIndex == text.length()){
                    textTable[tableRowIndex][j] = null;
                    continue;
                }
                textTable[tableRowIndex][j] = text.charAt(stringIndex);
                stringIndex++;
            }
            tableRowIndex++;
        }
        int[] order = keyOrder();

        int priorityValue = 1;
        StringBuilder result = new StringBuilder();
        while (priorityValue <= order.length){
            int tableColumn = 0;
            for (int i = 0; i < order.length; i++){
                if (priorityValue == order[i]){
                    tableColumn = i;
                    break;
                }
            }
            for (int i = 0; i < textTable.length; i++){
                if (textTable[i][tableColumn] != null){
                    result.append(textTable[i][tableColumn]);
                }
            }
            priorityValue++;
        }
        return result.toString();
    }

    public String decode(String cipher) {
        int cols = key.length();
        int rows = textTable.length;

        int[] order = keyOrder();

        Character[][] table = new Character[rows][cols];

        int cipherIndex = 0;

        for (int priority = 1; priority <= cols; priority++) {
            int col = 0;
            for (int i = 0; i < cols; i++) {
                if (order[i] == priority) {
                    col = i;
                    break;
                }
            }

            for (int r = 0; r < rows; r++) {
                if (cipherIndex < cipher.length()) {
                    table[r][col] = cipher.charAt(cipherIndex++);
                } else {
                    table[r][col] = null;
                }
            }
        }

        StringBuilder result = new StringBuilder();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (table[r][c] != null) {
                    result.append(table[r][c]);
                }
            }
        }

        return result.toString();
    }


    private int[] keyOrder(){
        int orderPriority = 1;
        int[] order = new int[key.length()];
        for (int i = 0; i < order.length; i++){
            order[i] = 0;
        }

        for (int i = 0; i < order.length; i++){
            int keyIndex = 0;
            int orderIndex = 0;
            char currentMin = Character.MAX_VALUE;
            while(keyIndex < key.length()){
                char currentChar = key.charAt(keyIndex);
                if (currentMin > currentChar && order[keyIndex] == 0){
                    currentMin = currentChar;
                    orderIndex = keyIndex;
                }
                keyIndex++;
            }
            order[orderIndex] = orderPriority;
            orderPriority++;
        }
        return order;
    }
}
