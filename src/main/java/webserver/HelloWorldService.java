package webserver;

/**
 * Created by Nissan on 12/7/2017.
 */
import components.Attributes.HashRouters;
import components.Attributes.WIFISample;
import components.Attributes.WIFIWeight;
import components.Algorithms.WeightedArithmeticMean;
import spark.Request;
import spark.Response;

import javax.servlet.MultipartConfigElement;
import java.io.File;
import java.io.PrintWriter;

import static spark.Spark.*;

public class HelloWorldService {
    static String[] userPath;
    static int fileNum=0;
    static HashRouters<String,WIFISample> hashRouters;

    public static void main(String[] args) {
        routeCSV();
        System.out.println("\nServer up!!! go to   http://localhost:4567/app    for web app");

    }

    public static void routeCSV(){
        staticFiles.location("/pages");

        File uploadDir = new File("upload");
        uploadDir.mkdir(); // create the upload directory if it doesn't exist

        get("/app/save2csv", (req, res) ->{

            System.out.println("============================================");
            //TODO changed save2csv args
            hashRouters = Save2CSV.save2csv("","");
            return "";
        });

        get("/app/submitAlgo1", (req, res) ->{
            if(hashRouters==null){
                throw new Exception("no hash routers");
            }
            System.out.println("============================================");
            String[] queries = req.queryString().split(",");
            System.out.println(queries[0]);
            WeightedArithmeticMean weightedArithmeticMean = new WeightedArithmeticMean(hashRouters);
            WIFIWeight ww= weightedArithmeticMean.getWAMbyMac(queries[0]);
            if(ww!=null) {
                System.out.println(ww.getWIFI_Lat());
                System.out.println(ww.getWIFI_Lon());
                System.out.println(ww.getWIFI_Alt());
                res.cookie("mac",queries[0]+"");
                //TODO: added .0000001 to Alt, for sometimes its x.0 and sends as int and javascript cant parseFloat it
                Double d = ww.getWIFI_Alt();
                String[] splitter = d.toString().split("\\.");
                if (splitter[1].length() <= 1)
                    return (ww.getWIFI_Lat()+"").substring(0,6)+" ,"+(ww.getWIFI_Lon()+"").substring(0,6)+" ,"+((ww.getWIFI_Alt())+"0") ;
                else
                    return (ww.getWIFI_Lat()+"").substring(0,6)+" ,"+(ww.getWIFI_Lon()+"").substring(0,6)+" ,"+((ww.getWIFI_Alt())+"").substring(0,6) ;
            }
//            return bootstrapCSV();
//            return "Lat is: "+(ww.getWIFI_Lat()+"").substring(0,6)+"  ,Lon is: "+(ww.getWIFI_Lon()+"").substring(0,6)+"  ,Alt is: "+(ww.getWIFI_Alt()+"").substring(0,6) ;
            res.cookie("mac","not valid mac");
            return  "0,0,0";

        });

        get("/app", (req, res) ->{
//                    res.redirect("app2.html");
            System.out.println(req.queryString());

//            System.out.println(req.queryString().split(","));
                    return bootstrapCSV();
//                    return "";
        });


        post("/app", (Request req, Response res) -> {


//            if(str!=null ){
//                res.cookie("/", "name", str, 3600, false, true);
//            }

//            Path tempFile = Files.createTempFile(uploadDir.toPath(), "submittedFile", ".csv");
            System.out.println("it works");

            req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));

            String[] splits = req.body().split("------WebKitFormBoundary");
//            System.out.println(Arrays.asList(splits));
//            System.out.println(splits[1]+"\n\n\n\n\n\n");
//            System.out.println(splits[3]);

//            System.out.println(req.body()+"\n\n\n\n\n\n\n"); //gives all the files in one string
            //======================i addd yesterday=========================

            for(String fileparser: splits){
                if (fileparser.contains(".csv")){
                    int index =fileparser.indexOf("WigleWifi-");
                    System.out.println(fileparser.substring(index) );
                    PrintWriter out = new PrintWriter("upload/uploadFile"+fileNum+".csv");
                    fileNum++;
                    out.println(fileparser.substring(index));
                    System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
                }

            }
            //======================end of my code=========================

//            try (InputStream input = req.raw().getPart("uploaded_file").getInputStream()) { // getPart needs to use same "name" as input field in form
//                Files.copy(input, tempFile, StandardCopyOption.REPLACE_EXISTING);
//
//            }

            res.status(200);
//            res.cookie("/", "threadName", "thread1", 3600, false, true);
//            res.redirect("index.html"); //sends this page


            return bootstrapCSV();


        });

    }

    public static String templateCSV(){


        return  "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <script\n" +
                "            src=\"https://code.jquery.com/jquery-2.2.4.min.js\"\n" +
                "            integrity=\"sha256-BbhdlvQf/xTY9gja0Dq3HiwQF8LaCRTXxZKRutelT44=\"\n" +
                "            crossorigin=\"anonymous\">\n" +
                "    </script>\n" +
                "</head>\n" +
                "<body>\n" +
                "<form method=\"post\" enctype=\"multipart/form-data\" style=\"padding:10px 5px 15px\">\n" +
                "    <input type=\"file\" name=\"uploaded_file\" accept=\".csv\">\n" +
                "    <button>Upload csv</button></form>\n" +
                "<form method=\"post\" enctype=\"multipart/form-data\" style=\"padding:10px 5px 15px\">\n" +
                "    <input type=\"file\" id=\"ctrl\" name=\"uploaded_folder\" webkitdirectory=\"\" directory=\"\" multiple=\"\">\n" +
                "    <button>Upload folder</button>\n" +
                "</form>\n" +
                "<form method=\"post\" enctype=\"multipart/form-data\" style=\"padding:10px 5px 15px\">\n" +
                "    <input type=\"text\" id=\"name\" >\n" +
                "    <input type=\"text\" id=\"bid\" >\n" +
                "    <button id=\"submit\">Submit Text</button>\n" +
                "</form>\n" +
                "\n" +
                "<div id='output' style='border: dashed 1px green; margin: 10px; padding: 5px'>\n" +
                "</div>\n" +
                "\n" +
                "\n" +
                "<script>\n" +
                "    $(\"button#submit\").click(() => {\n" +
                "        var name  = $(\"input#name\").val();\n" +
                "        var bid   = $(\"input#bid\").val();\n" +
                "        alert(name)\n" +
                "        alert(bid)\n" +
                "        var json1={\"name\":name,\n" +
                "            \"bid\":bid,\n" +
                "        };\n" +
                "    $.ajax(\n" +
                "        {\n" +
                "            \"url\": encodeURI(\"/app?\" + name + \",\" + bid)\n" +
                "        }\n" +
                "    ).then(\n" +
                "        function(output) {\n" +
                "            $(\"div#output\").html(output)\n" +
                "            $(\"div#output div\").first().attr(\"style\", \"background:yellow; font-weight:900\")\n" +
                "        }\n" +
                "    );\n" +
                "    return false\n" +
                "    })\n" +
                "\n" +
                "</script>";

    }

    public static String bootstrapCSV(){

        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "  <head>\n" +
                "     <!-- Optional theme -->\n" +
                "      <link href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\" rel=\"stylesheet\" integrity=\"sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u\" crossorigin=\"anonymous\">\n" +
                "     <!-- Latest compiled and minified JavaScript -->\n" +
                "     <script\n" +
                "       src=\"https://code.jquery.com/jquery-2.2.4.min.js\"\n" +
                "       integrity=\"sha256-BbhdlvQf/xTY9gja0Dq3HiwQF8LaCRTXxZKRutelT44=\"\n" +
                "       crossorigin=\"anonymous\">\n" +
                "     </script>\n" +
                "     <script src=\"https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.3.0/Chart.bundle.js\"></script>\n" +
                "\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <title>Wigle WIFI extracter</title>\n" +
                "<style>\n" +
                "\n" +
                "</style>\n" +
                "\n" +
                "  </head>\n" +
                "  <body>\n" +
                "\n" +
                "  <!-- WebSource: https://github.com/johnwargo/bootstrap-navbar-complete/blob/master/index.html ,I modified it a bit-->\n" +
                "    <!-- Fixed navbar -->\n" +
                "    <div class=\"navbar navbar-default navbar-fixed-top\" role=\"navigation\">\n" +
                "      <div class=\"container\">\n" +
                "        <div class=\"navbar-header\">\n" +
                "          <button type=\"button\" class=\"navbar-toggle\" data-toggle=\"collapse\" data-target=\".navbar-collapse\">\n" +
                "            <span class=\"sr-only\">Toggle navigation</span>\n" +
                "            <span class=\"icon-bar\"></span>\n" +
                "            <span class=\"icon-bar\"></span>\n" +
                "            <span class=\"icon-bar\"></span>\n" +
                "          </button>\n" +
                "          <a class=\"navbar-brand\" href=\"#about\">Wifi Application</a>\n" +
                "        </div>\n" +
                "        <div class=\"collapse navbar-collapse\">\n" +
                "          <ul class=\"nav navbar-nav\">\n" +
                "            <li><a class=\"menuLink\" href=\"#algo1Anchor\"><span class=\"glyphicon glyphicon-signal\"></span> Algo 1</a>\n" +
                "            </li>\n" +
                "            <li><a class=\"menuLink\" href=\"#algo2Anchor\"><span class=\"glyphicon glyphicon-map-marker\"></span> Algo 2</a>\n" +
                "            </li>\n" +
                "            <li><a class=\"menuLink\" href=\"#top\"><span class=\"glyphicon glyphicon-play-circle\"></span> Menu 3</a>\n" +
                "            </li>\n" +
                "          </ul>\n" +
                "          <ul class=\"nav navbar-nav navbar-right\">\n" +
                "            <li><a class=\"menuLink\" href=\"#settings\"><span class=\"glyphicon glyphicon-cog\"></span> Settings</a>\n" +
                "            </li>\n" +
                "            <li class=\"active\"><a class=\"menuLink\" href=\"#aboutModal\" data-toggle=\"modal\" data-target=\"#myModal\"><span class=\"glyphicon glyphicon-star-empty\"></span> About</a>\n" +
                "            </li>\n" +
                "          </ul>\n" +
                "        </div>\n" +
                "        <!--/.nav-collapse -->\n" +
                "      </div>\n" +
                "    </div>\n" +
                "\n" +
                "    <!----------------------------------------------------------------------->\n" +
                "<div id=\"about\"></div>\n" +
                "<br>\n" +
                "\n" +
                "<div class=\"container\">\n" +
                "<h1 class=\"page-header\">Wifi Application</h1>\n" +
                "<div class=\"alert alert-success\" role=\"alert\">\n" +
                "  <div class=\"row\">\n" +
                "    <div class=\"col-lg-12 col-md-12 col-sm-12\">\n" +
                "      <button type=\"button\" class=\"close\" data-dismiss=\"alert\" aria-label=\"Close\">\n" +
                "         <span aria-hidden=\"true\">&times;</span>\n" +
                "       </button>\n" +
                "      <h4 > Start off by adding some files. <strong>Don't forget</strong> to click Save 2 CSV.</h4>\n" +
                "\n" +
                "    </div>\n" +
                "\n" +
                "</div>\n" +
                "</div>\n" +
                "\n" +
                "    <div class=\"alert alert-info\" role=\"alert\">\n" +
                "      <div class=\"row\">\n" +
                "        <div class=\"col-lg-4 col-md-3 col-sm-5\">\n" +
                "              <br>\n" +
                "              <form target=\"/app.html\" method=\"post\" enctype=\"multipart/form-data\">\n" +
                "              <div class=\"input-group input-group-md\">\n" +
                "              <input type=\"file\" name=\"uploaded_file\" class=\"form-control\" accept=\".csv\">\n" +
                "              <span class=\"input-group-btn\">\n" +
                "                <button class=\"btn btn-default\">Upload csv</button>\n" +
                "              </span>\n" +
                "              </div>\n" +
                "              </form>\n" +
                "            <br>\n" +
                "        </div>\n" +
                "\n" +
                "      <div class=\"col-lg-4 col-md-3 col-sm-5\">\n" +
                "        <br>\n" +
                "        <form target=\"/app.html\" method=\"post\" enctype=\"multipart/form-data\">\n" +
                "        <div class=\"input-group input-group-md\">\n" +
                "        <input class=\"form-control\" type=\"file\" id=\"ctrl\" name=\"uploaded_folder\" webkitdirectory=\"\" directory=\"\" multiple=\"\">\n" +
                "        <span class=\"input-group-btn\">\n" +
                "          <button class=\"btn btn-default\">Upload folder</button>\n" +
                "        </span>\n" +
                "        </div>\n" +
                "        </form>\n" +
                "      </div>\n" +
                "\n" +
                "\n" +
                "      <div class=\"col-lg-2 col-md-2 col-sm-2\">\n" +
                "      <br>\n" +
                "      <form method=\"post\" enctype=\"multipart/form-data\">\n" +
                "              <button id=\"save2csvbutton\" class=\"btn btn-default  pull-right\" type=\"button\" name=\"button\">Save2 CSV</button>\n" +
                "\n" +
                "      </form>\n" +
                "      </div>\n" +
                "\n" +
                "    <div class=\"col-lg-2 col-md-2 col-sm-2\">\n" +
                "    <br>\n" +
                "      <form method=\"post\" enctype=\"multipart/form-data\">\n" +
                "              <button id=\"clearDataButton\" class=\"btn btn-default \" type=\"button\" name=\"button\">Clear Data</button>\n" +
                "\n" +
                "      </form>\n" +
                "      </div>\n" +
                "    </div>\n" +
                "    </div>\n" +
                "\n" +
                "\n" +
                "\n" +
                "<a id=\"algo1Anchor\"></a>\n" +
                "<div class=\"jumbotron\">\n" +
                "  <div class=\"row\">\n" +
                "    <div class=\"col-lg-4 col-md-3 col-sm-4\">\n" +
                "    <h4>Algo 1</h4>\n" +
                "      Calculates the weighted arithmic mean of the mac\n" +
                "      entered.\n" +
                "    </div>\n" +
                "\n" +
                "    <div class=\"col-lg-3 col-md-3 col-sm-6\">\n" +
                "    <h4>Column 2</h4>\n" +
                "    Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod\n" +
                "    </div>\n" +
                "\n" +
                "    <div class=\"col-lg-3 col-md-3 col-sm-6\">\n" +
                "      <canvas id=\"myChart\" width=\"200\" height=\"150\"></canvas>\n" +
                "\n" +
                "    </div>\n" +
                "\n" +
                "\n" +
                "</div>\n" +
                "  <div class=\"row\">\n" +
                "    <div class=\"col-lg-4 col-md-3 col-sm-4\">\n" +
                "          <br>\n" +
                "    <form method=\"post\" enctype=\"multipart/form-data\">\n" +
                "          <div class=\"input-group input-group-lg\">\n" +
                "          <input id=\"mac\" type=\"text\" class=\"form-control\" placeholder=\"MAC\" value=\"\">\n" +
                "          <span class=\"input-group-btn\">\n" +
                "            <button id=\"submit\" class=\"btn btn-default\" type=\"button\" name=\"button\">Enter</button>\n" +
                "          </span>\n" +
                "          </div><br>\n" +
                "    </form>\n" +
                "    </div>\n" +
                "\n" +
                "  <div class=\"col-lg-3 col-md-3 col-sm-6\">\n" +
                "        <br>\n" +
                "        <div class=\"input-group input-group-lg\">\n" +
                "        <input type=\"text\" class=\"form-control\" placeholder=\"Full Name\" value=\"\">\n" +
                "        <span class=\"input-group-btn\">\n" +
                "          <button class=\"btn btn-default\" type=\"button\" name=\"button\">Enter</button>\n" +
                "        </span>\n" +
                "        </div><br>\n" +
                "  </div>\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "</div>\n" +
                "\n" +
                "</div>\n" +
                "\n" +
                "\n" +
                "<div class=\"alert alert-success\" role=\"alert\">\n" +
                "  <div class=\"row\">\n" +
                "    <div class=\"col-lg-12 col-md-12 col-sm-12\">\n" +
                "      <h2 id='output'>\n" +
                "  \t </h2>\n" +
                "      <br>\n" +
                "     <h2 id='outputLat'>\n" +
                "    </h2>\n" +
                "\n" +
                "    </div>\n" +
                "\n" +
                "</div>\n" +
                "</div>\n" +
                "\n" +
                "<div class=\"alert alert-danger\" role=\"alert\">\n" +
                "  <div class=\"row\">\n" +
                "    <div class=\"col-lg-12 col-md-12 col-sm-12\">\n" +
                "      <button type=\"button\" class=\"close\" data-dismiss=\"alert\" aria-label=\"Close\">\n" +
                "         <span aria-hidden=\"true\">&times;</span>\n" +
                "       </button>\n" +
                "      <h2 id='outputAlt'>\n" +
                "  \t </h2>\n" +
                "\n" +
                "    </div>\n" +
                "\n" +
                "</div>\n" +
                "</div>\n" +
                "\n" +
                "<a id=\"algo2Anchor\"></a>\n" +
                "\n" +
                "<div class=\"jumbotron\">\n" +
                "  <div class=\"row\">\n" +
                "    <div class=\"col-lg-4 col-md-3 col-sm-4\">\n" +
                "    <h4>Algo 2</h4>\n" +
                "      Calculates the weighted arithmic mean of the mac\n" +
                "      entered.\n" +
                "    </div>\n" +
                "\n" +
                "</div>\n" +
                "  <div class=\"row\">\n" +
                "    <div class=\"col-lg-3 col-md-4 col-sm-4\">\n" +
                "          <br>\n" +
                "    <form method=\"post\" enctype=\"multipart/form-data\">\n" +
                "          <div class=\"input-group input-group-lg\">\n" +
                "          <input id=\"mac1Algo2\" type=\"text\" class=\"form-control\" placeholder=\"MAC 1\" value=\"\">\n" +
                "          <br>\n" +
                "          <hr>\n" +
                "          <input id=\"mac2Algo2\" type=\"text\" class=\"form-control\" placeholder=\"MAC 2\" value=\"\">\n" +
                "          <br>\n" +
                "          <hr>\n" +
                "          <input id=\"mac3Algo2\" type=\"text\" class=\"form-control\" placeholder=\"MAC 3\" value=\"\">\n" +
                "          <span class=\"input-group-btn\">\n" +
                "          </span>\n" +
                "          </div><br>\n" +
                "    </form>\n" +
                "    </div>\n" +
                "\n" +
                "  <div class=\"col-lg-2 col-md-3 col-sm-3\">\n" +
                "        <br>\n" +
                "        <div class=\"input-group input-group-lg\">\n" +
                "        <input id=\"sig1Algo2\" type=\"text\" class=\"form-control\" placeholder=\"Sig1\" value=\"\">\n" +
                "        <br>\n" +
                "        <hr>\n" +
                "        <input id=\"sig2Algo2\" type=\"text\" class=\"form-control\" placeholder=\"Sig2\" value=\"\">\n" +
                "        <br>\n" +
                "        <hr>\n" +
                "        <input id=\"sig3Algo2\" type=\"text\" class=\"form-control\" placeholder=\"Sig3\" value=\"\">\n" +
                "        <span class=\"input-group-btn\">\n" +
                "          <button id=\"submitAlgo2\" class=\"btn btn-default\" type=\"button\" name=\"button\">Enter</button>\n" +
                "        </span>\n" +
                "        </div><br>\n" +
                "  </div>\n" +
                "\n" +
                "  <div class=\"col-lg-5 col-md-3 col-sm-6\">\n" +
                "    <canvas id=\"myChart\" width=\"200\" height=\"200\"></canvas>\n" +
                "\n" +
                "  </div>\n" +
                "\n" +
                "\n" +
                "</div>\n" +
                "\n" +
                "</div>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <!-- Modal -->\n" +
                "  <div class=\"modal fade\" id=\"myModal\" role=\"dialog\">\n" +
                "    <div class=\"modal-dialog\">\n" +
                "\n" +
                "      <!-- Modal content-->\n" +
                "      <div class=\"modal-content\">\n" +
                "        <div class=\"modal-header\">\n" +
                "          <button type=\"button\" class=\"close\" data-dismiss=\"modal\">&times;</button>\n" +
                "          <h4 style=\"color:red;\"><span class=\"glyphicon glyphicon-user\"></span> Website Created By:</h4>\n" +
                "        </div>\n" +
                "        <div class=\"modal-body\">\n" +
                "          <h2>Nissan Goldberg</h2>\n" +
                "          <img src=\"https://media.licdn.com/mpr/mpr/shrinknp_200_200/AAEAAQAAAAAAAAmxAAAAJGY2M2VhZWJjLWVhZjUtNDllZi04YzE4LWJhYmVhNjE4ZmExYQ.jpg\" class=\"img-thumbnail\" alt=\"Cinque Terre\"  style=\"width:20%\">\n" +
                "          <br>\n" +
                "          <hr>\n" +
                "          <p>Currently a CS student in Ariel University</p>\n" +
                "        </div>\n" +
                "        <div class=\"modal-footer\">\n" +
                "          <button type=\"submit\" class=\"btn btn-default btn-default pull-left\" data-dismiss=\"modal\"><span class=\"glyphicon glyphicon-remove\"></span> Close</button>\n" +
                "          <p>Want to view my github <a href=\"https://github.com/Ariel-OOP/\">Github</a></p>\n" +
                "        </div>\n" +
                "      </div>\n" +
                "    </div>\n" +
                "  </div>\n" +
                "<!--end of modal--------------------------->\n" +
                "\n" +
                "    <script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js\" integrity=\"sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa\" crossorigin=\"anonymous\"></script>\n" +
                "\n" +
                "\n" +
                "\n" +
                "    <script>\n" +
                "        $(\"button#submit\").click(() => {\n" +
                "            var mac  = $(\"input#mac\").val();\n" +
                "            var name   = $(\"input#mac\").val();\n" +
                "            alert(mac)\n" +
                "            alert(name)\n" +
                "        $.ajax(\n" +
                "            {\n" +
                "                \"url\": encodeURI(\"/app/submitAlgo1?\" + mac + \",\" + name)\n" +
                "            }\n" +
                "        ).then(\n" +
                "            function(output) {\n" +
                "                var splitOutput = output.split(\",\");\n" +
                "                $(\"h2#output\").html(output)\n" +
                "                $(\"h2#outputLat\").html(splitOutput[1])\n" +
                "                $(\"h2#outputAlt\").html(splitOutput[2])\n" +
                "\n" +
                "                new Chart(document.getElementById(\"myChart\"),\n" +
                "                    {\"type\":\"bubble\",\"data\":{\"datasets\":[{\"label\":\"Wifi Points\",\n" +
                "                    \"data\":[{\"x\":31,\"y\":30,\"r\":5},{\"x\":parseFloat(splitOutput[0]),\"y\":parseFloat(splitOutput[1]),\"r\":10},{\"x\":30,\"y\":33,\"r\":5},{\"x\":33,\"y\":36,\"r\":5}],\n" +
                "                    \"backgroundColor\":\"rgb(255, 99, 132)\"}]}});\n" +
                "            }\n" +
                "        );\n" +
                "        return false\n" +
                "        })\n" +
                "\n" +
                "        $(\"button#save2csvbutton\").click(() => {\n" +
                "            alert(\"will save to csv\")\n" +
                "        $.ajax(\n" +
                "            {\n" +
                "                \"url\": encodeURI(\"/app/save2csv\")\n" +
                "            }\n" +
                "        ).then(\n" +
                "            function(output) {\n" +
                "                $(\"h2#output\").html(\"saved\")\n" +
                "            }\n" +
                "        );\n" +
                "        return false\n" +
                "        })\n" +
                "\n" +
                "<!-------------------------scroll animation--------------------------->\n" +
                "        $(document).on('click', 'a[href^=\"#\"]', function (event) {\n" +
                "            event.preventDefault();\n" +
                "\n" +
                "            $('html, body').animate({\n" +
                "                scrollTop: $($.attr(this, 'href')).offset().top\n" +
                "            }, 500);\n" +
                "        });\n" +
                "\n" +
                "    </script>\n" +
                "\n" +
                "    <script>\n" +
                "    </script>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  </body>\n" +
                "</html>\n";
    }
}



//        get("/hello/:name", (req,res)->{
//            userPath =  req.params(":name").split(",");
//            if(userPath!=null){
//                for(String str: userPath)
//                    System.out.println(str);
//            }
//
//            return "Hello, "+ req.params(":name");
//        });