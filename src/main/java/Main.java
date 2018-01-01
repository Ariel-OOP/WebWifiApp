/**
 * Created by Nissan on 12/7/2017.
 */
import components.Algorithms.Algorithm2;
import components.Algorithms.WeightedArithmeticMean;
import components.Attributes.HashRouters;
import components.Attributes.WIFISample;
import components.Attributes.WIFIWeight;
import components.Attributes.WifiPointsTimePlace;
import components.CSV_IO.OutputCSVWriter;
import components.Filters.DevicePredicate;
import components.Filters.PlacePredicate;
import components.Filters.TimePredicate;
import spark.Request;
import spark.Response;
import webserver.Save2CSV;

import javax.servlet.MultipartConfigElement;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.function.Predicate;

import static spark.Spark.*;

public class Main {
    static String[] userPath;
    static int fileNum=0;
    static HashRouters<String,WIFISample> hashRouters;
    static Hashtable<String,HashRouters> usersHashRouters;
    static List<WifiPointsTimePlace> processedFile;
    //TODO usersProccessedFiles

    public static void main(String[] args) {
        port(getHerokuAssignedPort());
        routeCSV();
        System.out.println("\nServer up!!! go to   http://localhost:4567/   for web app");
        usersHashRouters = new Hashtable<String,HashRouters>();
    }

