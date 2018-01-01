package components.Attributes;

import components.Filters.DateParser;

/**
 * @author Moshe
 * some aspects based on:  http://examples.javacodegeeks.com/core-java/apache/commons/csv-commons/writeread-csv-files-with-apache-commons-csv-example/
 */

public class WIFISample {

	//WIFI attributes
	private String WIFI_MAC;
	private String WIFI_SSID;
	private String WIFI_AuthMode;
	private String WIFI_FirstSeen;
	private String WIFI_Channel;
	private String WIFI_Frequency;
	private String WIFI_RSSI;
	private String WIFI_Lat;
	private String WIFI_Lon;
	private String WIFI_Alt;
	private String WIFI_Accuracy;
	private String WIFI_Type;

	//Device attributes
	private String WIFI_Device;

	public WIFISample(String wIFI_MAC, String wIFI_SSID, String wIFI_FirstSeen, String wIFI_Channel, String wIFI_RSSI,

					  String wIFI_Lat, String wIFI_Lon, String wIFI_Alt, String wIFI_Type, String wIFI_Device) {

		WIFI_MAC = wIFI_MAC;
		WIFI_SSID = wIFI_SSID;
		WIFI_FirstSeen = DateParser.setCorrectDateFormat(wIFI_FirstSeen);
		WIFI_Channel = wIFI_Channel;
		WIFI_RSSI = wIFI_RSSI;
		WIFI_Lat = wIFI_Lat;
		WIFI_Lon = wIFI_Lon;
		WIFI_Alt = wIFI_Alt;
		WIFI_Type = wIFI_Type;

		WIFI_Device = wIFI_Device;

		WIFI_Frequency = convertChannelToFrequency(WIFI_Channel);
	}

	public String getWIFI_Frequency() {
		return WIFI_Frequency;
	}

	public void setWIFI_Frequency(String wIFI_Frequency) {
		WIFI_Frequency = wIFI_Frequency;
	}

	private String convertChannelToFrequency(String wIFI_Channel) {
		int i;
		String channelString = "";
		int channel = Integer.parseInt(wIFI_Channel);

		if(channel >= 1 && channel <= 14)
		{
			i = (channel - 1) * 5 + 2412;
			channelString = i + "";
		}else if(channel >= 36 && channel <= 165)
		{
			i = (channel - 34) * 5 + 5170;
			channelString = i + "";
		}


		return channelString;

	}

	public String getWIFI_Device() {
		return WIFI_Device;
	}

	public void setWIFI_Device(String wIFI_Device) {
		WIFI_Device = wIFI_Device;
	}

	public String getWIFI_MAC() {
		return WIFI_MAC;
	}


	public void setWIFI_MAC(String wIFI_MAC) {
		WIFI_MAC = wIFI_MAC;
	}


	public String getWIFI_SSID() {
		return WIFI_SSID;
	}


	public void setWIFI_SSID(String wIFI_SSID) {
		WIFI_SSID = wIFI_SSID;
	}


	public String getWIFI_FirstSeen() {
		return WIFI_FirstSeen;
	}


	public void setWIFI_FirstSeen(String wIFI_FirstSeen) {
		WIFI_FirstSeen = DateParser.setCorrectDateFormat(wIFI_FirstSeen);
	}


	public String getWIFI_Channel() {
		return WIFI_Channel;
	}


	public void setWIFI_Channel(String wIFI_Channel) {
		WIFI_Channel = wIFI_Channel;
		setWIFI_Frequency(convertChannelToFrequency(wIFI_Channel));
	}


	public String getWIFI_RSSI() {
		return WIFI_RSSI;
	}


	public void setWIFI_RSSI(String wIFI_RSSI) {
		WIFI_RSSI = wIFI_RSSI;
	}


	public String getWIFI_Lat() {
		return WIFI_Lat;
	}


	public void setWIFI_Lat(String wIFI_Lat) {
		WIFI_Lat = wIFI_Lat;
	}


	public String getWIFI_Lon() {
		return WIFI_Lon;
	}


	public void setWIFI_Lon(String wIFI_Lon) {
		WIFI_Lon = wIFI_Lon;
	}


	public String getWIFI_Alt() {
		return WIFI_Alt;
	}


	public void setWIFI_Alt(String wIFI_Alt) {
		WIFI_Alt = wIFI_Alt;
	}


	public String getWIFI_Type() {
		return WIFI_Type;
	}


	public void setWIFI_Type(String wIFI_Type) {
		WIFI_Type = wIFI_Type;
	}


	@Override
	public String toString() {

		return WIFI_MAC + "," + WIFI_SSID + "," + WIFI_Frequency + "," + WIFI_RSSI;
	}

}