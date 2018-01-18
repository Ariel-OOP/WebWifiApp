
import components.Attributes.WIFISample;
import components.Attributes.WifiPointsTimePlace;
import components.Attributes.WIFISample;
import components.Attributes.WifiPointsTimePlace;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class WifiPointsTimePlaceTest {


    WifiPointsTimePlace line;
    WIFISample wifiSample1;
    WIFISample wifiSample2;
    WIFISample wifiSample3;

    @Before
    public void addPoint() throws Exception {
        line = new WifiPointsTimePlace();

        wifiSample1 = new WIFISample("a","b","2017-10-27 16:12:01","2","e","f","g","h","i","j");
        wifiSample2 = new WIFISample("a","b","2017-10-27 16:12:01","2","e","f","g","h","i","j");
        wifiSample3 = new WIFISample("a","b","2017-10-27 16:12:01","2","e","f","g","h","i","j");

        line.addPoint(wifiSample1);
        line.addPoint(wifiSample2);
        line.addPoint(wifiSample3);
    }

    @Test
    public void getWifiPoints() throws Exception {

        List<String> testLine = new ArrayList();

        List<String> ExpectLine = new ArrayList();
        ExpectLine.add("2017-10-27 16:12:01");
        ExpectLine.add("j");
        ExpectLine.add("f");
        ExpectLine.add("g");
        ExpectLine.add("h");

        ExpectLine.add("b");
        ExpectLine.add("a");
        ExpectLine.add(wifiSample3.getWIFI_Frequency());
        ExpectLine.add("e");

        ExpectLine.add("b");
        ExpectLine.add("a");
        ExpectLine.add(wifiSample3.getWIFI_Frequency());
        ExpectLine.add("e");

        ExpectLine.add("b");
        ExpectLine.add("a");
        ExpectLine.add(wifiSample3.getWIFI_Frequency());
        ExpectLine.add("e");

        testLine = line.getWifiPoints();
        System.out.println();
        for (int i = 0; i < testLine.size(); i++)
        {
            assertEquals(ExpectLine.get(0),testLine.get(0));
        }
    }
}