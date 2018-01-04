package components.Algorithms;

import components.Attributes.HashRouters;
import components.Attributes.WIFISample;
import components.Attributes.WIFIWeight;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nissan on 12/3/2017.
 */

public class WeightedArithmeticMean {

    private HashRouters<String,WIFISample> hashRouters;
    private final int k=3; // change this to make getWAMbyMac more or less accurate.

    /**
     *
     * @param hashRouters is the Hable Table of all the WIIFI points from all the files.
     */
    public WeightedArithmeticMean(HashRouters<String, WIFISample> hashRouters) {
        this.hashRouters = hashRouters;
    }

    /**
     *
     * @param mac the mac to search for from within th given file in the constructor.
     * @return returns a WIFIWeight object which contains the Weighted Arithmetic Mean of the given Hash Table in the constructor
     */
    public WIFIWeight getWAMbyMac(String mac) {
        //the last argument is the comparator
        List<WIFISample> kMacList = hashRouters.getKBest(mac,k, (x,y)->{
            int xi = (int)(Double.parseDouble(x.getWIFI_RSSI()));
            int yi = (int)(Double.parseDouble(y.getWIFI_RSSI()));
            return yi-xi;
        });

        if (kMacList==null)
            return null;

        List<WIFIWeight> wifiWeights = new ArrayList<WIFIWeight>();

        for(WIFISample ws: kMacList){
            double weightOfOne = Double.parseDouble(ws.getWIFI_RSSI());
            weightOfOne= 1/(weightOfOne*weightOfOne);
            wifiWeights.add(new WIFIWeight( ws.getWIFI_MAC()
                    ,calcWeight(ws.getWIFI_Lat(),weightOfOne )
                    ,calcWeight(ws.getWIFI_Lon(),weightOfOne )
                    ,calcWeight(ws.getWIFI_Alt(),weightOfOne)
                    ,(int)(Double.parseDouble(ws.getWIFI_RSSI()))
                    ,weightOfOne ));

        }

        return getWamByList(wifiWeights);
    }

    public WIFIWeight getWamByList(List<WIFIWeight> wifiWeights){
        //final sum
        WIFIWeight sum,wSum;

        sum = new WIFIWeight(wifiWeights.get(0).getWIFI_MAC() ,0.0,0.0,0.0,0,0.0);

        for(WIFIWeight ww: wifiWeights){
            sum.setWIFI_Lat(  ww.getWIFI_Lat()+sum.getWIFI_Lat()   );
            sum.setWIFI_Lon(  ww.getWIFI_Lon()+sum.getWIFI_Lon()   );
            sum.setWIFI_Alt(  ww.getWIFI_Alt()+sum.getWIFI_Alt()  );
            sum.setWIFI_Weight( ww.getWIFI_Weight()+sum.getWIFI_Weight() );
        }

        wSum = new WIFIWeight(sum.getWIFI_MAC(),sum.getWIFI_Lat()/sum.getWIFI_Weight()
                ,sum.getWIFI_Lon()/sum.getWIFI_Weight()
                ,sum.getWIFI_Alt()/sum.getWIFI_Weight(),0,0.0);

        return wSum;
    }
    private double calcWeight(String str,Double weight){
        return  Double.parseDouble(str)*weight ;
    }


}

