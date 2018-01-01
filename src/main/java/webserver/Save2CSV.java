package webserver;

import components.Attributes.HashRouters;
import components.Attributes.WIFISample;
import components.Attributes.WifiPointsTimePlace;
import components.CSV_IO.OutputCSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by Nissan on 12/15/2017.
 */
public class Save2CSV{

    public static HashRouters<String,WIFISample> save2csv(String uploadPath, String outputPath){
        System.out.println("==================saving to csv=============");
        System.out.println("============================================");

        File selectedFolder=null;
        File selectedFile = null;
        List<File> selectedFiles=new ArrayList<>();
        List<WifiPointsTimePlace> processedFile=new ArrayList<>();
        HashRouters<String,WIFISample> routersOfAllFiles;

        if(selectedFiles==null && selectedFolder==null){
            //TODO
        }

        File dir = new File(uploadPath);
        for (File file : dir.listFiles()) {
            //Incorrect file type-reject
            if (!(file.getName().toLowerCase().endsWith(".csv"))) {
                System.out.println(file.getName() + " is an incorrect file type in the folder");
                System.out.println("the file was not added to the csv file error 404");
                continue;
            }else{ //add absolute path
                selectedFiles.add(file.getAbsoluteFile());
            }
        }
        for(File file : selectedFiles){
            System.out.println(file.getAbsolutePath());
        }


        OutputCSVWriter outputCSVWriter = new OutputCSVWriter(selectedFiles);

        processedFile =  outputCSVWriter.sortAndMergeFiles();

        outputCSVWriter.ExportToCSV(processedFile,outputPath+"/OutputCSV.csv",null);

        routersOfAllFiles = outputCSVWriter.getAllRoutersOfTheFiles();

        csvToHtml( processedFile,outputPath+"/OutputCSV.txt");
        return routersOfAllFiles;

    }

    public static HashRouters<String,WIFISample> save2csvWithPredicate(String uploadPath, String outputPath, Predicate predicate){
        System.out.println("==================saving to csv=============");
        System.out.println("============================================");

        File selectedFolder=null;
        File selectedFile = null;
        List<File> selectedFiles=new ArrayList<>();
        List<WifiPointsTimePlace> processedFile=new ArrayList<>();
        HashRouters<String,WIFISample> routersOfAllFiles;

        if(selectedFiles==null && selectedFolder==null){
            //TODO
        }

        File dir = new File(uploadPath);
        for (File file : dir.listFiles()) {
            //Incorrect file type-reject
            if (!(file.getName().toLowerCase().endsWith(".csv"))) {
                System.out.println(file.getName() + " is an incorrect file type in the folder");
                System.out.println("the file was not added to the csv file error 404");
                continue;
            }else{ //add absolute path
                selectedFiles.add(file.getAbsoluteFile());
            }
        }
        for(File file : selectedFiles){
            System.out.println(file.getAbsolutePath());
        }


        OutputCSVWriter outputCSVWriter = new OutputCSVWriter(selectedFiles);

        processedFile =  outputCSVWriter.sortAndMergeFiles();

        outputCSVWriter.ExportToCSV(processedFile,outputPath+"/OutputCSV.csv",predicate);

        routersOfAllFiles = outputCSVWriter.getAllRoutersOfTheFiles();

        csvToHtml( processedFile,outputPath+"/OutputCSV.txt");
        return routersOfAllFiles;

    }

    public static void csvToHtml(List<WifiPointsTimePlace> processedFile,String outputPath){
        String allLines="<div id=\"wrap\">\n" +
                "\t\t\t<div class=\"container\">\n" +
                "            <h3>Wifi Point Table</h3>\n" +
                "\t\t\t\t<table id='csvTable' cellpadding=\"0\" cellspacing=\"0\" border=\"0\" class=\"datatable table table-striped table-bordered\">\n" +
                "\t\t\t\t\t<thead>\n" +
                "\t\t\t\t\t\t<tr>\n" +
                "\t\t\t\t\t\t\t<tr><th>Time </th><th>ID</th><th>Lat</th><th>Lon</th><th>Alt</th><th>&quot;#WiFi networks&quot;</th><th>SSID1</th><th>MAC1</th><th>Frequncy1</th><th>Signal1</th><th>SSID2</th><th>MAC2</th><th>Frequncy2</th><th>Signal2</th><th>SSID3</th><th>MAC3</th><th>Frequncy3</th><th>Signal3</th><th>SSID4</th><th>MAC4</th><th>Frequncy4</th><th>Signal4</th><th>SSID5</th><th>MAC5</th><th>Frequncy5</th><th>Signal5</th><th>SSID6</th><th>MAC6</th><th>Frequncy6</th><th>Signal6</th><th>SSID7</th><th>MAC7</th><th>Frequncy7</th><th>Signal7</th><th>SSID8</th><th>MAC8</th><th>Frequncy8</th><th>Signal8</th><th>SSID9</th><th>MAC9</th><th>Frequncy9</th><th>Signal9</th><th>SSID10</th><th>MAC10</th><th>Frequncy10</th><th>Signal10</th></tr>\n" +
                "\t\t\t\t\t\t</tr>\n" +
                "\t\t\t\t\t</thead>\n" +
                "\t\t\t\t\t<tbody>";


        int countLines=0;
        for (WifiPointsTimePlace wifiPointsTimePlace : processedFile){
//            if(countLines>300){break;}
            allLines+= "\n\t" +"<tr>";
                for(String wifiSample : wifiPointsTimePlace.getHTMLWifiPoints()){
                    allLines+= "<td>"+wifiSample+"</td>";

                }
            allLines+= "</tr>";
            countLines++;
        }

        String outputTxt= allLines+"</tbody> </table> </div> </div>";
        try {
            File file = new File("tableHTML.txt");
            FileWriter fileWriter = new FileWriter(file);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.print(outputTxt);
            System.out.println("txt exported");
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
