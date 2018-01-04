package components.CSV_IO;

import components.Filters.Filter;
import de.micromata.opengis.kml.v_2_2_0.*;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The class is responsible for exporting a KML file from our converted CSV file
 * that was generated from the outputCSVWriter.ExportToCSV(processedFile) line
 * @author nissan
 */
public class KmlExporter {
    String csvFilePath, outKmlPathAndName;
    Kml kml;
    Document document;

    /**
     *
     * @param csvFilePath the csv file, which is the source for our export
     * @param outKmlPathAndName the destination of the kml path and name to export
     */
    public KmlExporter(String csvFilePath, String outKmlPathAndName) {
        this.csvFilePath = csvFilePath;
        this.outKmlPathAndName = outKmlPathAndName;
    }

    /**
     *
     * @param filter is the filter object from the Filter class that generated the filter according to its user input.
     * @return true is export was successful.
     */
    public boolean csvToKml(Filter... filter) {
        kml = new Kml();
        document = kml.createAndSetDocument();


        IconStyle iconStyle =new IconStyle();
        Icon icon = new Icon();
        icon.setHref("http://maps.google.com/mapfiles/kml/paddle/grn-circle.png");
        iconStyle.setScale(1.0);
        iconStyle.setIcon(icon);
        document.createAndAddStyle().withId("green").setIconStyle(iconStyle);

        Icon iconRed = new Icon();
        iconRed.setHref("http://maps.google.com/mapfiles/kml/paddle/red-circle.png");
//        iconRed.setHref("http://files.softicons.com/download/game-icons/super-mario-icons-by-sandro-pereira/png/256/Retro%20Mario%202.png");
        IconStyle iconStyleRed =new IconStyle();
        iconStyleRed.setScale(1.0);
        iconStyleRed.setIcon(iconRed);
        document.createAndAddStyle().withId("red").setIconStyle(iconStyleRed);

        Icon iconYellow = new Icon();
        iconYellow.setHref("http://maps.google.com/mapfiles/kml/paddle/ylw-circle.png");
        IconStyle iconStyleYellow =new IconStyle();
        iconStyleYellow.setScale(1.0);
        iconStyleYellow.setIcon(iconYellow);
        document.createAndAddStyle().withId("yellow").setIconStyle(iconStyleYellow);


        BufferedReader br = null;
        String line;
        String[] lineInformation;

        try {
            br = new BufferedReader(new FileReader(csvFilePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            br.readLine();//Throws away the header
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            while ((line = br.readLine()) != null) {
                // use comma as separator
                lineInformation = line.split(",");//Split each line to parts of properties
                boolean isAllConditionsTrue=true;
                for (Filter fil : filter){
                    if (!fil.checkLineByFilter(lineInformation)) {
                        isAllConditionsTrue=false;
                    }
                }
                if (isAllConditionsTrue)
                    printPointOnMap(lineInformation);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        kml.setFeature(document);

        try {
            kml.marshal(new File(outKmlPathAndName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false; //unsuccessful write
        }
        return true; //successful write

    }
        private void printPointOnMap(String[] CSVLine) {
            String onePoint = "";
            onePoint ="<name><![CDATA[" + CSVLine[0] + "]]></name>"
                    + "<description><![CDATA[#WiFi networks: <b>" +"#WiFi networks: "+ CSVLine[5] + "</b>\n<br>Date: " + CSVLine[0]
                    + "<table  border=\"1\" style=\"font-size:12px;\"> "
                    + "<tr>" +
                    "<td><b>Name</b></td>" +
                    "<td><b>BSSID</b></td>" +
                    "<td><b>Frequency</b></td>" +
                    "<td><b>Signal</b></td>" +
                    "</tr>";

            for (int i = 0; i < Integer.parseInt(CSVLine[5]); i++)
            {//write the property to format of kml
                try {
                    onePoint += "<tr>" + "<td>" + CSVLine[6 + 4 * i] + "</td>" + "<td>" + CSVLine[7 + 4 * i]
                            + "</td>" + "<td>" + CSVLine[8 + 4 * i] + "</td>" + "<td>" + CSVLine[9 + 4 * i]
                            + " </td>" + "</tr>";
                } catch (Exception e) {
                    System.out.println(i + "i,  Integer.parseInt(CSVLine[5]): " + Integer.parseInt(CSVLine[5]) + ",   CSVLine[6 + 4 * i]" + CSVLine[6 + 4 * (i-1)] + ",    Time:   " + CSVLine[0]);
                }
            }
            onePoint += "</table>"
//                    + "]]>"
                   + "</description>";
//                    + "<styleUrl>#"
//                    + theColorOfPoint(CSVLine[9])
//                    + "</styleUrl>"
//                    + "<Point>"
//                    + "<coordinates>"
//                    + CSVLine[3] +","
//                    + CSVLine[2]
//                    + "</coordinates>"
//                    + "</coordinates>"
//                    + "</Point>";


            //TODO test if withId is null wht happens
            document.createAndAddPlacemark().withTimePrimitive(createTimeStamp(CSVLine[0]))
                    .withName(CSVLine[6])
                    .withStyleUrl("#"+colorOfPoint(CSVLine[9]))
//                    .withStyleUrl("http://maps.google.com/mapfiles/ms/icons/green-dot.png")
                    .withDescription(onePoint)
                    .createAndSetPoint().addToCoordinates(Double.parseDouble(CSVLine[3]),Double.parseDouble(CSVLine[2] ));
    }

    private static String colorOfPoint(String str) {

        int RXLnumber = (int)(Double.parseDouble(str));
        String color = "";

        if(RXLnumber <= 0 && RXLnumber > -70)
        {
            color = "green";
        }
        else if(RXLnumber <= -70 && RXLnumber > -80)
        {
            color = "yellow";
        }
        else
            color = "red";


        return color;
    }

    private TimeStamp createTimeStamp(String time) {
        String dateFormatFile = "yyyy-MM-dd hh:mm:ss";
        Date lineDate=null;
        try {
            if (!time.isEmpty() )
                lineDate = new SimpleDateFormat(dateFormatFile).parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String yearstr = lineDate.getYear()+"/";
        String monthstr = lineDate.getMonth()+"/";
        String daystr = lineDate.getDate()+"/";


        String dateString = (lineDate.getYear()+1900)+"-"+(lineDate.getMonth()+1)+ "-" +(lineDate.getDate())
                +"T"+lineDate.getHours()+":"+lineDate.getMinutes()+":"
                +((lineDate.getSeconds()<10)? "0" : "")+lineDate.getSeconds()+"Z";

        TimeStamp timeStamp = new TimeStamp ();
        timeStamp.setWhen(dateString);

        return timeStamp;
    }


}
