package components.Filters;

import components.Attributes.WifiPointsTimePlace;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Predicate;

public class TimePredicate implements Predicate<WifiPointsTimePlace> {
    private Date timeFrom;
    private Date timeTo;
    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public TimePredicate(String timeFromStr, String timeToStr) {

        timeFromStr= timeFromStr.replace("T"," ");
        timeToStr= timeToStr.replace("T"," ");

        try {
            //If doesn't have a second then the default is :00
            if(timeFromStr.split(":").length == 2)
                timeFromStr += ":00";
            //If doesn't have a second then the default is :00
            if(timeToStr.split(":").length == 2)
                timeToStr += ":00";

            Date timeFrom = formatter.parse(timeFromStr);
            Date timeTo = formatter.parse(timeToStr);

            System.out.println(timeFrom);
            System.out.println(formatter.format(timeFrom));
            this.timeFrom = timeFrom;
            this.timeTo = timeTo;
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean test(WifiPointsTimePlace wifiPointsTimePlace) {
        Date lineDate=null;
        try {
            lineDate = formatter.parse(wifiPointsTimePlace.getFirstSeen());
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        System.out.println("line date"+lineDate);
//        System.out.println("test date from "+timeFrom);
//        System.out.println("test date to "+timeTo);
//        System.out.println("check date");
//        System.out.println(lineDate.after(this.timeFrom) && lineDate.before(this.timeTo));
        return (lineDate.after(this.timeFrom) && lineDate.before(this.timeTo)) ||
                lineDate.equals(this.timeFrom) || lineDate.equals(this.timeTo);
    }
}
