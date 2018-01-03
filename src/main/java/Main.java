/**
 * Created by Nissan on 12/7/2017.
 */
import components.Algorithms.Algorithm2;
import components.Algorithms.WeightedArithmeticMean;
import components.Attributes.HashRouters;
import components.Attributes.WIFISample;
import components.Attributes.WIFIWeight;
import components.Attributes.WifiPointsTimePlace;
import components.CSV_IO.CSVReader;
import components.CSV_IO.CoboCSVReader;
import components.CSV_IO.KmlExporter;
import components.CSV_IO.OutputCSVWriter;
import components.Console_App.LineFilters;
import components.Filters.*;
import spark.Request;
import spark.Response;
import webserver.Save2CSV;
import webserver.SaveFilter;

import javax.servlet.MultipartConfigElement;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.function.Predicate;

import static spark.Spark.*;

public class Main{
    static String[] userPath;
    static int fileNum=0;
    static HashRouters<String,WIFISample> hashRouters;
    static Hashtable<String,HashRouters> usersHashRouters;
    static List<WifiPointsTimePlace> processedFile;
    static Hashtable<String,List<WifiPointsTimePlace>> usersProcessedFile;
    //TODO usersProccessedFiles

    public static void main(String[] args) throws InterruptedException {
        port(getHerokuAssignedPort());
        routeCSV();
        System.out.println("\nServer up!!! go to   http://localhost:4567/   for web app");
        usersHashRouters = new Hashtable<String,HashRouters>();



//
//        //===========================Watch Service============================================
//        WatchService watchService
//                = null;
//        try {
//            watchService = FileSystems.getDefault().newWatchService();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        Path path = Paths.get("UserFiles\\comboFolder");
//        Path path2 = Paths.get("UserFiles\\upload");
//
//
//        try {
//            path.register(
//                    watchService,
//                    StandardWatchEventKinds.ENTRY_CREATE,
//                    StandardWatchEventKinds.ENTRY_DELETE,
//                    StandardWatchEventKinds.ENTRY_MODIFY);
//            path2.register(
//                    watchService,
//                    StandardWatchEventKinds.ENTRY_CREATE,
//                    StandardWatchEventKinds.ENTRY_DELETE,
//                    StandardWatchEventKinds.ENTRY_MODIFY);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        WatchKey key;
//        while ((key = watchService.take()) != null) {
//            for (WatchEvent<?> event : key.pollEvents()) {
//                    System.out.println("do something");
//                System.out.println(
//                        "Event kind:" + event.kind()
//                                + ". File affected: " + event.context() + ".");
//            }
//            key.reset();
//        }
//        //===========================End of Watch Service============================================

    }

