package com.example.stocks.utils.formatters;

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
}
