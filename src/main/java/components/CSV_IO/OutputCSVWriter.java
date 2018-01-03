package components.CSV_IO;

import components.Attributes.WIFISample;
import components.Attributes.WIFIWeight;
import components.Attributes.WifiPointsTimePlace;
import components.Attributes.HashRouters;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
		* @author Moshe
		*/

public class OutputCSVWriter {

	//Delimiter used in CSV file
	private static final String NEW_LINE_SEPARATOR = "\n";

	//CSV file header
	private static final Object [] FILE_HEADER = {"Time","ID","Lat","Lon","Alt","#WiFi networks","SSID1","MAC1","Frequncy1","Signal1",
			"SSID2","MAC2","Frequncy2","Signal2","SSID3","MAC3","Frequncy3","Signal3","SSID4","MAC4","Frequncy4","Signal4",
			"SSID5","MAC5","Frequncy5","Signal5","SSID6","MAC6","Frequncy6","Signal6","SSID7","MAC7","Frequncy7","Signal7",
			"SSID8","MAC8","Frequncy8","Signal8","SSID9","MAC9","Frequncy9","Signal9","SSID10","MAC10","Frequncy10","Signal10"};

	List<File> files;
	File dir;

	//String outputPath;

	HashRouters<String,WIFISample> allRoutersOfTheFiles;

	/**
	 * @param files - List of files that the class will process.
	 */

	public OutputCSVWriter(List<File> files) {
		this.files = files;
		//this.outputPath = outputPath;

		allRoutersOfTheFiles = new HashRouters<>();

//		//Deletes file if it exists
//		File fileToDelete = new File(outputPath+".csv");
//		fileToDelete.delete();

	}

	/**
	 * The function process all the lines in a 'files' variable, and create a list and a hash Table.
	 * The list contains all lines that appear in the final CSV.
	 * The Hash table contains for each MAC address a list of objects (of the type WIFISample) that each object contains
	 * 		information about once that the MAC address is in the final CSV file.
	 * @return the list that contains all lines that appear in the final CSV.
	 */
	public List<WifiPointsTimePlace> sortAndMergeFiles() {
		List<WifiPointsTimePlace> allSortedPoints = new ArrayList<>(); //from all the files together
		List<WifiPointsTimePlace> processedFile = new ArrayList<>();
		WigleFileReader wigleFileReader;

		for (File file : files) {

			wigleFileReader = new WigleFileReader(file.getPath());
			wigleFileReader.readCsvFile();

			allRoutersOfTheFiles.mergeToHash(wigleFileReader.getHashRouters());

			//allSortedPoints = wigleFileReader.getWigleList();
			processedFile.addAll(wigleFileReader.getWigleList());
		}
		return processedFile;
	}

	/**
	 * The function gets:
	 * @param listOfLocatins - list of new locations for ALL lines of the source file.
	 * @param allLinesOfSourceFile - list of all lines in the sourcefile by type of WifiPointsTimePlace.
	 * @param destinationPath - the path for the updated file.
	 *
	 * The function replace all the locations of the source file with the location that listOfLocatins holds and create new file in a "destinationPath"
	 *
	 */
	public static void changeLocationOfFile(List<WIFIWeight> listOfLocatins,List<WifiPointsTimePlace> allLinesOfSourceFile,String destinationPath) {

		List<WifiPointsTimePlace> updatedListOfAllLines = new ArrayList<>();

		for (int i = 0; i < allLinesOfSourceFile.size(); i++)
		{
			WifiPointsTimePlace oldPoint = allLinesOfSourceFile.get(i);//Old line

			WIFIWeight location = listOfLocatins.get(i);//New location

			WifiPointsTimePlace newLine = new WifiPointsTimePlace(oldPoint.getFirstSeen(),oldPoint.getDevice(),location.getWIFI_Lat()+"",location.getWIFI_Lon()+"",location.getWIFI_Alt()+"",oldPoint.getWifiPointsAsIs());
			updatedListOfAllLines.add(newLine);//Add to list the new (updated) line
		}

		OutputCSVWriter.ExportToCSV(updatedListOfAllLines,destinationPath,null);
	}

	/**
	 * The function gets a list of objects and path of file. The function creates a new file, that contains all of the objects in the list, at the address it received.
	 * @param fileAfterSortintAndMerging - List of the the objects. The objects can only be WifiPointsTimePlace or WIFIWeight.
	 * @param outputPath - the path of the new CSV file
	 * @param <T> - WifiPointsTimePlace or WIFIWeight.
	 */
	public static<T extends Object> void ExportToCSV(List<T> fileAfterSortintAndMerging, String outputPath, Predicate<WifiPointsTimePlace> predicate) {

		//Deletes file if it exists
		File fileToDelete = new File(outputPath+".csv");
		fileToDelete.delete();

		FileWriter fileWriter = null;

		CSVPrinter csvFilePrinter = null;

		//Create the CSVFormat object with "\n" as a record delimiter
		CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);

		try {

			//initialize FileWriter object
			fileWriter = new FileWriter(outputPath);

			//initialize CSVPrinter object
			csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);

			//Create CSV file header
			csvFilePrinter.printRecord(FILE_HEADER);

			for (T line : fileAfterSortintAndMerging) {
				if (line instanceof WifiPointsTimePlace) {
					if (predicate==null)
						csvFilePrinter.printRecord(((WifiPointsTimePlace)line).getWifiPoints());
					else if(predicate.test((WifiPointsTimePlace)line)==true)
						csvFilePrinter.printRecord(((WifiPointsTimePlace)line).getWifiPoints());
				}else if(line instanceof WIFIWeight){
					csvFilePrinter.printRecord(((WIFIWeight)line).propertiesOfWifiWeight());
				}
			}
			System.out.println("CSV file was created successfully !!!");

		} catch (Exception e) {
			System.out.println("Error in CsvFileWriter !!!");
			e.printStackTrace();
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
				csvFilePrinter.close();
			} catch (IOException e) {
				System.out.println("Error while flushing/closing fileWriter/csvPrinter !!!");
				e.printStackTrace();
			}
		}
	}


	/**
	 * @return the hash table of the MACs.
	 */
	public HashRouters<String, WIFISample> getAllRoutersOfTheFiles() {
		return allRoutersOfTheFiles;
	}
}

