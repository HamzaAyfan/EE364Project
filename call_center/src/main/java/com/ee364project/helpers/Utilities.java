package com.ee364project.helpers;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;

import com.ee364project.HasData;
import com.ee364project.Timekeeper;

import net.datafaker.Faker;

/**
 * This class provides various utility functions for the project.
 * 
 * @author Hamza Ayfan
 */

public class Utilities {
    public static Random random = new Random();
    public static Faker faker = new Faker();

    /**
     * This method generates an array of fake objects of the specified class.
     * 
     * @param count the number of objects to generate
     * @param cls   the fully-qualified class name of the objects to generate
     * @return an array of fake objects of the specified class
     */
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

    /**
     * This method generates a string representation of an object using its class
     * name and its attributes.
     * 
     * @param cls   the class name of the object
     * @param attrs the attributes of the object
     * @return a string representation of the object
     */
    public static String prettyToString(String cls, Object... attrs) {
        String accumlate = "";
        for (int i = 0; i < attrs.length; i++) {
            accumlate += "" + attrs[i] + ", ";
        }

        accumlate = accumlate.substring(0, accumlate.length() - 2);
        return cls + "(" + accumlate + ")";
    }

    /**
     * This method validates a phone number.
     * 
     * @param phoneNumber the phone number to validate
     * @return true if the phone number is valid, false otherwise
     */
    public static boolean validatePhone(String phoneNumber) {
        if (phoneNumber.length() == 10 && phoneNumber.startsWith("05")) {
            return true;
        }
        return false;
    }

    /**
     * This method validates an ID.
     * 
     * @param id the ID to validate
     * @return true if the ID is valid, false otherwise
     */
    public static boolean validateId(String id) {
        if (id.length() == 8) {
            return true;
        }
        return false;
    }

    /**
     * This method generates a random LocalDateTime object that is n months in the
     * past.
     * 
     * @param n the number of months in the past
     * @return a random LocalDateTime object that is n months in the past
     */
    public static LocalDateTime getRandomLocalDateTime(int n) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minus(n, ChronoUnit.MONTHS);
        long secondsBetween = ChronoUnit.SECONDS.between(startDate, now);

        return startDate.plus((long) (secondsBetween * random.nextDouble()), ChronoUnit.SECONDS);
    }

    /**
     * This method generates a random LocalDateTime object that is n months in the
     * past.
     * Defaults to n=6.
     */
    public static LocalDateTime getRandLocalDateTime() {
        return getRandomLocalDateTime(6);
    }

    /**
     * This method returns a random object from the specified array.
     * 
     * @param objects the array of objects from which to select a random object
     * @return a random object from the specified array
     */
    public static Object getRandomFromArray(Object[] objects) {
        return objects[random.nextInt(objects.length)];
    }

    /**
     * This method returns a random object from the specified array.
     * 
     */
    public static Object getRandomFromArray(ArrayList<?> objects) {
        return objects.get(random.nextInt(objects.size()));
    }

    /**
     * This method logs an event to the console.
     * 
     * @param subject the subject of the event
     * @param verb    the verb of the event
     * @param object  the object of the event
     * @param msg     the message of the event
     */
    public static void log(Object subject, String verb, Object object, String msg) {
        // System.out.println("\n\n" + Timekeeper.getProperTime() + ": " + subject + " "
        // + verb + " " + object + " | " + msg);
    }

    /**
     * Returns a random array of strings of length between 1 and 19, inclusive. Each
     * string consists of a random number of words from the seniority method of the
     * faker.job object, separated by spaces.
     * 
     * @param len the desired length of the array
     * @return a random array of strings of the specified length
     */
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

    /**
     * Returns a random array of strings of length between 1 and 19, inclusive. Each
     * string consists of a random number of words from the seniority method of the
     * faker.job object, separated by spaces.
     * 
     * @return a random array of strings of the specified length
     */
    public static String[] getRandomStringArray() {
        return getRandomStringArray(random.nextInt(10));
    }

    /**
     * This method joins an array of strings together, separated by a delimiter.
     * 
     * @param strings the array of strings to join
     * @param del     the delimiter to use between strings
     * @return the joined strings
     */
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

    /**
     * This method joins an array of strings together, separated by a delimiter.
     * del=""
     * 
     * @param strings the array of strings to join
     * @return the joined strings
     */
    public static String joinStrings(String[] strings) {
        return joinStrings(strings, "");
    }
}
