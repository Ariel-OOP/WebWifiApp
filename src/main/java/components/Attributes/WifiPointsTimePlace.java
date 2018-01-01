package components.Attributes;

import components.Algorithms.Algorithm2;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Moshe
 */
public class WifiPointsTimePlace {

	List<WIFISample> WifiPoints;

	private String FirstSeen;
	private String Device;
	private String Lat;
	private String Lon;
	private String Alt;


	public WifiPointsTimePlace() {
		WifiPoints = new ArrayList();;
	}

	public void addPoint(WIFISample point)
	{
		FirstSeen = point.getWIFI_FirstSeen();
		Device = point.getWIFI_Device();
		Lat = point.getWIFI_Lat();
		Lon = point.getWIFI_Lon();
		Alt = point.getWIFI_Alt();

		WifiPoints.add(point);
	}

	/**
	 * The method enters into a list the common parameters of these WIFI points (time, place, device)
	 * and in addition the parameters of the points (MAC, SSID, Frequency, RSSI).
	 * @return the List<String> that contain all parameters above.
	 */
	public List<String> getWifiPoints() {

		List<String> line = new ArrayList();
		line.add(FirstSeen);
		line.add(Device);
		line.add(Lat);
		line.add(Lon);
		line.add(Alt);
		line.add("" + WifiPoints.size());

		for(WIFISample wifi : WifiPoints) {
			line.add(wifi.getWIFI_SSID());
			line.add(wifi.getWIFI_MAC());
			line.add(wifi.getWIFI_Frequency());
			line.add(wifi.getWIFI_RSSI());
		}

		return line;
	}
	public List<String> getHTMLWifiPoints() {

		List<String> line = new ArrayList();
		line.add(FirstSeen);
		line.add(Device);
		line.add(Lat);
		line.add(Lon);
		line.add(Alt);
		line.add("" + WifiPoints.size());


		for(WIFISample wifi : WifiPoints) {
			line.add(wifi.getWIFI_SSID());
			line.add(wifi.getWIFI_MAC());
			line.add(wifi.getWIFI_Frequency());
			line.add(wifi.getWIFI_RSSI());
		}
		if(WifiPoints.size()<10){
			for (int i=0; i<10-WifiPoints.size() ;i++){
				line.add(null);
				line.add("00:00:00:00:00:00");
				line.add("0");
				line.add("-100");
			}
		}

		return line;
	}

	/**
	 *
	 * @return int number of points in this line
	 */
	public String toString()
	{
		return "" + WifiPoints.size();
	}

	public WIFIWeight checkSimilarity(ArrayList<WIFIWeight> userInputMACs)
	{
		WIFIWeight result;
		boolean foundThisMAC = false;
		ArrayList<Integer> signalsLine = new ArrayList<Integer>();
		ArrayList<Integer> signalsUser = new ArrayList<Integer>();
		double 	lat = Double.parseDouble(this.Lat);
		double	lon = Double.parseDouble(this.Lon);
		double	alt = Double.parseDouble(this.Alt);
		double weight = 0;

		for (WIFIWeight sample : userInputMACs) {
			signalsUser.add(sample.getWIFI_RSSI());
		}

		for(WIFIWeight userSample : userInputMACs) {
			for (WIFISample sample : WifiPoints) {
				if(userSample.getWIFI_MAC().equals(sample.getWIFI_MAC()))
				{
					foundThisMAC = true;
					signalsLine.add(Integer.parseInt(sample.getWIFI_RSSI()));
					break;
				}
			}
			if(!foundThisMAC)
				signalsLine.add(-120);
			foundThisMAC = false;
		}

		weight = Algorithm2.calcWeight(signalsLine,signalsUser);

		result = new WIFIWeight("",lat*weight,lon*weight,alt*weight,0,weight);

		return result;
	}

	public String getFirstSeen() {
		return FirstSeen;
	}

	public String getDevice() {
		return Device;
	}

	public String getLat() {
		return Lat;
	}

	public String getLon() {
		return Lon;
	}

	public String getAlt() {
		return Alt;
	}

}
