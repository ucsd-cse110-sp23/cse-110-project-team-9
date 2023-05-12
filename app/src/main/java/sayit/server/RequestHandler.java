package sayit.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import sayit.storage.TsvStore;

public class RequestHandler implements HttpHandler{
    private final TsvStore data;

    public RequestHandler(TsvStore data) {
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

    //handle request for history
    private String handleGet(HttpExchange httpExchange) throws IOException {
      String response = "Invalid GET request";
      URI uri = httpExchange.getRequestURI();
      String query = uri.getRawQuery();
      if (query.indexOf("/history") > -1) {
        return data.getEntries().toString();
      }
      return response;
    }

    //handle request for Delete
    private String handleDelete(HttpExchange httpExchange) throws IOException{
      String response = "Invalid Delete request";
      URI uri = httpExchange.getRequestURI();
      String query = uri.getRawQuery();

      //delete single question
      if (query.indexOf("/delete-question") > -1) {
        int ID;
        String value = query.substring(query.indexOf("=") + 1);
        try{//see if ID number can be found
           ID = Integer.valueOf(query.substring(query.indexOf("=") + 1));
        }catch(Exception e){
          return response;
        }
        if (data.delete(ID)) {//check for correct deletion
          response = "Deleted entry {}" + ID + "}";
        } else {
          response = "No entry found for " + ID;
        }
      }

      //seeing if clear all was queried
      if (query.indexOf("/clear-all") > -1){
          if(data.clearAll()){
            response = "All entries cleared";
          }
      }
      return response;
    }
    
}
