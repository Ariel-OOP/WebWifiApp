package components;//package components;
//
//import java.util.List;
//import java.util.function.Predicate;
//import java.util.stream.Collectors;
//
///**
// * Created by Nissan on 11/19/2017.
// */
//public class PredicateTester {
//
//    public  Predicate<WIFISample> filterDistance(){
//        return p-> distFrom(32.10588208,35.20343111,Double.parseDouble(p.getWIFI_Lat()),Double.parseDouble(p.getWIFI_Lon()),200);
//    }
//
//    public List<WIFISample> filterWifi (List<WIFISample> employees, Predicate<WIFISample> predicate) {
//        return employees.stream().filter( predicate ).collect(Collectors.<WIFISample>toList());
//    }
//
//
//    private static boolean distFrom(double lat1, double lng1, double lat2, double lng2, double radius) {
//        //
//
//        double earthRadius = 6371000; //meters
//        double dLat = Math.toRadians(lat2-lat1);
//        double dLng = Math.toRadians(lng2-lng1);
//        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
//                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
//                        Math.sin(dLng/2) * Math.sin(dLng/2);
//        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
//        double dist = (double) (earthRadius * c);
//
//        return dist <= radius;
//    }
//}
