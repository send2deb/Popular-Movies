package com.debdroid.popularmovies.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by debashispaul on 23/02/2018.
 */

public class DateUtils {

    /**
     * This utility method formats the date for friendly user display
     * @param date Date to be formatted
     * @return Formatted date value in DD MMM YYYY format
     */
    public static String formatFriendlyDate(final String date) {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy");
        if (date.length() == 10) {
            if (date.charAt(4) == '-' && date.charAt(7) == '-') {
                final String dateString;
                try {
                    dateString = simpleDateFormat.format
                            (new SimpleDateFormat("yyyy-MM-dd").parse(date));
                    return dateString;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return date;
    }
}
