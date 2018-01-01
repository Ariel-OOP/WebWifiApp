package components.CSV_IO;

import components.Attributes.WIFISample;
import components.Attributes.WifiPointsTimePlace;
import components.Attributes.HashRouters;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author moshe, nissan

 */
public class WigleFileReader {

	//CSV file header
	private  final String [] FILE_HEADER_MAPPING = {"MAC","SSID","AuthMode","FirstSeen","Channel","RSSI","CurrentLatitude","CurrentLongitude","AltitudeMeters","AccuracyMeters","Type"};
	//A list of all wifi points

	private List<WifiPointsTimePlace> allWifiPoints;
  
	//WIFI attributes
	private final String WIFI_MAC = "MAC";
	private final String WIFI_SSID = "SSID";
	private final String WIFI_AuthMode = "AuthMode";
	private final String WIFI_FirstSeen = "FirstSeen"; 
	private final String WIFI_Channel = "Channel";
	private final String WIFI_RSSI = "RSSI";
	private final String WIFI_Lat = "CurrentLatitude";
	private final String WIFI_Lon = "CurrentLongitude";
	private final String WIFI_Alt = "AltitudeMeters"; 
	private final String WIFI_Accuracy = "AccuracyMeters";
	private final String WIFI_Type = "Type";

	private String[] firstLine_DeviceAttributes = {};
	private String[] firstLine_Titles = {};

	private HashRouters<String,WIFISample> hashRouters;
	private String fileName = "";

	/**
	 * @param fileName the file name that will worked on.
	 */
	public WigleFileReader(String fileName)
	{
		this.fileName = fileName;
		hashRouters = new HashRouters<>();
	}

	/**
	 * readCsvFile reads a wigle file and will automatically sort the file file by time,location and RSSI
	 */
	public void readCsvFile() {

		String deviceName = "";

		FileReader fileReader = null;

		CSVParser csvFileParser = null;
		boolean correctHeaderAndTitle = false;
		
		//Create the CSVFormat object with the header mapping
		CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(FILE_HEADER_MAPPING);

		try {

			//Create a new list of wifi points to be filled by CSV file data
			//each element is a WIFi sample
			allWifiPoints = new ArrayList();

			//initialize FileReader object
			fileReader = new FileReader(fileName);

			//Read line of device attributes
			BufferedReader inStream = new BufferedReader(fileReader);
			firstLine_DeviceAttributes = inStream.readLine().split(",");

			firstLine_Titles = inStream.readLine().split(",");

			correctHeaderAndTitle = checkTitle(firstLine_Titles) && checkHeader(firstLine_DeviceAttributes);
			if(!correctHeaderAndTitle)
				throw new Exception("This is not correct CSV file");

			//Get the device name from the header
			deviceName = ExtractDeviceattribute(firstLine_DeviceAttributes[5]);

			//initialize CSVParser object
			csvFileParser = new CSVParser(inStream, csvFileFormat);

			//Get a list of CSV file records
			List csvRecords = csvFileParser.getRecords(); 

			//Read the CSV file records starting from the second record to skip the header

			CSVRecord record = (CSVRecord) csvRecords.get(0);

			//
			WIFISample wifiSample = new WIFISample(record.get(WIFI_MAC),record.get(WIFI_SSID),record.get(WIFI_FirstSeen),
					record.get(WIFI_Channel),record.get(WIFI_RSSI),record.get(WIFI_Lat),record.get(WIFI_Lon),
					record.get(WIFI_Alt),record.get(WIFI_Type),deviceName);

			//
			List<WIFISample> PointsOfOneMinute = new ArrayList<>();

			PointsOfOneMinute.add(wifiSample);
			WIFISample prev = PointsOfOneMinute.get(0);

			int sizeOfLines = csvRecords.size();

			for (int i = 1; i < sizeOfLines; i++) {
				record = (CSVRecord) csvRecords.get(i);
				//Create a new WIFISample object and fill his data

				wifiSample = new WIFISample(record.get(WIFI_MAC),record.get(WIFI_SSID),record.get(WIFI_FirstSeen),
						record.get(WIFI_Channel),record.get(WIFI_RSSI),record.get(WIFI_Lat),record.get(WIFI_Lon),
						record.get(WIFI_Alt),record.get(WIFI_Type),deviceName);

				//Compares the previous lan, lat and time to the current WIFI sample // Mybe add Compares alt
				if (wifiSample.getWIFI_Lat().equals(prev.getWIFI_Lat()) && wifiSample.getWIFI_Lon().equals(prev.getWIFI_Lon()) && wifiSample.getWIFI_FirstSeen().equals(prev.getWIFI_FirstSeen()) ) {
					PointsOfOneMinute.add(wifiSample);

				}else
				{
					allWifiPoints.add(takeTopTenPoints(PointsOfOneMinute));

					prev = wifiSample;
					PointsOfOneMinute.clear();
					PointsOfOneMinute.add(wifiSample);		
				}
			}
			csvFileParser.close();
		} 
		catch (Exception e) {
			System.out.println("Error in CsvFileReader !!!");
			e.printStackTrace();
		} finally {
			try {
				fileReader.close();
				//csvFileParser.close();
			} catch (IOException e) {
				System.out.println("Error while closing fileReader/csvFileParser Becouse that your file is incorrect !!!");
				e.printStackTrace();
			}
		}
	}

