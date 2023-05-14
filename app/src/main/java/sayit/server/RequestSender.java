package sayit.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * <p>
 * A class that contains static methods to make requests to our HTTP server to access the busines logic of our project
 * </p>
 * <p>
 * Send request for GET, which should return the question history. 
 * Send Request for POST/OR Put which takes sends an audio file to the server for whisper and chat GPT. 
 * Send DELETE request to either delete a single entry or clear all entriews
 * </p>
 * <p>
 * The following assumptions are made for this class:
 *     <ul>
 *         <li> Calls from UI will contain endpoint and Audio File for POST or PUT exists.</li>
 *     </ui>
 * </p>
 */

public class RequestSender {

    /*
     * URL for the server
     */
    private static final String BASE_URL = "http://localhost:8100";


    /*
     * Send get request
     * @ param endpoint: command we have set up for the server.
     * @ return will return the response from the server that can be picked up from the UI if successful and handled otherwise
     * @ throws IOException if connection fails
     */
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
        }   finally {
                if (connection != null) {
                    connection.disconnect();
                
                }
            }   
        //return server response
        return responseBuilder.toString().trim();
    }

    /*
     * Method to Send Post request
     * @param endpoint, string for the command sent to the server
     * @param audioFile, file passed in from the audio recoder
     * @ throws IOException if connection fails
     * @return returns a String containing the server response
     */
    public static String sendPostRequest(String endpoint, File audioFile) throws IOException {
        
        //method variables 
        String url = BASE_URL + endpoint;
        StringBuilder responseBuilder = new StringBuilder();
        URL requestUrl = new URL(url);

        //make connection
        HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/octet-stream");

        //convert audio file into bytes
        try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());

            FileInputStream fileInputStream = new FileInputStream(audioFile)) {
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
            } else {
                return "Request error";
            }

        } finally {
            connection.disconnect();
       
        }
        //return server response
        return responseBuilder.toString().trim();
    }

    /*
     * Method to Send Delete Request
     * @param endpoint, a string containing the command for the server, either for Delete or Clear
     * @param toDel, should be the ID number of a question that needs to be deleted from server storage for the /delete-question endpoint
     * toDel can be anything
     * @ throws IOException if connection fails
     * @return a string with the server response
     */
    public static String sendDeleteRequest(String endpoint, String toDel) throws IOException {

        //set up method variables
        String url = BASE_URL + endpoint + "?id=" + toDel;
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
        //return server response
        return responseBuilder.toString().trim();
    }

}

