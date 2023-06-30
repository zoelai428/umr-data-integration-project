package org.example.Data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class Report {

    public Integer id;
    public Date date;
    public String author;
    public String headline;

    public String description;

    public boolean doesExist() {
        return date != null;
    }

    public void setValueForAttribute(String value, String attribute) throws ParseException {

        switch (attribute) {
            case "date" -> date = parseTime(value);
            case "author" -> author = value;
            case "headline" -> headline = value;
            case "description" -> description = value;
        }

    }

    public static Date parseTime(String value) throws ParseException {

        SimpleDateFormat dateFormat;
        Date date = null;

        if(matchesTimePattern1(value)) {
            //2000-06-16T12:00:00Z
            dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            date = dateFormat.parse(value);

        } else if(matchesTimePattern2(value)) {
            //1974-09-20
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            date = dateFormat.parse(value);

        } else if(matchesTimePattern3(value)) {
            //2000-06-16 12:00:00
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = dateFormat.parse(value);

        } else if(matchesTimePattern4(value)) {
            //2019-06-23T18:53:00
            dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            date = dateFormat.parse(value);

        } else if(matchesTimePattern5(value)) {
            //10/10/1949 20:30
            dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
            date = dateFormat.parse(value);

        } else if(matchesTimePattern6(value)) {
            //5/15/21 22:36
            dateFormat = new SimpleDateFormat("M/d/yy HH:mm");
            date = dateFormat.parse(value);
        }

        return date;
    }

    //2000-06-16T12:00:00Z
    private static boolean matchesTimePattern1(String value) {
        String pattern = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}Z$";
        return Pattern.matches(pattern, value);
    }

    //1974-09-20
    private static boolean matchesTimePattern2(String value) {
        String pattern = "^\\d{4}-\\d{2}-\\d{2}$";
        return Pattern.matches(pattern, value);
    }
    //2000-06-16 12:00:00
    private static boolean matchesTimePattern3(String value) {
        String pattern = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$";
        return Pattern.matches(pattern, value);
    }

    //2019-06-23T18:53:00
    private static boolean matchesTimePattern4(String value) {
        String pattern = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$";
        return Pattern.matches(pattern, value);
    }

    //10/10/1949 20:30
    private static boolean matchesTimePattern5(String value) {
        String pattern = "^\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}$";
        return Pattern.matches(pattern, value);
    }

    //5/15/21 22:36
    private static boolean matchesTimePattern6(String value) {
        String pattern = "^\\d{1,2}/\\d{1,2}/\\d{2} \\d{2}:\\d{2}$";
        return Pattern.matches(pattern, value);
    }

    public static void main(String[] args) throws ParseException {

        Date date1 = parseTime("2000-06-16T12:00:00Z");
        System.out.println(date1);

        Date date2 = parseTime("1974-09-20");
        System.out.println(date2);

        Date date3 = parseTime("2000-06-16 12:00:00");
        System.out.println(date3);

        Date date4 = parseTime("2019-06-23T18:53:00");
        System.out.println(date4);

        Date date5 = parseTime("10/10/1949 20:30");
        System.out.println(date5);

        Date date6 = parseTime("15/12/69 22:36");
        System.out.println(date6);
    }
}
