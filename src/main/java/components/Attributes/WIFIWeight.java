package components.Attributes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nissan on 12/3/2017.
 */
public class WIFIWeight{

    private String WIFI_MAC;
    private double WIFI_Lat;
    private double WIFI_Lon;
    private double WIFI_Alt;
    private int WIFI_RSSI;
    private double WIFI_Weight;

    public WIFIWeight(String WIFI_MAC, double WIFI_Lat, double WIFI_Lon, double WIFI_Alt, int WIFI_RSSI, double WIFI_Weight) {
        this.WIFI_MAC = WIFI_MAC;
        this.WIFI_Lat = WIFI_Lat;
        this.WIFI_Lon = WIFI_Lon;
        this.WIFI_Alt = WIFI_Alt;
        this.WIFI_RSSI = WIFI_RSSI;
        this.WIFI_Weight = WIFI_Weight;
    }

    public String getWIFI_MAC() {
        return WIFI_MAC;
    }

    public void setWIFI_MAC(String WIFI_MAC) {
        this.WIFI_MAC = WIFI_MAC;
    }

    public double getWIFI_Lat() {
        return WIFI_Lat;
    }

    public void setWIFI_Lat(double WIFI_Lat) {
        this.WIFI_Lat = WIFI_Lat;
    }

    public double getWIFI_Lon() {
        return WIFI_Lon;
    }

    public void setWIFI_Lon(double WIFI_Lon) {
        this.WIFI_Lon = WIFI_Lon;
    }

    public double getWIFI_Alt() {
        return WIFI_Alt;
    }

    public void setWIFI_Alt(double WIFI_Alt) {
        this.WIFI_Alt = WIFI_Alt;
    }

    public int getWIFI_RSSI() {
        return WIFI_RSSI;
    }

    public void setWIFI_RSSI(int WIFI_RSSI) {
        this.WIFI_RSSI = WIFI_RSSI;
    }

    public double getWIFI_Weight() {
        return WIFI_Weight;
    }

    public void setWIFI_Weight(double WIFI_Weight) {
        this.WIFI_Weight = WIFI_Weight;
    }

    public List<String> propertiesOfWifiWeight(){
        List<String> line = new ArrayList();
        line.add(WIFI_MAC);
        line.add(WIFI_Lat+"");
        line.add(WIFI_Lon+"");
        line.add(WIFI_Alt+"");
        line.add(WIFI_Weight+"");
        return line;
    }
}