    public static void routeCSV(){
        staticFiles.location("/pages");

        File uploadDir = new File("upload");
        uploadDir.mkdir(); // create the upload directory if it doesn't exist

        File createOutputDir = new File("output");
        createOutputDir.mkdir(); // create the upload directory if it doesn't exist

        get("/filter", (req, res) ->{
            String userName = req.queryString();
            System.out.println("Logic 1:"+req.cookie("Logic"));
            System.out.println("filter 1:"+req.cookie("filter1"));
            String filter1 = req.cookie("filter1");
            System.out.println("filter "+filter1);
            String[] inputsFilter1 = req.cookie("input1").split("&");
            System.out.println(inputsFilter1[0]);
            Predicate<WifiPointsTimePlace> predicate1 = null; //mean time
            Predicate<WifiPointsTimePlace> predicate2 = null; //mean time
            Predicate<WifiPointsTimePlace> finalPredicate = null; //mean time

            switch (filter1){
                case "Location":
                    predicate1 = new PlacePredicate(Double.parseDouble(inputsFilter1[0]),Double.parseDouble(inputsFilter1[1]),
                            Double.parseDouble(inputsFilter1[2]),Double.parseDouble(inputsFilter1[3]) );
                    break;
                case "Device":
                    predicate1 = new DevicePredicate(inputsFilter1[0]);
                    break;
                case "Time":
                    predicate1 = new TimePredicate(inputsFilter1[0],inputsFilter1[1]);
                    break;
            }
            if (req.cookie("negate1").equals("true"))
                predicate1 = predicate1.negate();
            // if there is logic
            if (req.cookie("Logic").equals("And") || req.cookie("Logic").equals("Or")) {
                String filter2 = req.cookie("filter2");
                String[] inputsFilter2 = req.cookie("input2").split("&");

                switch (filter2) {
                    case "Location":
                        predicate2 = new PlacePredicate(Double.parseDouble(inputsFilter2[0]), Double.parseDouble(inputsFilter2[1]),
                                Double.parseDouble(inputsFilter2[2]), Double.parseDouble(inputsFilter2[3]));
                        break;
                    case "Device":
                        predicate2 = new DevicePredicate(inputsFilter2[0]);
                        break;
                    case "Time":
                        predicate2 = new TimePredicate(inputsFilter2[0], inputsFilter2[1]);
                        break;
                }
                if (req.cookie("negate2").equals("true"))
                    predicate2 = predicate2.negate();

                String logicSymbol = req.cookie("Logic");
                if (logicSymbol.equals("And")){
                    finalPredicate = predicate1.and(predicate2);
                }else if (logicSymbol.equals("Or")){
                    finalPredicate = predicate1.or(predicate2);
                }
            }
            if (predicate2==null)
                finalPredicate = predicate1;
            System.out.println("predicate "+finalPredicate.test(processedFile.get(1)) );
            System.out.println("predicate "+finalPredicate.test(processedFile.get(3)) );
            return "filtered";

        });

        get("/clearData", (req, res) ->{
            File filesToDelete = new File("upload/"+req.cookie("user"));
            //websource:  https://stackoverflow.com/questions/20281835/how-to-delete-a-folder-with-files-using-java
            String[]entries = filesToDelete.list();
            for(String s: entries){
                File currentFile = new File(filesToDelete.getPath(),s);
                System.out.println("deleted "+currentFile.getName());
                currentFile.delete();
            }
            //TODO check is this is the solution
            usersHashRouters.remove(req.cookie("user"));
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
            File file = new File("upload/"+req.cookie("user"));
            File outputDir = new File("output/"+req.cookie("user"));
            outputDir.mkdir(); // create the upload directory if it doesn't exist

            if(file.list().length>0){
                System.out.println("saving to csv output");
                HashRouters<String,WIFISample> currnetHashRouter= Save2CSV.save2csv("upload/"+req.cookie("user"),"output/"+req.cookie("user"));
                usersHashRouters.put(req.cookie("user"),currnetHashRouter);
//                hashRouters = Save2CSV.save2csv("upload/"+req.cookie("user"),"output/"+req.cookie("user"));

                //==========================================added 12-31-17
                List<File> selectedFiles= new ArrayList<>();
                File uploads = new File("upload\\"+req.cookie("user"));
                for(File file2 : uploads.listFiles()){
                    selectedFiles.add(file2);
                }

                OutputCSVWriter outputCSVWriter = new OutputCSVWriter(selectedFiles);

                processedFile =outputCSVWriter.sortAndMergeFiles();

                //==========================================added 12-31-17 - end

                return "true,"+req.cookie("user");
            }else{
                System.out.println("cannot save to output csv");
                System.out.println("Directory is empty!");
                return "false,"+req.cookie("user");
                }

//            hashRouters = Save2CSV.save2csv();



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
                    return (ww.getWIFI_Lat()+"").substring(0,6)+" ,"+(ww.getWIFI_Lon()+"").substring(0,6)+" ,"+((ww.getWIFI_Alt())+"0")+" ,"+ ww.getWIFI_MAC() ;
                else
                    return (ww.getWIFI_Lat()+"").substring(0,6)+" ,"+(ww.getWIFI_Lon()+"").substring(0,6)+" ,"+((ww.getWIFI_Alt())+"").substring(0,6)+" ,"+ ww.getWIFI_MAC();
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

            List<File> selectedFiles= new ArrayList<>();
            File uploads = new File("upload");
            for(File file : uploads.listFiles()){
                selectedFiles.add(file);
            }

            OutputCSVWriter outputCSVWriter = new OutputCSVWriter(selectedFiles);

            processedFile =  outputCSVWriter.sortAndMergeFiles();

            List<WIFIWeight> kLineMostSimilar = Algorithm2.getKMostSimilar(processedFile, userInput, 3);

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
            File userUploadDir = new File("upload/"+req.cookie("user"));
            userUploadDir.mkdir(); // create the upload directory if it doesn't exist

//            Path tempFile = Files.createTempFile(uploadDir.toPath(), "submittedFile", ".csv");
            System.out.println("uploading files");

            req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));

            String[] splits = req.body().split("------WebKitFormBoundary");

            for(String fileparser: splits){
                if (fileparser.contains(".csv")){
                    int index =fileparser.indexOf("WigleWifi-");
//                    System.out.println(fileparser.substring(index) );
//                    PrintWriter printWriter = new PrintWriter("upload/uploadFile"+fileNum+".csv");


                    String fileToOutput = fileparser.substring(index);
                    try(PrintWriter writeToFile = new PrintWriter(new BufferedWriter(new FileWriter("upload/"+req.cookie("user")+"/"+fileNum+".csv", true)))) {
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
    }
    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }
}

