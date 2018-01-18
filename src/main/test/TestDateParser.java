

import components.Filters.DateParser;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class TestDateParser {

	DateParser date = new DateParser();

	@Test
	public void testSetCorrectDateFormat1() {
		DateParser.setCorrectDateFormat("1/25/2017 08:43:00");
		assertEquals("25-1-2017 08:43:00", DateParser.getCorrectDateFormat());

	}
	@Test
	public void testSetCorrectDateFormat2() {
		DateParser.setCorrectDateFormat("12-12-2012 00:11");
		assertEquals("12-12-2012 00:11:00", DateParser.getCorrectDateFormat());
		
	}
	@Test
	public void testSetCorrectDateFormat3() {
		DateParser.setCorrectDateFormat("12-25-2012 00:11");
		assertEquals("25-12-2012 00:11:00", DateParser.getCorrectDateFormat());
		
	}
	@Test
	public void testSetCorrectDateFormat4() {
		DateParser.setCorrectDateFormat("12-25-2012");
		assertEquals("25-12-2012 00:00:00", DateParser.getCorrectDateFormat());
		
	}
}
