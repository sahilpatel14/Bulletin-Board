package in.sahilpatel.bulletinboard.support;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public final class TimestampConversion {

    /**
     * The class should be converting a String or long or double
     * representing a timestamp in milliseconds into a more human readable form.
     * The types converted would be immutable. i.e. Converting back to milliseconds would not
     * be possible.
     */

    public static final String TAG = TimestampConversion.class.getSimpleName();
    private static String datePattern = "dd/MM/yyyy HH:mm:ss.SSS";      //  Standard pattern for every date
    private static SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
    private static Calendar calendar = Calendar.getInstance();          //  For use in code.

    /**
     *
     * @param timestamp, the time in milliseconds
     * @return  String representing timestamp in a more human readable form.
     * if current data and time is 26/8/2016 10:16:00 AM,
     * converted timestamp value                                                equivalent converted readable value
     *
     * 25/8/2016 12:01:00 AM < value < 25/8/2016 11:59:PM                               yesterday
     * 26/7/2015  12:00:00 PM < value < 25/8/2016 12:00:00                              few days ago
     * value < 26/7/2015  12:00:00 PM                                                   months ago
     *
     * 26/8/2016 10:16:30 < value < 26/8/2016 10:16:00 AM                             just now   (30 seconds)
     * 26/8/2016 10:17:00 < value < 26/8/2016 10:16:31 AM                             seconds now   (60 seconds)
     * 26/8/2016 11:16:00 < value < 26/8/2016 10:17:01 AM                             x minutes now   (for 59 minutes)
     * 27/8/2016 11:16:00 < value < 26/8/2016 11:17:00 AM                             x hours now   (minutes)
     *
     */
    public static final String getReadableTimestamp(long timestamp){

        /**
         * Would store upper and lower range
         */
        long upperRange,lowerRange,curTime;

        curTime = System.currentTimeMillis();
        upperRange = goBack(curTime,1,Calendar.MONTH);        //  last month same date.
        //  Checking if the timestamp is more than 1 month old

        if(timestamp < upperRange){
            return getTimeDifference(timestamp,curTime,Calendar.MONTH)+" months ago";
        }


            lowerRange = yesterdaysDateAt12();                      //  yesterday
            upperRange = goBack(lowerRange,-24,Calendar.HOUR_OF_DAY);                                 //  today
            if(lowerRange <= timestamp && timestamp < upperRange ) {
                return "yesterday";
            }

        //  Checking if the timestamp is less than 1 month old
        lowerRange = goBack(curTime,1,Calendar.MONTH);        //  last month same date
        upperRange = goBack(curTime,1,Calendar.DATE);         //  yesterday
        if(lowerRange <= timestamp && timestamp < upperRange ) {
            return getTimeDifference(timestamp,curTime,Calendar.DATE) + " days ago";
        }
        //  Checking if the timestamp is 1 day old


        //  Checking if timestamp is 24 hours old
        lowerRange = goBack(curTime,24,Calendar.HOUR_OF_DAY);         //  today
        upperRange = goBack(curTime,1,Calendar.HOUR_OF_DAY);         //  one hour
        if(lowerRange <= timestamp && timestamp < upperRange ) {
            return getTimeDifference(timestamp,curTime,Calendar.HOUR) + " hours ago";
        }

        //  Checking if timestamp is 1 hour old
        lowerRange = goBack(curTime,60,Calendar.MINUTE);         //  1 hour
        upperRange = goBack(curTime,1,Calendar.MINUTE);         //  1 minute
        if(lowerRange <= timestamp && timestamp < upperRange ) {
            return getTimeDifference(timestamp,curTime,Calendar.MINUTE) + " minutes ago";
        }

        //  Checking if timestamp is 1 minute old
        lowerRange = goBack(curTime,60,Calendar.SECOND);         //  1 minute
        upperRange = goBack(curTime,30,Calendar.SECOND);         //  30 seconds
        if(lowerRange <= timestamp && timestamp < upperRange ) {
            return "about a minute ago";
        }

        return "seconds ago";
    }


    /**
     *
     * @param timestamp, the time in milliseconds, String variant.
     * @return  String representing timestamp in a more human readable form.
     * if current data and time is 26/8/2016 10:16:00 AM,
     * converted timestamp value                                                equivalent converted readable value
     *
     * 25/8/2016 12:01:00 AM < value < 25/8/2016 11:59:PM                               yesterday
     * 26/7/2015  12:00:00 PM < value < 25/8/2016 12:00:00                              few days ago
     * value < 26/7/2015  12:00:00 PM                                                   months ago
     *
     * 26/8/2016 10:16:30 < value < 26/8/2016 10:16:00 AM                             just now   (30 seconds)
     * 26/8/2016 10:17:00 < value < 26/8/2016 10:16:31 AM                             seconds now   (60 seconds)
     * 26/8/2016 11:16:00 < value < 26/8/2016 10:17:01 AM                             x minutes now   (for 59 minutes)
     * 27/8/2016 11:16:00 < value < 26/8/2016 11:17:00 AM                             x hours now   (minutes)
     *
     */
    public static final String getReadableTimestamp(String time){

        /**
         * Would store upper and lower range
         */
        long upperRange,lowerRange,curTime;
        long timestamp = Long.parseLong(time);

        curTime = System.currentTimeMillis();
        upperRange = goBack(curTime,1,Calendar.MONTH);        //  last month same date.
        //  Checking if the timestamp is more than 1 month old

        if(timestamp < upperRange){
            return getTimeDifference(timestamp,curTime,Calendar.MONTH)+" months ago";
        }


        lowerRange = yesterdaysDateAt12();                      //  yesterday
        upperRange = goBack(lowerRange,-24,Calendar.HOUR_OF_DAY);                                 //  today
        if(lowerRange <= timestamp && timestamp < upperRange ) {
            return "yesterday";
        }

        //  Checking if the timestamp is less than 1 month old
        lowerRange = goBack(curTime,1,Calendar.MONTH);        //  last month same date
        upperRange = goBack(curTime,1,Calendar.DATE);         //  yesterday
        if(lowerRange <= timestamp && timestamp < upperRange ) {
            return getTimeDifference(timestamp,curTime,Calendar.DATE) + " days ago";
        }
        //  Checking if the timestamp is 1 day old


        //  Checking if timestamp is 24 hours old
        lowerRange = goBack(curTime,24,Calendar.HOUR_OF_DAY);         //  today
        upperRange = goBack(curTime,1,Calendar.HOUR_OF_DAY);         //  one hour
        if(lowerRange <= timestamp && timestamp < upperRange ) {
            return getTimeDifference(timestamp,curTime,Calendar.HOUR) + " hours ago";
        }

        //  Checking if timestamp is 1 hour old
        lowerRange = goBack(curTime,60,Calendar.MINUTE);         //  1 hour
        upperRange = goBack(curTime,1,Calendar.MINUTE);         //  1 minute
        if(lowerRange <= timestamp && timestamp < upperRange ) {
            return getTimeDifference(timestamp,curTime,Calendar.MINUTE) + " minutes ago";
        }

        //  Checking if timestamp is 1 minute old
        lowerRange = goBack(curTime,60,Calendar.SECOND);         //  1 minute
        upperRange = goBack(curTime,30,Calendar.SECOND);         //  30 seconds
        if(lowerRange <= timestamp && timestamp < upperRange ) {
            return "about a minute ago";
        }

        return "seconds ago";
    }

    /**
     *
     * @param t1    lower timestamp
     * @param t2    upper timestamp
     *              t < t2
     * @param durationType  Minute, Hour, Days,
     * @return  duration between t1 and t2 in unit durationType
     */
    private static long getTimeDifference(long t1, long t2, int durationType) {

        long diff = t2 - t1;

        switch (durationType){
            case Calendar.DATE :
                return TimeUnit.MILLISECONDS.toDays(diff);
            case Calendar.HOUR :
                return TimeUnit.MILLISECONDS.toHours(diff);
            case Calendar.MINUTE :
                return TimeUnit.MILLISECONDS.toMinutes(diff);
        }
        return 0;
    }

    /**
     *
     * @param date , in milliseconds to be worked on
     * @param duration ,    How many units of time you need to go back. duration > 0
     * @param durationType, Calendar.Month, Calendar.Time etc.
     * @return, new date in milliseconds
     */
    private static long goBack(long date, int duration, int durationType){

        calendar.setTimeInMillis(date);
        calendar.add(durationType,(-duration));
        return calendar.getTimeInMillis();
    }

    private static long yesterdaysDateAt12(){
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY,00);
        calendar.set(Calendar.MINUTE,01);
        calendar.set(Calendar.SECOND,00);

        calendar.add(Calendar.HOUR_OF_DAY,-24);

        return calendar.getTimeInMillis();
    }

    // Helper methods Not being used
//
//    /**
//     * Converts a timestamp in milliseconds to a String date.
//     * look at format pattern of date for more info about dateFormatter.
//     * No precision is lost
//     *
//     * @param timestamp
//     * @return, String equivalent to the date
//     */
//    private static String convertToDate(long timestamp){
//
//
//        calendar.setTimeInMillis(timestamp);
//        Date date = calendar.getTime();
//
//        return dateFormat.format(date);
//    }
//
//    /**
//     * Converts a String date into long.
//     * @param date
//     * @return, long timesamp representing time in milliseconds.
//     *
//     * @exception if the date is not parsable, returns timestamp of current system time.
//     */
//    private static long convertToMilliseconds(String date){
//        try{
//            calendar.setTime(dateFormat.parse(date));
//        }
//        catch (ParseException e){
//            e.printStackTrace();
//            calendar.setTime(new Date());
//        }
//        long milliS = calendar.getTimeInMillis();
//        return milliS;
//    }
//

}
