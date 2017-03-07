package com.sqsong.videosample.util;

import java.util.Formatter;

/**
 * Created by 青松 on 2017/3/7.
 */

public class VideoUtils {

    public static String formatTime(Formatter formatter, StringBuilder formatBuilder, String time) {
        try {
            int mill = Integer.parseInt(time);
            int totalSeconds = mill / 1000;
            int seconds = totalSeconds % 60;
            int minutes = (totalSeconds / 60) % 60;
            int hours   = totalSeconds / 3600;
            formatBuilder.setLength(0);
            if (hours > 0) {
                return formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
            } else {
                return formatter.format("%02d:%02d", minutes, seconds).toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }

}
