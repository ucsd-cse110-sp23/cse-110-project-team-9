package sayit.server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import sayit.Constants;
import sayit.openai.ChatGpt;
import sayit.openai.IWhisper;
import sayit.openai.Whisper;
import sayit.openai.WhisperCheck;
import sayit.qa.Answer;
import sayit.qa.Question;
import sayit.qa.QuestionAnswerEntry;
import sayit.storage.TsvStore;

public class mockHandler implements HttpHandler{

    private final TsvStore data;

    public mockHandler(TsvStore data) {
        this.data = data;
    }

    public void handle(HttpExchange httpExchange) throws IOException {
        String response = "Request Received";
        String method = httpExchange.getRequestMethod();
        try {
            if (method.equals("GET")) {
              response = handleGet(httpExchange);
            } else if (method.equals("POST")) {
              response = handlePost(httpExchange);
            } 
              else if (method.equals("PUT")){
              response = handlePost(httpExchange);
            }
              else if(method.equals("DELETE")){
              response = handleDelete(httpExchange);
            }
              else {
              throw new Exception("Not Valid Request Method");
            }
        }catch (Exception e) {
            System.out.println("An erroneous request");
            response = e.toString();
            e.printStackTrace();
        } 

        //Sending back response to the client
        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream outStream = httpExchange.getResponseBody();
        outStream.write(response.getBytes());
        outStream.close();
    }

    private String handleGet(HttpExchange httpExchange){
        String response = "Invalid GET request";
        URI uri = httpExchange.getRequestURI();
        if (uri.getPath().equals("/history")) {
            return "GET";
        }
        return response;
    }

    private String handlePost(HttpExchange httpExchange) throws IOException{
      String response = "Invalid POST request";
      
      URI uri = httpExchange.getRequestURI();
      
      //check endpoint
      if (!uri.getPath().equals("/ask")){
        return response;
      }

      // Read the audio data from the request body
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      InputStream inputStream = httpExchange.getRequestBody();
      byte[] buffer = new byte[8192];
      int bytesRead;

      while ((bytesRead = inputStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, bytesRead);
      }

      //get bytes from request
      byte[] audioBytes = outputStream.toByteArray();
      outputStream.close();

      return "POST";
    }

    private String handleDelete(HttpExchange httpExchange){
        String response = "Invalid DELETE request";
        URI uri = httpExchange.getRequestURI();
        String query = uri.getRawQuery();
        int ID;
        String value = query.substring(query.indexOf("=") + 1);
        try{//see if ID number can be found
          ID = Integer.valueOf(value);
        }catch(Exception e){
          return response;
        }
        if (uri.getPath().equals("/delete-question")) {
            return "DELETE{" + value + "}";
        }
        else if(uri.getPath().equals("/clear-all")){
            return "CLEAR"; 
        }
        return response;
    }
}