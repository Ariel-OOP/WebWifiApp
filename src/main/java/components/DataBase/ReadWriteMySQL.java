package components.DataBase; /**
 * This is a very simple example representing how to work with MySQL
 * using java JDBC interface;
 * The example mainly present how to read a table representing a set of WiFi_Scans
 * Note: for simplicity only two properties are stored (in the DB) for each AP:
 * the MAC address (mac) and the signal strength (rssi), the other properties (ssid and channel)
 * are omitted as the algorithms do not use the additional data.
 *
 * @see https://github.com/benmoshe/OOP_Exe/edit/master/src/db/MySQL_101.java
 */

import components.Attributes.HashRouters;
import components.Attributes.WIFISample;
import components.Attributes.WifiPointsTimePlace;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReadWriteMySQL {

    private String _tableName = "oop_course_ariel";
    private String _ip = "5.29.193.52";
    private String _port = "3306";
    private String _url = "jdbc:mysql://"+_ip+":"+_port+"/" + _tableName;
    private String _user = "oop1";
    private String _DataBase = "";
    private String _password = "Lambda1();";
    private Connection _con = null;

    public ReadWriteMySQL(String _tableName, String _ip, String _port, String _url, String _user, String _DataBase, String _password) {
        this._tableName = _tableName;
        this._ip = _ip;
        this._port = _port;
        this._url = _url;
        this._user = _user;
        this._DataBase = _DataBase;
        this._password = _password;
    }

    public ReadWriteMySQL() {
    }

//    public static int test_101() {
//        Statement st = null;
//        ResultSet rs = null;
//        int max_id = -1;
//        //String ip = "localhost";
//        // String ip = "192.168.1.18";
//
//        try {
//            _con = DriverManager.getConnection(_url, _user, _password);
//            st = _con.createStatement();
//            rs = st.executeQuery("SELECT UPDATE_TIME FROM ");
//            if (rs.next()) {
//                System.out.println(rs.getString(1));
//            }
//
//            PreparedStatement pst = _con.prepareStatement("SELECT * FROM test101");
//            rs = pst.executeQuery();
//
//            while (rs.next()) {
//                int id = rs.getInt(1);
//                if(id>max_id) {max_id=id;}
//                System.out.print(id);
//                System.out.print(": ");
//                System.out.print(rs.getString(2));
//                System.out.print(" (");
//                double lat = rs.getDouble(3);
//                System.out.print(lat);
//                System.out.print(", ");
//                double lon = rs.getDouble(4);
//                System.out.print(lon);
//                System.out.println(") ");
//            }
//        } catch (SQLException ex) {
//            Logger lgr = Logger.getLogger(ReadWriteMySQL.class.getName());
//            lgr.log(Level.SEVERE, ex.getMessage(), ex);
//        } finally {
//            try {
//                if (rs != null) {rs.close();}
//                if (st != null) { st.close(); }
//                if (_con != null) { _con.close();  }
//            } catch (SQLException ex) {
//
//                Logger lgr = Logger.getLogger(ReadWriteMySQL.class.getName());
//                lgr.log(Level.WARNING, ex.getMessage(), ex);
//            }
//        }
//        return max_id;
//    }

    public List<WifiPointsTimePlace> readSQL(HashRouters<String,WIFISample> routersOfAllFiles) {
        List<WifiPointsTimePlace> processedFile = new ArrayList<>();
        WifiPointsTimePlace wifiPointsTimePlace;
        List<WIFISample> wifiPoints = new ArrayList<>();
        WIFISample wifiSample;


        String wifi_MAC;
        String wifi_SSID;
        String wifi_FirstSeen;
        String wifi_Channel;
        String wifi_Frequency;
        String wifi_RSSI;
        String wifi_Lat;
        String wifi_Lon;
        String wifi_Alt;
        String wifi_Type;
        String wifi_Device;
        int numOfWifis;

        Statement st = null;
        ResultSet rs = null;
        int max_id = -1;

        try {
            _con = DriverManager.getConnection(_url, _user, _password);
            st = _con.createStatement();
            rs = st.executeQuery("SELECT UPDATE_TIME FROM information_schema.tables WHERE TABLE_SCHEMA = 'oop_course_ariel' AND TABLE_NAME = 'ex4_db'");
            if (rs.next()) {
                System.out.println("**** Update: "+rs.getString(1));
            }

            PreparedStatement pst = _con.prepareStatement("SELECT * FROM ex4_db");
            rs = pst.executeQuery();
            int ind=0;
            while (rs.next()) {
                int size = rs.getInt(7);
                int len = 7+2*size;
                if(ind%100==0) {
                    System.out.println();
                    for(int i=1;i<=7;i++){
                        System.out.print(rs.getString(i)+",");
                    }
                    wifi_FirstSeen = rs.getString(2);
                    wifi_Device = rs.getString(3);
                    wifi_Lat= rs.getString(4);
                    wifi_Lon= rs.getString(5);
                    wifi_Alt= rs.getString(6);
                    numOfWifis = Integer.parseInt(rs.getString(7));
                    wifiPointsTimePlace = new WifiPointsTimePlace();

                    for(int i=8;i<=len;i++){
                        if(i % 2 == 0)
                            System.out.print(" SSID, " + rs.getString(i)+",");
                        else if(i % 2 == 1 )
                            System.out.print(" FREQ, " + rs.getString(i)+",");
                    }
                    //
                    for (int i =8 ; i<2*numOfWifis+8;i=i+2){
                        wifiSample = new WIFISample(rs.getString(i),"",wifi_FirstSeen,"14",rs.getString(i+1),
                                wifi_Lat,wifi_Lon,wifi_Alt,"wifi",wifi_Device);
                        wifiPointsTimePlace.addPoint(wifiSample);

                        routersOfAllFiles.addElement(rs.getString(i),wifiSample);
                    }

                    processedFile.add(wifiPointsTimePlace);
                    System.out.println();
                }
                ind++;
            }
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(ReadWriteMySQL.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
            try {
                if (rs != null) {rs.close();}
                if (st != null) { st.close(); }
                if (_con != null) { _con.close();  }
            } catch (SQLException ex) {

                Logger lgr = Logger.getLogger(ReadWriteMySQL.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
        return processedFile;
    }
}