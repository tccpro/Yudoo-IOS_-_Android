package jereme.urban_network_dating.Utils;

import android.content.Context;

import jereme.urban_network_dating.R;

public class UnixToHuman {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    private static final int WEEK_MILLIS = 7 * DAY_MILLIS ;

    public static String getTimeAgo(long time, Context context) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now =System.currentTimeMillis();;


        long diff = now - time;
        if(diff>0) {


            if (diff < MINUTE_MILLIS) {
                return context.getResources().getString(R.string.justnow);
            } else if (diff < 2 * MINUTE_MILLIS) {
                return context.getResources().getString(R.string.minuteago);
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + context.getResources().getString(R.string.minuteago);
            } else if (diff < 90 * MINUTE_MILLIS) {
                return context.getResources().getString(R.string.anhourago);
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + context.getResources().getString(R.string.hourago);
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else if (diff < 7 * DAY_MILLIS) {
                return diff / DAY_MILLIS + context.getResources().getString(R.string.daysago);
            } else if (diff < 2 * WEEK_MILLIS) {
                return context.getResources().getString(R.string.aweekago);
            } else if (diff < WEEK_MILLIS * 3) {
                return diff / WEEK_MILLIS + context.getResources().getString(R.string.weeksago);
            } else {
                java.util.Date date = new java.util.Date((long) time);
                return date.toString();
            }

        }
        else {

            diff=time-now;
            if (diff < MINUTE_MILLIS) {
                return context.getResources().getString(R.string.thisminute);
            } else if (diff < 2 * MINUTE_MILLIS) {
                return context.getResources().getString(R.string.aminutelater);
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " " + context.getResources().getString(R.string.minuteslater);
            } else if (diff < 90 * MINUTE_MILLIS) {
                return context.getResources().getString(R.string.anhourlater);
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " "+context.getResources().getString(R.string.hourslater);
            } else if (diff < 48 * HOUR_MILLIS) {
                return context.getResources().getString(R.string.tomorrow);
            } else if (diff < 7 * DAY_MILLIS) {
                return diff / DAY_MILLIS + " days later";
            } else if (diff < 2 * WEEK_MILLIS) {
                return context.getResources().getString(R.string.aweeklater);
            } else if (diff < WEEK_MILLIS * 3) {
                return diff / WEEK_MILLIS + " "+context.getResources().getString(R.string.weeklater);
            } else {
                java.util.Date date = new java.util.Date((long) time);
                return date.toString();
            }
        }

    }
}
