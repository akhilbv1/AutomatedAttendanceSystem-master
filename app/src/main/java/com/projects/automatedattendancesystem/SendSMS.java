package com.projects.automatedattendancesystem;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SendSMS {
    public static String sendSms() {
        try {
            // Construct data
            String apiKey = "apikey=" + "1vhKRku3pDk-GQnlto8GpUdT7bd5Uw6YLShZ05RqNj";
            String message = "&message=" + "Testing";
            String sender = "&sender=" + "TXTLCL";
            String numbers = "&numbers=" + "918341770556";

            // Send data
            HttpURLConnection conn = (HttpURLConnection) new URL("https://api.textlocal.in/send/?").openConnection();
            Log.i("url","url:- "+conn.getURL());
            String data = apiKey + numbers + message + sender;
            Log.i("url","url:- "+data);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
            conn.getOutputStream().write(data.getBytes("UTF-8"));
            Log.i("url","url:- "+conn.getRequestMethod());
            Log.i("url","url:- "+conn.getResponseMessage());
            Log.i("url","url:- "+conn.getContent());
            final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            final StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = rd.readLine()) != null) {
                stringBuffer.append(line);
            }
            rd.close();

            return stringBuffer.toString();
        } catch (Exception e) {
            System.out.println("Error SMS " + e);
            return "Error " + e;
        }
    }

}
