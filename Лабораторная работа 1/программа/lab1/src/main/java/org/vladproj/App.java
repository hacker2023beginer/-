package org.vladproj;

import org.vladproj.alqorithm.StolbzoviyMethod;
import org.vladproj.alqorithm.Vizhener;

import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Scanner sc = new Scanner(System.in);
        String key = sc.nextLine();
        String text = sc.nextLine();
        StringBuilder realText = new StringBuilder();
        for (int i = 0; i < text.length(); i++){
            if (text.charAt(i) == ' ') continue;
            realText.append(text.charAt(i));
        }
        text = realText.toString();
        int ost = text.length() % key.length() == 0 ? 0 : 1;
        int size = text.length() / key.length() + ost;
        Character[][] table = new Character[size][key.length()];
        StolbzoviyMethod method = new StolbzoviyMethod(table, key);
        String shifr = method.encode(text);
        System.out.println(shifr);
        String key2 = sc.nextLine();
        String text2 = sc.nextLine();
        Vizhener vizhener = new Vizhener(key2);
        String r = vizhener.encode(text2);
        System.out.println(r);
    }
}
