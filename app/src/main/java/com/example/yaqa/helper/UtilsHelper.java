package com.example.yaqa.helper;

public class UtilsHelper {
    public static int parseTime(String input) {
        String[] units = input.split(":"); //will break the string up into an array
        int minutes = Integer.parseInt(units[0]); //first element
        int seconds = Integer.parseInt(units[1]); //second element
        int duration = 60 * minutes + seconds; //add up our values
        return duration;
    }

}
