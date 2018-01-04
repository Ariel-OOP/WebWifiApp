package components.Algorithms;

import components.Attributes.WIFIWeight;
import components.Attributes.WifiPointsTimePlace;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Moshe
 */
public class Algorithm2 {

    final static double sig_diff = 0.4;
    final static double power = 2;
    final static double norm = 10000;
    final static int diff_no_sig = 100;

    public static ArrayList<WIFIWeight> getKMostSimilar(List<WifiPointsTimePlace> FinalCSV, ArrayList<WIFIWeight> inputFromUser, int k)
    {
        WIFIWeight kSimilar[] = new WIFIWeight[k];
        WIFIWeight wifiWeightOfOneLine;
        ArrayList<WIFIWeight> kMostSimilar = new ArrayList<>();

        for(int i = 0; i < kSimilar.length; i++)
            kSimilar[i] = new WIFIWeight("",0,0,0,0,0);

        //kSimilar = Arrays.stream(kSimilar).map((x) -> x = new WIFIWeight("",0,0,0,0,0));

        int p = 0;
        for (WifiPointsTimePlace line : FinalCSV)
        {
            p++;
            wifiWeightOfOneLine = line.checkSimilarity(inputFromUser);

            //Try to insert the new wifiWeightOfOneLine into the array of k most similar. //enterToArrayOfKSimilar(kSimilar,wifiWeightOfOneLine);
            WIFIWeight cotterntWifiWight = wifiWeightOfOneLine;
            WIFIWeight temp;
            int numberOfLines = kSimilar.length;
            for(int i = 0; i < kSimilar.length; i++)
            {
                if(kSimilar[i].getWIFI_Weight() < cotterntWifiWight.getWIFI_Weight())
                {
                    temp = kSimilar[i];
                    kSimilar[i] = cotterntWifiWight;
                    cotterntWifiWight = temp;
                }
            }
        }

        for (int i = 0; i < kSimilar.length; i++)
            kMostSimilar.add(kSimilar[i]);

        return kMostSimilar;
    }

//    private static void enterToArrayOfKSimilar(WIFIWeight[] kSimilar, WIFIWeight wifiWeightOfOneLine) {
//        WIFIWeight cotterntWifiWight = wifiWeightOfOneLine;
//        WIFIWeight temp;
//
//
//        for(int i = 0; i < kSimilar.length; i++)
//        {
//            if(kSimilar[i].getWIFI_Weight() < cotterntWifiWight.getWIFI_Weight())
//            {
//                temp = kSimilar[i];
//                kSimilar[i] = cotterntWifiWight;
//                cotterntWifiWight = temp;
//            }
//        }
//    }


    public static double calcWeight(ArrayList<Integer> signalsLine, ArrayList<Integer> signalsUser)
    {
        double weight = 1;

        int singleDiff = 0;

        ArrayList<Integer> diffs = new ArrayList<Integer>();
        ArrayList<Double> weights = new ArrayList<Double>();

        for(int i = 0; i < signalsUser.size(); i++) {
            singleDiff = (signalsLine.get(i) == -120)? diff_no_sig : Math.abs(Math.abs(signalsLine.get(i)) - Math.abs(signalsUser.get(i)));
            diffs.add(singleDiff);
        }

        for(int i = 0; i < diffs.size(); i++) {
            int difference = (diffs.get(i) > 3)? diffs.get(i) : 3;
            weights.add(norm/(Math.pow(difference,sig_diff)*Math.pow(signalsUser.get(i),power)));
        }

        for(int i = 0; i < weights.size(); i++) {
            weight *= weights.get(i);
        }

        return weight;
    }
}