	private WifiPointsTimePlace takeTopTenPoints(List<WIFISample> pointsOfOneMinute) {
		//Sorting 10 best wifi signals

		WifiPointsTimePlace line = new WifiPointsTimePlace();

		Collections.sort(pointsOfOneMinute, new Comparator<WIFISample>() {
			@Override
			public int compare(WIFISample wifi1, WIFISample wifi2) {
				return wifi1.getWIFI_RSSI().compareTo(wifi2.getWIFI_RSSI());
			}
		});

		//topTenPoints.clear();
		int tenOrLessPoints = 0;

		line = new WifiPointsTimePlace();
		for(WIFISample wifiSamp : pointsOfOneMinute) {
			if (tenOrLessPoints < 10 /*&& wifiSamp.getWIFI_Type().equals("WIFI")*/) {
				hashRouters.addElement(wifiSamp.getWIFI_MAC(),wifiSamp);
				line.addPoint(wifiSamp);
				tenOrLessPoints++;
			}
		}
		return line;
	}

	private String ExtractDeviceattribute(String string) {
		//The structure of device name id "display=...".
		//The method cut the name and throw the string "display=" 

		int indexOfEqualSymbol = string.indexOf("=");
		String name = string.substring(indexOfEqualSymbol + 1);//Add 1 becouse have to cut affter '='

		return name;
	}

	/**
	 * getWigleList will return the final sorted list of wifi includes more than 10
	 * @return returns a List of LineOfFinalCSV.
	 */
	public List<WifiPointsTimePlace> getWigleList() {
		return allWifiPoints;
	}

	/**
	 * This method checks if in the header there are:  WigleWifi...,appRelease=...,model=...,release=...,device=...,display=...,board=...,brand=...
	 * @return The method return true if all the parameters appear, else the method return false.
	 */
	private boolean checkHeader(String[] arrayLine)
	{
		boolean headerCorrect = false;

		if (arrayLine.length == 8) {
			headerCorrect = arrayLine[0].contains("WigleWifi") && arrayLine[1].contains("appRelease=")
					&& arrayLine[2].contains("model=") && arrayLine[3].contains("release=")
					&& arrayLine[4].contains("device=") && arrayLine[5].contains("display=")
					&& arrayLine[6].contains("board=") && arrayLine[7].contains("brand=");
		}
		return headerCorrect;
	}

	/**
	 * This method checks if in the title there are: MAC,SSID,AuthMode,FirstSeen,Channel,RSSI,CurrentLatitude,CurrentLongitude,AltitudeMeters,AccuracyMeters,Type
	 * @return The method return true if all the parameters appear, else the method return false.
	 */
	private boolean checkTitle(String[] arrayLine)
	{
		boolean titleCorrect = false;

		if (arrayLine.length == 11) {
			titleCorrect = arrayLine[0].contains("MAC") && arrayLine[1].contains("SSID")
					&& arrayLine[2].contains("AuthMode") && arrayLine[3].contains("FirstSeen")
					&& arrayLine[4].contains("Channel") && arrayLine[5].contains("RSSI")
					&& arrayLine[6].contains("CurrentLatitude") && arrayLine[7].contains("CurrentLongitude")
					&& arrayLine[8].contains("AltitudeMeters") && arrayLine[9].contains("AccuracyMeters")
					&& arrayLine[10].contains("Type");
		}
		return titleCorrect;
	}

	public HashRouters<String, WIFISample> getHashRouters() {
		return hashRouters;
	}

}
