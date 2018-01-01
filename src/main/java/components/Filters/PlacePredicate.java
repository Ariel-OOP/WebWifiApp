package components.Filters;

import components.Attributes.WifiPointsTimePlace;

import java.io.Serializable;
import java.util.function.Predicate;

public class PlacePredicate implements Predicate<WifiPointsTimePlace> {
    private double lat1;
    private double lon1;
    private double lat2;
    private double lon2;

    public PlacePredicate(double lat1, double lon1, double lat2, double lon2) {
        this.lat1 = lat1;
        this.lon1 = lon1;
        this.lat2 = lat2;
        this.lon2 = lon2;
    }

    @Override
    public boolean test(WifiPointsTimePlace wifiPointsTimePlace) {
        double testLat = Double.valueOf(wifiPointsTimePlace.getLat());
        double testLon = Double.valueOf(wifiPointsTimePlace.getLon());
        boolean condition;

        if (lat1<=testLat)
            condition=true;
        if (lon1<=testLon)
            condition=true;
        if (lat2>=testLat)
            condition=true;
        if (lon2>=testLon)
            condition=true;

        if (lat1<=testLat && lon1<=testLon && lat2>=testLat && lon2>=testLon)
            return true;
        return false;
    }
}
