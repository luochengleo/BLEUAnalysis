package bleu;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by cheng on 2015/3/26.
 */
public class BLEU {
    public static double min(double d1, double d2) {
        if (d1 <= d2) {
            return d1;
        } else {
            return d2;
        }
    }

    public static double logBLEU(String candidate, String reference) {
        double sum = 0.0;
        double c = candidate.length();
        double r = reference.length();
        sum += min(1 - r / c, 0);
        sum += 0.33 * pn(candidate, reference, 1) + 0.33 * pn(candidate, reference, 2) + 0.2 * pn(candidate, reference, 3) + 0.33 * pn(candidate, reference, 4);
//        sum += 0.2 * pn(candidate,reference,1)+0.2 * pn(candidate,reference,2)+0.2 * pn(candidate,reference,3)+0.2 * pn(candidate,reference,4)+0.2 * pn(candidate,reference,5);

        return sum;

    }

    public static double pn(String candidate, String reference, int n) {
        double d = 0.0;

        ArrayList<String> candNgramList = ngram(candidate, n);
        ArrayList<String> refNgramList = ngram(reference, n);
        double sumNormal = 0.0;
        double sumClip = 0.0;
        for (String s : candNgramList) {
            sumNormal += count(candidate, s);
            sumClip += min(count(candidate, s), count(reference, s));
        }

        return sumClip / sumNormal;

    }

    public static int count(String src, String dist) {
        int count = 0;
        int start = 0;
        while (src.indexOf(dist, start) >= 0 && start < src.length()) {
            count += 1;
            start = src.indexOf(dist, start) + dist.length();
        }
        return count;
    }

    public static ArrayList<String> ngram(String s, int n) {
        ArrayList<String> ngramList = new ArrayList<String>();
        char[] charList = s.toCharArray();
        if (n > charList.length) {
            return ngramList;
        } else {
            for (int i = 0; i <= charList.length - n; i++) {
                String temp = new String();
                for (int j = 0; j < n; j++) {
                    temp += charList[i + j];
                }
                ngramList.add(temp);
            }
        }
        return ngramList;
    }


    public static HashMap<Integer, String> topics;

    public static void main(String args[]) throws Exception {


        File gsdir = new File("local/goldenstandard");
        File ocdir = new File("local/outcome");
        for (File g : gsdir.listFiles()) {
            for (File o : ocdir.listFiles()) {

                if (g.getName().contains("gs-") && o.getName().contains("oc")) {
                    String referenceFile = g.getAbsolutePath();

                    String bleuResultFile = "local/bleu/bleu/" + g.getName().replace(".txt", "") + "+" + o.getName().replace(".txt", "") + ".bleu";
                    HashMap<Integer, String> reference = new HashMap<Integer, String>();
                    BufferedReader refReader = new BufferedReader(new InputStreamReader(new FileInputStream(g.getAbsoluteFile())));
                    String line = new String();
                    while ((line = refReader.readLine()) != null) {
                        String segs[] = line.split("\t");
                        int k = Integer.valueOf(segs[0]);
                        reference.put(k, segs[1]);
                    }
                    refReader.close();

                    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(o.getAbsolutePath())));
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(bleuResultFile)));
                    line = new String();
                    while ((line = br.readLine()) != null) {
                        String segs[] = line.split("\t");
                        String studentID = segs[0];
                        int topicID = Integer.parseInt(segs[1]);
                        String answer = segs[2];
                        System.out.println(topicID);

                        if (reference.keySet().contains(topicID)) {
                            bw.write(studentID + ',' + topicID + ',' + logBLEU(answer, reference.get(topicID)) + '\n');
                        }

                        bw.flush();

                    }
                    bw.close();
                }
            }
        }
    }
}
