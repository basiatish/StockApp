package com.example.stocks.utils.formatters;

import android.text.Html;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class Formatter {

    public String formattedValue(Double value) {
        String string = "";
        if (value > 1000.0 && value < 1000000.0) string = Math.round(value / 10.0) / 100.0 + "K";
        else if (value > 1000000.0 && value < 1000000000.0)
            string = Math.round(value / 10000.0) / 100.0 + "M";
        else if (value > 1000000000.0 && value < 1000000000000.0)
            string = Math.round(value / 10000000.0) / 100.0 + "B";
        else if (value > 1000000000000.0 && value < 1000000000000000.0)
            string = Math.round(value / 10000000000.0) / 100.0 + "T";
        else if (value == 0.0) string = "—";
        else string = Double.toString(value);
        return string;
    }

    public String[] formattedRange(String range) {
        int i = 0;
        StringBuilder first_part = new StringBuilder();
        StringBuilder second_part = new StringBuilder();
        String[] result = new String[2];
        boolean flag = false;
        while (i <= range.length() - 1) {
            if (range.charAt(i) == '-') {
                flag = true;
                i++;
            }
            if (!flag) first_part.append(range.charAt(i));
            else second_part.append(range.charAt(i));
            i++;
        }
        result[0] = first_part.toString();
        result[1] = second_part.toString();
        return result;
    }

    public String cutZeros(Double value) {
        if (value == 0.0) return "—";
        else return Double.toString(Math.round(value * 100.0) / 100.0);
    }

    public String parseTicker(String str) {
        if (str.length() > 15) return "-";
        StringBuilder res = new StringBuilder();
        int position = str.length();
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == ':') {
                position = i;
            }
            if (i > position) {
                res.append(str.charAt(i));
            }
        }
        if (position == str.length()) return str;
        else return res.toString();
    }

    public String parseDate(String str) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime date = LocalDateTime.parse(str, format);
        ZoneId oldZone = ZoneId.of("America/New_York");
        ZoneId localZone = ZoneId.systemDefault();
        ZonedDateTime serverDate = date.atZone(oldZone);
        ZonedDateTime localDate = serverDate.withZoneSameInstant(localZone);
        long currentTimeMills = Instant.now().toEpochMilli();
        long millsLocal = localDate.toInstant().toEpochMilli();
        long delta = currentTimeMills - millsLocal;
        int days = (int) TimeUnit.MILLISECONDS.toDays(delta);
        int hours = (int) (TimeUnit.MILLISECONDS.toHours(delta) - days * 24L);
        int mins = (int) (TimeUnit.MILLISECONDS.toMinutes(delta) - TimeUnit.MILLISECONDS.toHours(delta) * 60);
        int weeks = days / 7;
        int months = weeks / 4;
        int years = months / 12;
        String res = "";
        if (years == 1) {
            res = years + " Year Ago";
        } else if (years > 1) {
            res = years + " Years Ago";
        } else if (months == 1) {
            res = months + " Month Ago";
        } else if (months > 1 && months < 12) {
            res = months + " Months Ago";
        } else if (weeks == 1) {
            res = weeks + " Week Ago";
        } else if (weeks > 1 && weeks < 4) {
            res = weeks + " Weeks Ago";
        } else if (days == 1) {
            res = days + " Day Ago";
        } else if (days > 1 && days < 7) {
            res = days + " Days Ago";
        } else if (hours == 1) {
            res = hours + " Hour Ago";
        } else if (hours > 1 && hours < 24) {
            res = hours + " Hours Ago";
        } else if (mins == 1) {
            res = mins + " Minute Ago";
        } else if (mins > 1 && mins < 60) {
            res = mins + " Minutes Ago";
        } else {
            res = "Seconds Ago";
        }

        return res;
    }

    public String formatHtml(String str) {
        return Html.fromHtml(str, Html.FROM_HTML_MODE_LEGACY).toString();
    }
}
