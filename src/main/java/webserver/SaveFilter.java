package webserver;

import components.Attributes.WifiPointsTimePlace;
import components.Filters.DevicePredicate;
import components.Filters.PlacePredicate;
import components.Filters.TimePredicate;
import spark.Request;
import spark.Response;

import java.io.*;
import java.util.function.Predicate;

public class SaveFilter implements Serializable{


    public static String saveFilter(Request req, Response res) throws IOException, ClassNotFoundException {

        String userName = req.queryString();
//        System.out.println("Logic 1:"+req.cookie("Logic"));
//        System.out.println("filter 1:"+req.cookie("filter1"));
        String filter1 = req.cookie("filter1");
//        System.out.println("filter "+filter1);
        String[] inputsFilter1 = req.cookie("input1").split("&");
//        System.out.println(inputsFilter1[0]);
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
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("pred.ser")))) {
            oos.writeObject(finalPredicate);
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] bytes = baos.toByteArray();
        System.out.println("saved filter");

        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);


        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("pred.ser")))) {
            Predicate e2 = (Predicate)ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println(finalPredicate);

//        ObjectInputStream ois = new ObjectInputStream(bais);
//        Predicate e2 = (Predicate) ois.readObject();

        return "filtered";
    }
}

