package com.ee364project.helpers;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;

import com.ee364project.HasData;
import com.ee364project.Timekeeper;

import net.datafaker.Faker;

public class Utilities {
    public static Random random = new Random();
    public static Faker faker = new Faker();
    public static HasData[] getFakeData(int count, String cls) {
        ArrayList<HasData> objects = new ArrayList<>();
        HasData object;
        try {
            for (int i = 0; i < count; i++) {
                Class<?> dataClass = Class.forName(Vars.projectPrefix + cls);
                object = (HasData) dataClass.getDeclaredConstructor().newInstance();
                object.shuffle();
                objects.add(object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (HasData[]) objects.toArray(new HasData[objects.size()]);
    }

    public static String prettyToString(String cls, Object... attrs) {
        String accumlate = "";
        for (int i = 0; i < attrs.length; i++) {
            accumlate += "" + attrs[i] + ", ";
        }

        accumlate = accumlate.substring(0, accumlate.length() - 2);
        return cls + "(" + accumlate + ")";
    }

    public static boolean validatePhone(String phoneNumber) {
        if (phoneNumber.length() == 10 && phoneNumber.startsWith("05")) {
            return true;
        }
        return false;
    }

    public static boolean validateId(String id) {
        if (id.length() == 8) {
            return true;
        }
        return false;
    }

    public static LocalDateTime getRandomLocalDateTime(int n) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minus(n, ChronoUnit.MONTHS);
        long secondsBetween = ChronoUnit.SECONDS.between(startDate, now);
        
        return startDate.plus((long)(secondsBetween * random.nextDouble()), ChronoUnit.SECONDS);
    }

    public static LocalDateTime getRandLocalDateTime() {
        return getRandomLocalDateTime(6);
    }

    public static Object getRandomFromArray(Object[] objects) {
        return objects[random.nextInt(objects.length)];
    }

    public static Object getRandomFromArray(ArrayList<?> objects) {
        return objects.get(random.nextInt(objects.size()));
    }

    public static void log(Object subject, String verb, Object object, String msg) {
        // System.out.println("\n\n" + Timekeeper.getProperTime() + ": " + subject + " " + verb + " " + object + " | " + msg);
    }

    public static String[] getRandomStringArray(int len) {
        if (len <= 0) {
            len = 1;
        }
        int x;
        String inStr;
        String[] str = new String[len];
        for (int i = 0; i < len; i++) {
            x = random.nextInt(19);
            inStr = "";
            for (int j = 0; j < (1 + x); j++) {
                inStr += faker.job().seniority() + " ";
            }
            str[i] = inStr.strip();
        }
        return str;
    }
// hearthstone().mainCharacter() + " ";
    public static String[] getRandomStringArray() {
        return getRandomStringArray(random.nextInt(10));
    }

    public static String joinStrings(String[] strings, String del) {
        if (strings.length == 0) {
            return "";
        }
        String result = ""; 
        for (int i = 0; i < strings.length - 1; i++) {
            result += (strings[i] + del);
        }
        result += strings[strings.length - 1];
        try {
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
    }

    public static String joinStrings(String[] strings) {
        return joinStrings(strings, "");
    }
}
