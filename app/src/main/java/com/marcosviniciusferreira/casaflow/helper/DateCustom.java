package com.marcosviniciusferreira.casaflow.helper;

import android.util.Log;

import java.text.SimpleDateFormat;

public class DateCustom {

    public static String actualDate() {
        long dateInMilliseconds = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String date = sdf.format(dateInMilliseconds);
        return date;

    }

    public static String monthYearChosenDate(String chosenDate) {

        String[] date = chosenDate.split("/");
        String day = date[0];
        String month = date[1];
        String year = date[2];

        String monthYear = month + year;

        Log.i("date =====>>>>", date.toString());
        Log.i("monthYearChosenDate =====>>>>", monthYear);

        return monthYear;

    }
}
