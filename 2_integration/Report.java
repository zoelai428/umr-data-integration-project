package org.example;

import java.util.Date;

public class Report {

    int ID;
    String date;
    String author;
    String headline;
    String description;

    public boolean doesExist() {
        return date != null;
    }

    public void setValueForAttribute(String value, String attribute) {

        switch (attribute) {
            case "date" -> date = value;
            case "author" -> author = value;
            case "headline" -> headline = value;
            case "description" -> description = value;
        }

    }
}
