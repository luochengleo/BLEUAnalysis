package bleu;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by cheng on 2015/3/26.
 */
public class BLEU {
    public static double min(double d1, double d2){
        if (d1<= d2){
            return d1;
        }
        else{
            return d2;
        }
    }

    public static double logBLEU(String candidate, String reference){
        double sum = 0.0;
        double c = candidate.length();
        double r = reference.length();
        sum += min(1-r/c,0);
        sum += 0.33 * pn(candidate,reference,1)+0.33 * pn(candidate,reference,2)+0.2 * pn(candidate,reference,3)+0.33 * pn(candidate,reference,4);
//        sum += 0.2 * pn(candidate,reference,1)+0.2 * pn(candidate,reference,2)+0.2 * pn(candidate,reference,3)+0.2 * pn(candidate,reference,4)+0.2 * pn(candidate,reference,5);

        return sum;

    }
    public static double pn(String candidate, String reference, int n){
        double d = 0.0;

        ArrayList<String> candNgramList = ngram(candidate,n);
        ArrayList<String> refNgramList = ngram(reference,n);
        double sumNormal =0.0;
        double sumClip = 0.0;
        for(String s:candNgramList){
            sumNormal += count(candidate,s);
            sumClip += min(count(candidate, s), count(reference, s));
        }

        return sumClip/sumNormal;

    }
    public static int count(String src,String dist){
        int count = 0;
        int start = 0;
        while(src.indexOf(dist,start) >=0 && start <src.length()){
            count +=1;
            start = src.indexOf(dist,start)+dist.length();
        }
        return count;
    }
    public static ArrayList<String> ngram(String s, int n){
        ArrayList<String> ngramList = new ArrayList<String>();
        char[] charList = s.toCharArray();
        if (n>charList.length){
            return ngramList;
        }
        else{
            for (int i = 0;i <=charList.length-n;i++){
                String temp = new String();
                for (int j = 0;j<n;j++){
                    temp += charList[i+j];
                }
                ngramList.add(temp);
            }
        }
        return ngramList;
    }


    public static void userAnswerSegs() throws Exception{
        BufferedReader br  =new BufferedReader( new InputStreamReader(new FileInputStream("data/recordcontent.csv")));
        BufferedWriter bw = new BufferedWriter( new OutputStreamWriter(new FileOutputStream("data/UserAnswer2GS/segs-useranswer.txt")));

        String line = new String();
        while ((line=br.readLine())!=null) {
            String segs[] = line.split(",");
            String studentID = segs[0];
            int topicID = Integer.parseInt(segs[2]);
            String answer = segs[3];
            for (int i = 2;i<=4;i++){
                for(String s:ngram(answer,i)){
                    if(!topics.get(topicID).contains(s)){
                        bw.write("SEGS::"+topicID+"::"+i+"::"+s+"\n");
                        bw.flush();}
                }
            }
        }
        bw.close();
    }

    public static void clickedResultSegs() throws Exception{
        BufferedReader br  =new BufferedReader( new InputStreamReader(new FileInputStream("data/UserAnswer2GS/clickedContent.txt")));
        BufferedWriter bw = new BufferedWriter( new OutputStreamWriter(new FileOutputStream("data/UserAnswer2GS/segs-clickedresult.txt")));

        String line = new String();
        while ((line=br.readLine())!=null) {
            String segs[] = line.split("\t");
            int topicID = Integer.parseInt(segs[0]);
            String answer = segs[1];
            for (int i = 2;i<=4;i++){
                for(String s:ngram(answer,i)){
                    if(!topics.get(topicID).contains(s)){
                    bw.write("SEGS::"+topicID+"::"+i+"::"+s+"\n");
                    bw.flush();}
                }
            }
        }
        bw.close();
    }
    public static HashMap<Integer,String> topics;
    public static void init()throws Exception{
        topics = new HashMap<Integer,String>();
        BufferedReader topicReader = new BufferedReader( new InputStreamReader(new FileInputStream("data/topics.csv")));
        String line = new String();
        while ((line = topicReader.readLine()) != null){
            String segs[] = line.split(",");
            int tid = Integer.valueOf(segs[0]);
            String content = "";
            for(int i = 1;i<= segs.length-1;i++){
                content += segs[i];
            }
            topics.put(tid,content);
        }

    }
    public static void main(String args[])throws Exception{
        init();
        clickedResultSegs();
        userAnswerSegs();

        HashMap<Integer, String> reference = new HashMap<Integer, String>();
        BufferedReader refReader = new BufferedReader( new InputStreamReader(new FileInputStream("data/UserAnswer2GS/segs-useranswer.txt.out")));
        String line = new String();
        while((line=refReader.readLine()) != null){
            String segs[] = line.split("\t");
            int k = Integer.valueOf(segs[0]);

            reference.put(k,segs[1]);
        }

        BufferedReader br  =new BufferedReader( new InputStreamReader(new FileInputStream("data/recordcontent.csv")));
        BufferedWriter bw = new BufferedWriter( new OutputStreamWriter(new FileOutputStream("data/bleuresult-with-useranwser.csv")));
        line = new String();
        while ((line=br.readLine())!=null){
            String segs[] = line.split(",");
            String studentID=segs[0];
            int topicID = Integer.parseInt(segs[2]);
            String answer = segs[3];
            System.out.println(topicID);
            bw.write(studentID+','+topicID+','+logBLEU(answer,reference.get(topicID))+'\n');
            bw.flush();

        }
        bw.close();
    }
}
