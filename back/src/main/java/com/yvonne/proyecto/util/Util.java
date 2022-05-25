package com.yvonne.proyecto.util;

import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@Component
public class Util {

    public static String randomString(Integer stringLenght) {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ01234566789";
        StringBuilder random = new StringBuilder();
        for (int i = 0; i < stringLenght; i++) {
            Random r = new Random();
            int number = r.nextInt(chars.length());
            random.append(chars.charAt(number));
        }

        return random.toString();
    }

    public static String generateRandomDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Date date = new Date();

        return dateFormat.format(date);
    }
}
