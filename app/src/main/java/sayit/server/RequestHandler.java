package sayit.server;

import java.io.*;
import java.net.URI;
import java.util.Base64;
import java.util.HashMap;
import java.util.Scanner;

import sayit.Constants;
import sayit.openai.*;
import sayit.qa.Answer;
import sayit.qa.Question;
import sayit.qa.QuestionAnswerEntry;

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
      if (uri.getPath().equals("/history")) {
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
      if (uri.getPath().equals("/history")) {
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

    private String handlePost(HttpExchange httpExchange) throws IOException {
      
      String response = "Invalid Post Request";
      
      URI uri = httpExchange.getRequestURI();
      String query = uri.getRawQuery();
      
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
      byte[] audioBytes = outputStream.toByteArray();
      outputStream.close();

      // Save the audio bytes as a sound file
      String soundFilePath = saveAudioFile(audioBytes);

      IWhisper whisper = new Whisper(Constants.OPENAI_API_KEY);
      WhisperCheck whisperCheck = new WhisperCheck(whisper, new File(soundFilePath));

      String question = whisperCheck.output();

      if (whisperCheck.isExceptionThrown()) {
        // Show a message box with an error containing the exception content
        response = "Unable to transcribe response";
        return response;
      }

      ChatGpt chatGpt = new ChatGpt(Constants.OPENAI_API_KEY, 100);
      String answer;

      try{
        answer = chatGpt.askQuestion(question);
      }catch(Exception e){
        response = "Chat GPT Error";
        return response;
      }

      // Create a new question/answer pair and insert it into the database
      Question q = new Question(question);
      Answer a = new Answer(answer);
      QuestionAnswerEntry entry = new QuestionAnswerEntry(q, a);

      int newID = data.insert(entry);

      response = "New Entry Added: " + newID;

      data.save();

      return response;
  }
  
  private String extractBase64AudioData(String requestBody) {
    // Extract the Base64-encoded audio data from the request body (example assumes JSON format)
    // Implement your own logic here based on your specific request format
    // Example: {"audioData": "<base64-encoded-audio-data>"}
    // Use a JSON parser to extract the value of the "audioData" field
    // For simplicity, this example assumes a simple format without error handling

    int startIndex = requestBody.indexOf("\"audioData\": \"") + 14;
    int endIndex = requestBody.lastIndexOf("\"");
    return requestBody.substring(startIndex, endIndex);
  }

  //save bytes into audiofile
  private String saveAudioFile(byte[] audioBytes) throws IOException {
    String soundFilePath = "question.wav"; // Provide the desired file path
    try (OutputStream outStream = new FileOutputStream(soundFilePath)) {
        outStream.write(audioBytes);
    }
    return soundFilePath;
}
    
}
