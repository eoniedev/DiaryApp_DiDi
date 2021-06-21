package kr.ac.pusan.cs.android.myapplication;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class SearchCalorie{
    private String food_name;
    SearchCalorie(String name){
        food_name = name;
    }

    public String search() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        // TODO Auto-generated method stub

        ApiThread thread = new ApiThread();
        thread.start();
        thread.join();
        String result = thread.returnResult();
        //System.out.println(sb.toString());
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        if(result==null) return "null";
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(result)));
        doc.getDocumentElement().normalize();

        Element root = doc.getDocumentElement();
        NodeList nodeTotal = root.getElementsByTagName("totalCount");
        Element elTotal = (Element) nodeTotal.item(0);
        Node v_txt_total = elTotal.getFirstChild();
        int totalCount = Integer.parseInt(v_txt_total.getNodeValue());
        String value = "not found";
        if(totalCount != 0) {
            NodeList node = root.getElementsByTagName("NUTR_CONT1");

            Element el = (Element) node.item(0);
            Node v_txt = el.getFirstChild();
            value = v_txt.getNodeValue();
            Log.v("search","search value: "+value);
            //System.out.println(value);
        }
        return value;
    }

    public class ApiThread extends Thread {
        final String[] result = new String[1];
        public void run() {
            try {
                // Open the connection
                StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1470000/FoodNtrIrdntInfoService/getFoodNtrItdntList"); /*URL*/
                urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "=oL2kvb2nwDmjPgOGJ432w1Oo3ptSkR0fXAowXoAsZDg6Chj06Nblfysz24brhJl9WyJLydUXmsnqlW8msBubJQ%3D%3D"); /*Service Key*/
                urlBuilder.append("&" + URLEncoder.encode("desc_kor","UTF-8") + "=" + URLEncoder.encode(food_name, "UTF-8")); /*식품이름*/
                URL url = new URL(urlBuilder.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-type", "application/json");
                //System.out.println("Response code: " + conn.getResponseCode());
                Log.v("search","response code: "+conn.getResponseCode());
                BufferedReader rd;
                if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                    rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }
                // doc = rd.
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                // Set the result
                result[0] = sb.toString();
                //return result;
            }
            catch (Exception e) {
                // Error calling the rest api
                Log.e("REST_API", "GET method failed: " + e.getMessage());
                e.printStackTrace();
            }
        }
        public String returnResult(){
            return result[0];
        }
    }
}