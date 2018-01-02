package components.CSV_IO;

import components.Attributes.HashRouters;
import components.Attributes.WIFISample;
import components.Attributes.WifiPointsTimePlace;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nissan on 12/14/2017.
 */
public class CoboCSVReader {
    private static final String [] FILE_HEADER_MAPPING = {"Time","ID","Lat","Lon","Alt","#WiFi networks","SSID1","MAC1","Frequncy1","Signal1","SSID2","MAC2","Frequncy2","Signal2","SSID3","MAC3","Frequncy3","Signal3","SSID4","MAC4","Frequncy4","Signal4","SSID5","MAC5","Frequncy5","Signal5","SSID6","MAC6","Frequncy6","Signal6","SSID7","MAC7","Frequncy7","Signal7","SSID8","MAC8","Frequncy8","Signal8","SSID9","MAC9","Frequncy9","Signal9","SSID10","MAC10","Frequncy10","Signal10"};


    public static List<WifiPointsTimePlace> readCsvFile(String fileName,HashRouters<String,WIFISample> routersOfAllFiles) throws IOException,FileNotFoundException {
        if (!fileName.endsWith(".csv"))
            throw new IOException();

        List<WifiPointsTimePlace> allWifiPoints;

        FileReader fileReader = null;
        CSVParser csvFileParser = null;

        //Create the CSVFormat object with the header mapping
        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withHeader(FILE_HEADER_MAPPING);
        List<WifiPointsTimePlace> allWifiPointsTimePlaces = new ArrayList<>();
        try {

            //Create a new list of wifi points to be filled by CSV file data
            //each element is a WIFi sample
            allWifiPoints = new ArrayList();

            //initialize FileReader object

            fileReader = new FileReader(fileName);

            //initialize CSVParser object

            csvFileParser = new CSVParser(fileReader, csvFileFormat);


            //Get a list of CSV file records
            List csvRecords = csvFileParser.getRecords();

            //Read the CSV file records starting from the second record to skip the header

            CSVRecord record;

            int sizeOfLines = csvRecords.size();

            for (int j = 1; j < sizeOfLines; j++) {
                record = (CSVRecord) csvRecords.get(j);

                int numOfWIFINetworks = Integer.valueOf(record.get("#WiFi networks"));
                WifiPointsTimePlace wifiPointsTimePlace = new WifiPointsTimePlace();
                for (int i = 1; i <= numOfWIFINetworks; i++) {
                    WIFISample wifiSample = new WIFISample(record.get("MAC" + i), record.get("SSID" + i), record.get("Time"),
                            record.get("Frequncy" + i), record.get("Signal" + i), record.get("Lat"), record.get("Lon"),
                            record.get("Alt"), "WIFI", record.get("ID"));
                    wifiPointsTimePlace.addPoint(wifiSample);
                    routersOfAllFiles.addElement(wifiSample.getWIFI_MAC(),wifiSample);
                }
                allWifiPointsTimePlaces.add(wifiPointsTimePlace);
            }
        } catch (Exception e) {
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

        return allWifiPointsTimePlaces;
    }
}
