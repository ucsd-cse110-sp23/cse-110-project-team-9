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

    private String handleGET(HttpExchange httpExchange){
        String response = "Invalid GET request";
        URI uri = httpExchange.getRequestURI();
        if (uri.getPath().equals("/history")) {
            return "GET";
        }
        return response;
    }

    private String handlePOST(HttpExchange httpExchange){
        String response = "Invalid GET request";
        URI uri = httpExchange.getRequestURI();
        if (uri.getPath().equals("/ask")) {
            return "POST";
        }
        return response;
    }

    private String handleDelete(HttpExchange httpExchange){
        String response = "Invalid Delete request";
        URI uri = httpExchange.getRequestURI();
        if (uri.getPath().equals("delete-question")) {
            return "DELETE";
        }
        else if(uri.getPath().equals("clear-all")){
            return "CLEAR"; 
        }
        return response;
    }
}