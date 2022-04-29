package mc.bape.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
public class CloudConfigUtils {
    public static void downloadConfig(String filePath,String link){
        URL url;
        int responsecode;
        HttpURLConnection urlConnection;
        BufferedReader reader;
        String line;
        try{
            url=new URL(link);
            urlConnection = (HttpURLConnection)url.openConnection();
            responsecode=urlConnection.getResponseCode();
            if(responsecode==200){
                reader=new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"GBK"));
                while((line=reader.readLine())!=null){
                    WriteStringToFile(filePath,line);
                }
            }
            else{
                Helper.sendMessage("Failed get config file. Check your net state, Http code: "+responsecode);
            }
        }
        catch(Exception e){
        }
    }

    public static void WriteStringToFile(String filePath, String strings) {
        try {
            File file = new File(filePath);
            PrintStream ps = new PrintStream(new FileOutputStream(file));
            ps.append(strings);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}