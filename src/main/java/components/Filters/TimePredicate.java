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

            Date timeFrom = formatter.parse(timeFromStr);
            Date timeTo = formatter.parse(timeToStr);

            System.out.println(timeFrom);
            System.out.println(formatter.format(timeFrom));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
    }

    @Override
    public boolean test(WifiPointsTimePlace wifiPointsTimePlace) {
        Date lineDate = new Date(wifiPointsTimePlace.getFirstSeen());

        return lineDate.after(this.timeFrom) && lineDate.before(this.timeTo);
    }
}
