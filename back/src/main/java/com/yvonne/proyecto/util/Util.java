package com.yvonne.proyecto.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class Util {

    public static String randomString(Integer stringLenght){
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ01234566789";
        StringBuilder random = new StringBuilder();
        for (int i = 0; i < stringLenght; i++) {
            Random r = new Random();
            int number = r.nextInt(chars.length()+1);
            random.append(chars.charAt(number));
        }

        return random.toString();
    }
}
