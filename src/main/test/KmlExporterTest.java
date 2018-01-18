
import components.Filters.*;
import components.Filters.Filter;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * Created by Nissan on 11/22/2017.
 */
public class KmlExporterTest {
    Filter filterTime = new Filter(2);

    @Before
    public void setUp() throws Exception {
        Filter filterLocation = new Filter(1);
        filterLocation.setFilter("32.009,43.99 55");

//        Filter filterId = new Filter(3);
    }

    @Test
    public void csvToKml() throws Exception {

        assertEquals(true,filterTime.setFilter("12/12/2001 12:12,12/12/2023 12:12") );
        assertEquals(false,filterTime.setFilter("12/12/2001aa 12:12,12/12/2023 12:12"));

    }

}