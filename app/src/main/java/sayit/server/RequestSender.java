package sayit.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestSender {
    private static final String BASE_URL = "http://localhost:8100";

    public static String sendGetRequest(String endpoint) throws IOException {

        //set up method variables
        String url = BASE_URL + endpoint;
        HttpURLConnection connection = null;
        StringBuilder responseBuilder = new StringBuilder();

        //attempt to make connection
        try {
            URL requestUrl = new URL(url);
            connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int responseCode = connection.getResponseCode();

            //get response from input stream and make a string
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseBuilder.append(line).append("\n");
                    }
                }
            } else {
                return "Request Error";
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return responseBuilder.toString().trim();
    }

    public static String sendPostRequest(String endpoint, byte[] body) throws IOException {
        
        String audioFilePath = "/path/to/audio/file.wav";
        String url = BASE_URL + endpoint;
        StringBuilder responseBuilder = new StringBuilder();
        URL requestUrl = new URL(url);

        HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/octet-stream");

        try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            FileInputStream fileInputStream = new FileInputStream(audioFilePath)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();

            // Get the response
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line).append("\n");
                }
                }
            }else {
                return "Request error";
            }

    }  finally {
       connection.disconnect();
       
    }
        return responseBuilder.toString().trim();
    }

    public static String sendDeleteRequest(String endpoint) throws IOException {

        //set up method variables
        String url = BASE_URL + endpoint;
        HttpURLConnection connection = null;
        StringBuilder responseBuilder = new StringBuilder();

        //attempt to make connection
        try {
            URL requestUrl = new URL(url);
            connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setRequestMethod("DELETE");
            connection.connect();

            int responseCode = connection.getResponseCode();

            //get response from input stream and make a string
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseBuilder.append(line).append("\n");
                    }
                }
            } else {
                return "Request Error";
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return responseBuilder.toString().trim();
    }

}

