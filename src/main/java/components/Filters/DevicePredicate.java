package components.Filters;

import components.Attributes.WifiPointsTimePlace;

import java.io.Serializable;
import java.util.function.Predicate;

public class DevicePredicate implements Predicate<WifiPointsTimePlace> {
    private String device;

    public DevicePredicate(String device) {

        this.device = device.replace("%20"," ");
    }
    public String getDevice() {
        return device;
    }


    @Override
    public boolean test(WifiPointsTimePlace wifiPointsTimePlace) {
        return wifiPointsTimePlace.getDevice().equals(this.device);
    }
}