    public static void routeCSV(){
        staticFiles.location("/pages");
        new File("UserFiles").mkdir();
        new File("UserFiles/upload").mkdir();
        // create the upload directory if it doesn't exist
        new File("UserFiles/output").mkdir();
        new File("UserFiles/comboFolder").mkdir();
        new File("UserFiles/filteredOutput").mkdir();
        new File("UserFiles/KmlOutput").mkdir();
        usersProcessedFile = new Hashtable<>();
        processedFile = new ArrayList<>();

        get("/filter", (req, res) ->{
            Predicate finalPredicate = WebsiteFilter.filter(req,res);
            System.out.println("predicate "+finalPredicate.test(usersProcessedFile.get(req.cookie("user")).get(1)) );
            new File("UserFiles/filteredOutput/"+req.cookie("user")).mkdir();

            HashRouters<String,WIFISample> currnetHashRouter= Save2CSV.save2csvWithPredicate("UserFiles/upload/"+req.cookie("user")
                    ,"UserFiles/filteredOutput/"+req.cookie("user"),finalPredicate);

            usersHashRouters.put(req.cookie("user"),currnetHashRouter);
            return "filtered";
        });

        get("/saveFilter", (req, res) ->{
            System.out.println("starting saved filter");
            return SaveFilter.saveFilter(req, res);
        });

        get("/clearData", (req, res) ->{

            File filesToDelete = new File("UserFiles/upload/"+req.cookie("user"));
            //websource:  https://stackoverflow.com/questions/20281835/how-to-delete-a-folder-with-files-using-java
            String[]entries = filesToDelete.list();
            if(entries != null) {
                for (String s : entries) {
                    File currentFile = new File(filesToDelete.getPath(), s);
                    System.out.println("deleted " + currentFile.getName());
                    currentFile.delete();
                }
            }

            filesToDelete = new File("UserFiles/comboFolder/"+req.cookie("user"));
            entries = filesToDelete.list();
            if (filesToDelete.list()!=null) {
                for (String s : entries) {
                    File currentFile = new File(filesToDelete.getPath(), s);
                    System.out.println("deleted " + currentFile.getName());
                    currentFile.delete();
                }
            }

            //TODO check is this is the solution
//            usersHashRouters.remove(req.cookie("user"));
//            usersProcessedFile.remove(req.cookie("user"));
            usersHashRouters.put(req.cookie("user"),new HashRouters());
            usersProcessedFile.put(req.cookie("user"),new ArrayList<>());
            processedFile = new ArrayList<>();
            return "deleted uploaded files";


        });

        get("/login", (req, res) ->{
            String userName = req.queryString();
            System.out.println(userName+" - has logged in");
            res.cookie("/", "user", userName+"", 3600, false, false);
            return userName;

        });

        get("/getCSVTable", (req, res) ->{
             String content = new String(Files.readAllBytes(Paths.get("tableHTML.txt")));
            return content;

        });

        get("/save2csv", (req, res) ->{

            System.out.println("============================================");
            File file = new File("UserFiles/upload/"+req.cookie("user"));
            new File("UserFiles/output/"+req.cookie("user")).mkdir();
            // create the upload directory if it doesn't exist


            //===================================================
            processedFile = new ArrayList<>();
            usersHashRouters.put(req.cookie("user"),new HashRouters());
            usersProcessedFile.put(req.cookie("user"), processedFile);//Because that each time the program read all the files again we need to delete the old files fot dont append the new files on the old files

            File comboFiles = new File("UserFiles/comboFolder/"+req.cookie("user"));

            System.out.println(comboFiles.exists());
            if (comboFiles.exists()) {
                if (comboFiles.list().length > 0) {
                    for (File fileInCombo : comboFiles.listFiles()) {
                        try {
                            System.out.println(fileInCombo.getPath() + "");
                            if (usersHashRouters.get(req.cookie("user")) == null)
                                usersHashRouters.put(req.cookie("user"),new HashRouters());
                            processedFile.addAll(CoboCSVReader.readCsvFile(fileInCombo.getPath() + "", usersHashRouters.get(req.cookie("user"))));
                            usersProcessedFile.put(req.cookie("user"), processedFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            if (usersHashRouters.get(req.cookie("user")) == null)
                usersHashRouters.put(req.cookie("user"),new HashRouters());

            String userName = req.cookie("user");
            //================================================
             if(file.list() != null && file.list().length>0){
//            if(file.list().length>0 || comboFiles.list().length>0){
                System.out.println("saving to csv output");
//                String userName = req.cookie("user");
                 System.out.println(userName);
                 HashRouters<String,WIFISample> temp = usersHashRouters.get(userName);
                 temp.mergeToHash(Save2CSV.save2csv("UserFiles/upload/"+userName,"UserFiles/output/"+userName));
                //HashRouters<String,WIFISample> currnetHashRouter= Save2CSV.save2csv("UserFiles/upload/"+req.cookie("user"),"UserFiles/output/"+req.cookie("user"));
                usersHashRouters.put(userName,usersHashRouters.get(userName));
//                hashRouters = Save2CSV.save2csv("upload/"+req.cookie("user"),"output/"+req.cookie("user"));

                //==========================================added 12-31-17
                List<File> selectedFiles= new ArrayList<>();
                File uploads = new File("UserFiles/upload/"+userName);
                for(File file2 : uploads.listFiles()){
                    selectedFiles.add(file2);
                }

                OutputCSVWriter outputCSVWriter = new OutputCSVWriter(selectedFiles);

                processedFile.addAll(outputCSVWriter.sortAndMergeFiles());
                usersProcessedFile.put(userName,processedFile);
                System.out.println(userName);

                //==========================================added 12-31-17 - end
                System.out.println("processed file size:"+ usersProcessedFile.get(userName).size());
                System.out.println("hash routers file size:"+ usersHashRouters.get(userName).getCountOfRouters());

//                 processedFile = usersProcessedFile.get(userName);

//                OutputCSVWriter.ExportToCSV(usersProcessedFile.get(userName),"UserFiles/output/"+userName+"/testOutputCSV.csv",null);
//
//
//                return "true,"+userName+","+usersProcessedFile.get(userName).size()+","
//                        +usersHashRouters.get(userName).getCountOfRouters();
//                return "true,nis";
             }

             OutputCSVWriter.ExportToCSV(usersProcessedFile.get(userName),"UserFiles/output/"+userName+"/OutputCSV.csv",null);

             return "true,"+userName+","+usersProcessedFile.get(userName).size()+","
                    +usersHashRouters.get(userName).getCountOfRouters();
//             else if (comboFiles.list().length>0){
//                 System.out.println("saving to csv output");
//                 usersHashRouters.get(req.cookie("user")).mergeToHash(Save2CSV.save2csv("UserFiles/upload/"+req.cookie("user"),"UserFiles/output/"+req.cookie("user")));
//                 //HashRouters<String,WIFISample> currnetHashRouter= Save2CSV.save2csv("UserFiles/upload/"+req.cookie("user"),"UserFiles/output/"+req.cookie("user"));
//                 usersHashRouters.put(req.cookie("user"),usersHashRouters.get(req.cookie("user")));
////                hashRouters = Save2CSV.save2csv("upload/"+req.cookie("user"),"output/"+req.cookie("user"));
//
//                 //==========================================added 12-31-17
//                 List<File> selectedFiles= new ArrayList<>();
//                 for(File file2 : comboFiles.listFiles()){
//                     selectedFiles.add(file2);
//                 }
//
////                 OutputCSVWriter outputCSVWriter = new OutputCSVWriter(selectedFiles);
//                 for (File comboSingleFile : comboFiles.listFiles()){
//                     processedFile.addAll(CoboCSVReader.readCsvFile(comboSingleFile.getPath(),usersHashRouters.get(req.cookie("user"))));
//
//                 }
//
//
////                 processedFile.addAll(outputCSVWriter.sortAndMergeFiles());
//                 usersProcessedFile.put(req.cookie("user"),processedFile);
//                 System.out.println(req.cookie("user"));
//
//                 //==========================================added 12-31-17 - end
//                 System.out.println("processed file size:"+ usersProcessedFile.get(req.cookie("user")).size());
//                 System.out.println("hash routers file size:"+ usersHashRouters.get(req.cookie("user")).getCountOfRouters());
//                 return "true,"+req.cookie("user")+","+usersProcessedFile.get(req.cookie("user")).size()+","
//                         +usersHashRouters.get(req.cookie("user")).getCountOfRouters();
////                return "true,nis";
//
//             }
//             else{
//                System.out.println("cannot save to output csv");
//                System.out.println("Directory is empty!");
//                return "false,"+req.cookie("user");
//                }
//
////            hashRouters = Save2CSV.save2csv();

        });

        get("/submitAlgo1", (req, res) ->{
            if(usersHashRouters.containsKey(req.cookie("user")==null)){
                throw new Exception("no hash routers");
            }
            System.out.println("==============Algo 1======================");
            String[] queries = req.queryString().split(",");
            System.out.println(queries[0]);
            WeightedArithmeticMean weightedArithmeticMean = new WeightedArithmeticMean(usersHashRouters.get(req.cookie("user")));
            WIFIWeight ww= weightedArithmeticMean.getWAMbyMac(queries[0]);
            if(ww!=null) {
                System.out.print("Lat:"+ww.getWIFI_Lat()+", ");
                System.out.print("Lon:"+ww.getWIFI_Lon()+", ");
                System.out.print("Alt:"+ww.getWIFI_Alt()+", ");

                Double d = ww.getWIFI_Alt();
                String[] splitter = d.toString().split("\\.");
                if (splitter[1].length() <= 1)
                    return (ww.getWIFI_Lat()+"").substring(0,8)+" ,"+(ww.getWIFI_Lon()+"").substring(0,8)+" ,"+((ww.getWIFI_Alt())+"0")+" ,"+ ww.getWIFI_MAC() ;
                else
                    return (ww.getWIFI_Lat()+"").substring(0,8)+" ,"+(ww.getWIFI_Lon()+"").substring(0,8)+" ,"+((ww.getWIFI_Alt())+"").substring(0,8)+" ,"+ ww.getWIFI_MAC();
            }
//            return bootstrapCSV();
//            return "Lat is: "+(ww.getWIFI_Lat()+"").substring(0,6)+"  ,Lon is: "+(ww.getWIFI_Lon()+"").substring(0,6)+"  ,Alt is: "+(ww.getWIFI_Alt()+"").substring(0,6) ;

            return  "0,0,0";
        });

        get("/submitAlgo2", (req, res) ->{
            if(usersHashRouters.containsKey(req.cookie("user")==null)){
                throw new Exception("no hash routers");
            }
            System.out.println("============================================");
            String[] queries = req.queryString().split(",");
            System.out.println(queries[0]+", "+queries[1]+", "+queries[2]+", "+queries[3]+", "+queries[4]+", "+queries[5]);

            ArrayList<WIFIWeight> userInput = new ArrayList<>();
            userInput.add(new WIFIWeight(queries[0],0,0,0,Integer.parseInt(queries[3]),0));
            userInput.add(new WIFIWeight(queries[1],0,0,0,Integer.parseInt(queries[4]),0));
            userInput.add(new WIFIWeight(queries[2],0,0,0,Integer.parseInt(queries[5]),0));

//            List<File> selectedFiles= new ArrayList<>();
//            File uploads = new File("UserFiles/upload");
//            for(File file : uploads.listFiles()){
//                selectedFiles.add(file);
//            }
//
//            OutputCSVWriter outputCSVWriter = new OutputCSVWriter(selectedFiles);
//
//            //processedFile =  outputCSVWriter.sortAndMergeFiles();
//            usersProcessedFile.put(req.cookie("user"),outputCSVWriter.sortAndMergeFiles());

            List<WIFIWeight> kLineMostSimilar = Algorithm2.getKMostSimilar(usersProcessedFile.get(req.cookie("user")), userInput, 3);

            WeightedArithmeticMean weightedArithmeticMean = new WeightedArithmeticMean(usersHashRouters.get(req.cookie("user")) );

            WIFIWeight ww = weightedArithmeticMean.getWamByList(kLineMostSimilar);

            if (ww!=null){
                System.out.println(ww.getWIFI_Lat());
                System.out.println(ww.getWIFI_Lon());
                System.out.println(ww.getWIFI_Alt());

                Double d = ww.getWIFI_Alt();
                String[] splitter = d.toString().split("\\.");
                if (splitter[1].length() <= 1)
                    return (ww.getWIFI_Lat()+"").substring(0,6)+" ,"+(ww.getWIFI_Lon()+"").substring(0,6)+" ,"+((ww.getWIFI_Alt())+"0")+" ,"+ ww.getWIFI_MAC() ;
                else
                    return (ww.getWIFI_Lat()+"").substring(0,6)+" ,"+(ww.getWIFI_Lon()+"").substring(0,6)+" ,"+((ww.getWIFI_Alt())+"").substring(0,6)+" ,"+ ww.getWIFI_MAC();
            }

            return  "0,0,0";

        });

        get("/submitComboWithQuestionMarksAlgo2", (req, res) ->{
            if(usersHashRouters.containsKey(req.cookie("user")==null)){
                throw new Exception("no hash routers");
            }
            System.out.println("============================================");
            String[] queries = req.queryString().split(",");

            //=========================================================
            ArrayList<WIFIWeight> userInput = new ArrayList<WIFIWeight>();

            ArrayList<WIFIWeight> listOfWIFIWeightsUsingAlgo2 = new ArrayList<>();//Hold locations of all lines of the combination without location CSV File

            //Read the combination-without-location-CSV-File and inserts all line to the ArrayList<ArrayList<WIFIWeight>>.
            //the innter ArrayList<WIFIWeight> hold one line of combination-without-location-CSV-File. and the external ArrayList hold all of lines.
            ArrayList<ArrayList<WIFIWeight>> listOfCombinationCsvLines = CSVReader.readCombinationCsvFile("combo.csv");
            for(ArrayList<WIFIWeight> line : listOfCombinationCsvLines) {
                //run algorithm 2 on each line, get the WIFIWeight of each line and insert to ArrayList.
                List<WifiPointsTimePlace> newPFile = usersProcessedFile.get(req.cookie("user"));
                Algorithm2.getKMostSimilar(newPFile, line, 3);//delete
                    ArrayList<WIFIWeight> kLineMostSimilar = Algorithm2.getKMostSimilar(usersProcessedFile.get(req.cookie("user")), line, 3);
                        WeightedArithmeticMean weightedArithmeticMean = new WeightedArithmeticMean(usersHashRouters.get(req.cookie("user")));
                        WIFIWeight ww = weightedArithmeticMean.getWamByList(kLineMostSimilar);

                        listOfWIFIWeightsUsingAlgo2.add(ww);
            }

            //Getting all lines of combination-without-location-CSV-File and insert the new locations and export to new file
            try {
                List<WifiPointsTimePlace> s = CoboCSVReader.readCsvFile("combo.csv", usersHashRouters.get(req.cookie("user")));
                OutputCSVWriter.changeLocationOfFile(listOfWIFIWeightsUsingAlgo2,s, "afterAlgo2.csv");
            }
            catch (IOException e)
            {
            }

            //=========================================================
            WIFIWeight ww = listOfWIFIWeightsUsingAlgo2.get(0);

            if (ww!=null){
                System.out.println(ww.getWIFI_Lat());
                System.out.println(ww.getWIFI_Lon());
                System.out.println(ww.getWIFI_Alt());

                Double d = ww.getWIFI_Alt();
                String[] splitter = d.toString().split("\\.");
                if (splitter[1].length() <= 1)
                    return (ww.getWIFI_Lat()+"").substring(0,6)+" ,"+(ww.getWIFI_Lon()+"").substring(0,6)+" ,"+((ww.getWIFI_Alt())+"0")+" ,"+ ww.getWIFI_MAC() ;
                else
                    return (ww.getWIFI_Lat()+"").substring(0,6)+" ,"+(ww.getWIFI_Lon()+"").substring(0,6)+" ,"+((ww.getWIFI_Alt())+"").substring(0,6)+" ,"+ ww.getWIFI_MAC();
            }

            return  "0,0,0";

        });

        get("/", (req, res) ->{

            String userName = "user"+(int)(Math.random()*1000);
            System.out.println("hello "+userName);
            res.cookie("/", "user", userName+"", 3600, false, false);
//            req.session().attribute("user",userName+"");
//            System.out.println("Session user  : "+req.session().attribute("user") );
//            res.redirect("app2.html?"+userName);
            res.redirect("app2.html");
                    return "hello user" +userName;
        });




        post("/sendFiles", (Request req, Response res) -> {
            //make sub folder for user
            File userUploadDir = new File("UserFiles/upload/"+req.cookie("user"));
            userUploadDir.mkdir(); // create the upload directory if it doesn't exist

//            Path tempFile = Files.createTempFile(uploadDir.toPath(), "submittedFile", ".csv");
            System.out.println("uploading files");

            req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));

            String[] splits = req.body().split("------WebKitFormBoundary");

            for(String fileparser: splits){
                if (fileparser.contains(".csv")){
                    int index =fileparser.indexOf("WigleWifi-");
//                    System.out.println(fileparser );
//                    PrintWriter printWriter = new PrintWriter("upload/uploadFile"+fileNum+".csv");


                    String fileToOutput = fileparser.substring(index);
                    try(PrintWriter writeToFile = new PrintWriter(new BufferedWriter(new FileWriter("UserFiles/upload/"+req.cookie("user")+"/"+fileNum+".csv", true)))) {
                        writeToFile.println(fileToOutput);
                    }catch (IOException e) {
                        System.err.println(e);
                    }


                    fileNum++;
//                    printWriter.println(fileparser.substring(index));
//                    System.out.println("\n\n\n\n\n\nend of file:"+fileNum+"\n\n\n\n\n\n\n\n\n\n");
                }
            }
            System.out.println(fileNum + " is total num of uploading files");

            //======================end of my code=========================
//            try (InputStream input = req.raw().getPart("uploaded_file").getInputStream()) { // getPart needs to use same "name" as input field in form
//                Files.copy(input, tempFile, StandardCopyOption.REPLACE_EXISTING);
//
//            }
            res.status(200);
            res.redirect("app2.html");
            return req.cookie("user")+"";
//            return "sent files";
        });

        post("/sendComboFile", (Request req, Response res) -> {
            //make sub folder for user
            File userUploadDir = new File("UserFiles/comboFolder/"+req.cookie("user"));
            userUploadDir.mkdir(); // create the upload directory if it doesn't exist

//            Path tempFile = Files.createTempFile(uploadDir.toPath(), "submittedFile", ".csv");
            System.out.println("uploading files");

            req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));

            String[] splits = req.body().split("------WebKitFormBoundary");

            for(String fileparser: splits){
                if (fileparser.contains(".csv")){
                    int index =fileparser.indexOf("Time,ID,Lat,Lon,Alt,");
//                    System.out.println(fileparser );
//                    PrintWriter printWriter = new PrintWriter("upload/uploadFile"+fileNum+".csv");


                    String fileToOutput = fileparser.substring(index);
                    try(PrintWriter writeToFile = new PrintWriter(new BufferedWriter(new FileWriter("UserFiles/comboFolder/"+req.cookie("user")+"/"+fileNum+".csv", true)))) {
                        writeToFile.println(fileToOutput);
                    }catch (IOException e) {
                        System.err.println(e);
                    }


                    fileNum++;
//                    printWriter.println(fileparser.substring(index));
//                    System.out.println("\n\n\n\n\n\nend of file:"+fileNum+"\n\n\n\n\n\n\n\n\n\n");
                }
            }
            System.out.println(fileNum + " is total num of uploading files");

            //======================end of my code=========================
//            try (InputStream input = req.raw().getPart("uploaded_file").getInputStream()) { // getPart needs to use same "name" as input field in form
//                Files.copy(input, tempFile, StandardCopyOption.REPLACE_EXISTING);
//
//            }
            res.status(200);
            res.redirect("app2.html");
            return req.cookie("user")+"";
//            return "sent files";
        });

        get("/Save2kml", (req, res) ->{
            Filter filter = new Filter(0);
            File kmlFolder = new File("UserFiles/KmlOutput//"+req.cookie("user"));

            if (!kmlFolder.exists())
                new File("UserFiles/KmlOutput//"+req.cookie("user")).mkdir();

            KmlExporter kmlExporter = new KmlExporter("UserFiles\\output\\"+req.cookie("user")+"\\OutputCSV.csv","UserFiles\\KmlOutput\\"+req.cookie("user")+"\\KML1.kml");
            if(kmlExporter.csvToKml(filter) )
                System.out.println("successful export kml");
            else
                System.out.println("failure to export kml");
            return "success";

        });
    }
    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }
}

