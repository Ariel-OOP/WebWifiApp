package webserver;

import java.io.*;

//source https://rosettacode.org/wiki/CSV_to_HTML_translation#Java_2
public class Csv2Html {

    public static String escapeChars(String lineIn) {
        StringBuilder sb = new StringBuilder();
        int lineLength = lineIn.length();
        for (int i = 0; i < lineLength; i++) {
            char c = lineIn.charAt(i);
            switch (c) {
                case '"':
                    sb.append("&quot;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                case '\'':
                    sb.append("&apos;");
                    break;
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                default: sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String tableHeader(String ps, String[] columns) {
        ps+="<tr class=\"success\">";
        for (int i = 0; i < columns.length; i++) {
            ps+="<th>";
            ps+=columns[i];
            ps+="</th>";
        }
        ps+="</tr>";
        return ps;
    }

    public static String tableRow(String ps, String[] columns) {
        ps+="<tr class=\"danger\">";
        for (int i = 0; i < columns.length; i++) {
            ps+="<td>";
            ps+=columns[i];
            ps+="</td>";
        }
        ps+="</tr>";
        return ps;
    }

    public static String getCSVTable(String path) throws Exception {

        File file = new File(path);
        InputStreamReader isr = new InputStreamReader(new FileInputStream(file));
        BufferedReader br = new BufferedReader(isr);
        String stdout=null;


        stdout+="<table class=\"table table-condensed\">";
        String stdinLine;
        boolean firstLine = true;
        int limit=0;
        while (((stdinLine = br.readLine()) != null) && limit<10) {
            String[] columns = escapeChars(stdinLine).split(",");
            if (firstLine == true) {
                stdout+= tableHeader(stdout, columns);
                firstLine = false;
            } else {
                stdout+= tableRow(stdout, columns);
            }
            limit++;
        }
        stdout+="</table>";
        System.out.println("uploaded table");
        return stdout;

    }
}