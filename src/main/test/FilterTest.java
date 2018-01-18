
import components.Filters.Filter;
import components.Filters.Filter;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Nissan on 11/21/2017.
 */
public class FilterTest {


    @Test (timeout = 1000) //in ms
    public void setFilter() throws Exception {
        Filter filter1 = new Filter(1); //location
        assertTrue(filter1.setFilter("32.009,43.99 55"));
        assertEquals(false, filter1.setFilter("32.009,43.99 aa") );

        Filter filter2 = new Filter(2); //Time
        assertEquals(true,filter2.setFilter("04-09-1990 12:12,04-09-2000 12:12"));
        assertEquals(false,filter2.setFilter("04-09-1990a 12:12,04-09-2000 12:12"));

        Filter filter3 = new Filter(3); //ID
//        assertEquals(false,"dd adf");
    }

    @Test
    public void checkLineByFilter() throws Exception {
        Filter filter = new Filter(1);
        filter.setFilter("32.009,43.99 55");
//        assertEquals(true,filter.checkLineByFilter());

    }

}