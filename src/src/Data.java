package src;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.*;


public class Data {

    String url = "https://www.alphavantage.co/";
    String charset = java.nio.charset.StandardCharsets.UTF_8.name();  // Or in Java 7 and later, use the constant: java.nio.charset.StandardCharsets.UTF_8.name()
    String function = "TIME_SERIES_DAILY";
    String symbol = "FB";
    String apikey = "PHVQ81JSW6C2OUUS";

    String query;


    private JsonObject object;
    private TreeMap<String, Double> dailyValue = new TreeMap<>();


    //https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=FB&apikey=PHVQ81JSW6C2OUUS
    public void initializeQuery() {
        try {
            query = String.format("function=%s&symbol=%s&apikey=%s",
                    URLEncoder.encode(function, charset),
                    URLEncoder.encode(symbol, charset),
                    URLEncoder.encode(apikey, charset));
        } catch(UnsupportedEncodingException e) {

        }

    }

    public void getData() {
        InputStream response;
        try {
            URLConnection connection = new URL(url + "query?" + query).openConnection();
            connection.setRequestProperty("Accept-Charset", charset);
            response = connection.getInputStream();

            //int status = httpConnection.getResponseCode();
            connection.connect();

            /*for (Map.Entry<String, List<String>> header : connection.getHeaderFields().entrySet()) {
                System.out.println(header.getKey() + "=" + header.getValue());
            }*/

            String contentType = connection.getHeaderField("Content-Type");
            if(contentType.contains("json")){

                StringBuilder stringBuilder = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(response, charset))) {
                    for (String line; (line = reader.readLine()) != null;) {
                        stringBuilder.append(line);
                    }
                }
                String jsonString = stringBuilder.toString();
                JsonParser parser = new JsonParser();
                object = parser.parse(jsonString).getAsJsonObject();

                //System.out.println(element);


            }
        } catch (Exception e) {

        }

        //System.out.println(response.toString());
    }


    public void parseData() {
        JsonObject prices = object.getAsJsonObject("Time Series (Daily)");

        Set<Map.Entry<String, JsonElement>> entries = prices.entrySet();//will return members of your object
        for (Map.Entry<String, JsonElement> entry: entries) {
            String day = entry.getKey();

            JsonObject values = entry.getValue().getAsJsonObject();
            String value = values.get("4. close").toString();
            value = value.replace("\"", "");
            dailyValue.put(day, Double.valueOf(value));
        }

        System.out.println(dailyValue);

    }


    public static void main(String[] args){
        Data d = new Data();

        d.initializeQuery();
        d.getData();
        d.parseData();

    }
}