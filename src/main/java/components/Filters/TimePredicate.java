package components.Filters;

import components.Attributes.WifiPointsTimePlace;

import java.util.Date;
import java.util.function.Predicate;

public class TimePredicate implements Predicate<WifiPointsTimePlace>{
    private Date timeFrom;
    private Date timeTo;

    public TimePredicate(String timeFrom, String timeTo) {

        this.timeFrom = new Date(timeFrom);
        this.timeTo = new Date(timeTo);
    }

    @Override
    public boolean test(WifiPointsTimePlace wifiPointsTimePlace) {
        Date lineDate = new Date(wifiPointsTimePlace.getFirstSeen());

        return lineDate.after(this.timeFrom) && lineDate.before(this.timeTo);
    }
}
