package bleu;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by cheng on 2015/3/26.
 */
public class Utils {
    public static double min(double d1, double d2){
        if (d1<= d2){
            return d1;
        }
        else{
            return d2;
        }
    }

    public static String cleanText(String str){
        return str;
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
        BufferedWriter bw = new BufferedWriter( new OutputStreamWriter(new FileOutputStream("local/segs-useranswer.txt")));

        String line = new String();
        while ((line=br.readLine())!=null) {
            String segs[] = line.split(",");
            String studentID = segs[0];
            int topicID = Integer.parseInt(segs[2]);
            String answer = segs[3];
            for (int i = 2;i<=4;i++){
                for(String s:ngram(answer,i)){
                    if(!topics.get(topicID).contains(s)){
                        bw.write("SEGS@_@"+topicID+"@_@"+i+"@_@"+s+"@_@1\n");
                        bw.flush();}
                    else{
                        System.out.println("EXCLUDE " + s);
                    }
                }
            }
        }
        bw.close();
    }

    public static void clickedResultSegs() throws Exception{
        BufferedReader br  =new BufferedReader( new InputStreamReader(new FileInputStream("data/UserAnswer2GS/clickedContent.txt")));
        BufferedWriter bw = new BufferedWriter( new OutputStreamWriter(new FileOutputStream("local/segs-clickedresult.txt")));

        String line = new String();
        while ((line=br.readLine())!=null) {
            String segs[] = line.split("\t");
            int topicID = Integer.parseInt(segs[0]);
            String answer = segs[1];
            for (int i = 2;i<=4;i++){
                for(String s:ngram(answer,i)){
                    if(!topics.get(topicID).contains(s)){
                    bw.write("SEGS@_@"+topicID+"@_@"+i+"@_@"+s+"@_@1\n");
                    bw.flush();}
                    else{
                        System.out.println("EXCLUDE " + s);
                    }
                }
            }
        }
        bw.close();
    }

    public static void fixedResultSegs() throws Exception{
        BufferedReader br  =new BufferedReader( new InputStreamReader(new FileInputStream("data/UserAnswer2GS/fixedContent.txt")));
        BufferedWriter bw = new BufferedWriter( new OutputStreamWriter(new FileOutputStream("local/segs-fixedresult.txt")));

        String line = new String();
        while ((line=br.readLine())!=null) {
            String segs[] = line.split("\t");
            int topicID = Integer.parseInt(segs[0]);
            int dura = Integer.parseInt(segs[1]);
            String answer = segs[2];
            for (int i = 2;i<=4;i++){
                for(String s:ngram(answer,i)){
                    if(!topics.get(topicID).contains(s)){
                        bw.write("SEGS@_@"+topicID+"@_@"+i+"@_@"+s+"@_@"+dura+"\n");
                        bw.flush();}
                    else{
                        System.out.println("EXCLUDE "+s);
                    }
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
    public static void allResultText2Segs() throws Exception{
        BufferedReader br  =new BufferedReader( new InputStreamReader(new FileInputStream("data/allResultText.txt")));
        BufferedWriter bw = new BufferedWriter( new OutputStreamWriter(new FileOutputStream("local/allResultTextSegs.txt")));
        String line = new String();
        while ((line=br.readLine())!=null){
            for (int i  =2;i<=4;i++){
                for (String s:ngram(line,i)){
                    bw.write(s+"\n");
                    bw.flush();
                }
            }
        }
        bw.close();
        br.close();
    }
    public static void main(String args[])throws Exception{
        init();
//        allResultText2Segs();
        clickedResultSegs();
        userAnswerSegs();
        fixedResultSegs();
    }
}
