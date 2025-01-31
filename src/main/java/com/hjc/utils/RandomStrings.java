package com.hjc.utils;

import java.util.Random;

public class RandomStrings {
    private static final char[] symbols;
    static{
        StringBuilder tmp= new StringBuilder();
        for (char ch='0';ch<='9';++ch)
            tmp.append(ch);
        for(char ch = 'a';ch<='z';++ch)
            tmp.append(ch);
        symbols  = tmp.toString().toCharArray();
    }

    private static final Random random = new Random();
    public static String nextString(int length){
        char[] buf = new char[length];
        for (int idx = 0; idx < buf.length; ++idx) {
            buf[idx] = symbols[random.nextInt(symbols.length)];
        }
        return new String(buf);
    }
}
