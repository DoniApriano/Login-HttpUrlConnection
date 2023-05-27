package com.doniapriano.bismillahloginlagihttp;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Http {
    Context context;
    private String url, method = "GET", data = null, response = null;
    private Integer statusCode = 0;
    private Boolean token = false;
    private LocalStorage localStorage;

    public Http(Context context, String url) {
        this.context = context;
        this.url = url;
        localStorage = new LocalStorage(context);
    }

    public void setMethod(String method) {
        this.method = method.toUpperCase();
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setToken(Boolean token) {
        this.token = token;
    }

    public String getResponse() {
        return response;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void send() {
        try {
            URL sUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) sUrl.openConnection();
            conn.setRequestMethod(method);
            conn.setRequestProperty("Content-Type","application/json");
            conn.setRequestProperty("X-Requested-With","XMLHttpRequest");
            if (token) {
                conn.setRequestProperty("Authorization","Bearer " + localStorage.getToken());
            }

            if (!method.equals("GET"))  {
                conn.setDoOutput(token);
            }

            if (data != null) {
                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(data.getBytes());
                outputStream.flush();
                outputStream.close();
            }

            statusCode = conn.getResponseCode();
            InputStreamReader inputStreamReader;
            if (statusCode >= 200 && statusCode <= 299) {
                inputStreamReader = new InputStreamReader(conn.getInputStream());
            } else {
                inputStreamReader = new InputStreamReader(conn.getErrorStream());
            }

            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer buffer = new StringBuffer();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                buffer.append(line);
            }
            bufferedReader.close();
            response = buffer.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
