package components.Console_App;

import java.util.Date;

/**
 * A collection of static functions that are responsible to check if the line that was read in file
 * fulfills the filter and its requirements.
 * It also contains functions that print if the user input was correct when generating a filter.
 */

public class LineFilters {

    /**
     *
     * @param lat1 the latitude from the file
     * @param lng1 the longitude from the file
     * @param lat2 the latitude from the user
     * @param lng2  the longitude from the file
     * @param radius the user given radius
     * @return true if the given WIFI point is in the filters radius
     */
    public static boolean distFrom(double lat1, double lng1, double lat2, double lng2, double radius) {
        //

        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = (double) (earthRadius * c);

        return dist <= radius;
    }

    /**
     *
     * @param startDate is from the users date
     * @param endDate is from the users date
     * @param inputDate is from the WIFI pointsq date
     * @return true if the inputDate is in bounds of the start date and end date.
     */
    public static boolean isDateInBounds(Date startDate, Date endDate, long inputDate){
        long test = startDate.getTime();
        long test2= inputDate-startDate.getTime();
        long test3= endDate.getTime()-inputDate;
        if ( (inputDate-startDate.getTime())>=0 && (endDate.getTime()-inputDate)>=0 )
            return true;
        return false;
    }

    /**
     * prints a message to the user to either enter or renter a input that was incorrect
     * @param choice is the choice of filter to be used
     */
    public static void printInput(int choice){
        switch (choice){
            case 1: System.out.println("enter the LOCATION in the following format: \n32.009,43.99 55");
                    break;
            case 2: System.out.println("enter the TIME in following format:" +
                    " \n" + "(Start Time\t ,\t\t EndTime)\n"+
                    "15-12-2001 12:12,12-12-2023 12:12");
                     break;
            case 3: System.out.println("enter the ID with a space:");
                    break;
            case 0:
                     System.out.println("No filter, press enter to continue");
                     break;
            default: System.out.println("incorrect input, NO Filter has been defaulted\n" +
                    "press enter to continue");
        }
    }
}
