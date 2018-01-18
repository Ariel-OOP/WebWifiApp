
import components.Attributes.WIFISample;
import components.Attributes.WIFISample;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * @author Moshe
 */
public class WIFISampleTest {

    WIFISample wifiSample;

    @Before
    public void buildObject() throws Exception {
        wifiSample = new WIFISample("b4:ee:b4:36:d2:b0","ben moshe","2017-10-27 16:12:01","2","-51","32.16857397","34.81328609","-9","WIFI","DDos");
    }

    @Test
    public void testToString() throws Exception {
        assertEquals("b4:ee:b4:36:d2:b0,ben moshe,2417,-51", wifiSample.toString());
    }

    @Test
    public void testGetChannel() throws Exception {
        wifiSample.setWIFI_Channel("68");
        assertEquals("5340", wifiSample.getWIFI_Frequency());
    }

    @Test
    public void testGetWIFI_FirstSeen() throws Exception {
        wifiSample.setWIFI_FirstSeen("2017/10/27");
        assertEquals("2017-10-27 00:00:00", wifiSample.getWIFI_FirstSeen());
    }
}