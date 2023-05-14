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


/**
 * <p>
 * Request Handler for the web server
 * </p>
 * <p>
 * We need the server to handle a few different request types
 * Get requests to get the entry history from the server
 * Delete requests to either delete a single entry or clear all entries
 * Post or Put requests to send an audio file to the server to be transcribed and then put into Chat GPT
 * </p>
 * <p>
 * Credit: some of the code is taken from CSE110 Lab 6
 * Chat GPT used for audio encoding protocol
 * The following assumptions are made for this store:
 *     <ul>
 *         <li>Audio file will be transcribed into bytes and included in HTTP request.
 *              Server will be running for this file to be accessed</li>
 *     </ui>
 * </p>
 */
public class RequestHandler implements HttpHandler{

    //storage for the server
    private final TsvStore data;

    //Request Handler Constructor
    /*
     * @ param data, TsvStore to keep track of changes made through server requests
     */
    public RequestHandler(TsvStore data) {
        this.data = data;
    }

    /*
     * general handle method for any incoming HTTP request to the server
     * @param httpExchange should be the HTTP exchange made by the request
     * @throws IOException if the HTTP request is invalid or not of a type the server can handle
     */
    public void handle(HttpExchange httpExchange) throws IOException {
        //method variablse
        String response = "Request Received";
        String method = httpExchange.getRequestMethod();

        //identify request type and call method to handle each type if applicable
        try {
          if (method.equals("GET")) {
              response = handleGet(httpExchange);
          } else if (method.equals("POST")) {
              response = handlePost(httpExchange);
          } else if (method.equals("PUT")){
              response = handlePost(httpExchange);
          } else if(method.equals("DELETE")){
              response = handleDelete(httpExchange);
          }
          else{
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

    /*
     * method to handle GET request
     * request should return question history
     * @param httpExchange, httpExchange for request passed through by handle()
     * @throws IOException if the GET request is broken
     * proper command is /history, all others will send back base response
     * @return a string with the server response
     */
    private String handleGet(HttpExchange httpExchange) throws IOException {
      //set up method variablse
      String response = "Invalid GET request";
      URI uri = httpExchange.getRequestURI();
      String query = uri.getRawQuery();

      //check for proper endpoint
      if (uri.getPath().equals("/history")) {
        return data.getEntries().toString();
      }
      return response;
    }

    
    /*
     * Method to handle DELETE request
     * @param httpExchange, httpExchange for request passed through by handle()
     * @throws IOEXception if the DELETE request is broken
     * Checks for two possible endpoints, /delete-question and /clear-all
     * /delete-question deletes a single entry, and /clear-all clears all entries
     * @return a string with the server response
     */
    private String handleDelete(HttpExchange httpExchange) throws IOException{
      String response = "Invalid DELETE request";
      URI uri = httpExchange.getRequestURI();
      String query = uri.getRawQuery();

      //delete single question
      if (uri.getPath().equals("/delete-question")) {
        int ID;
        String value = query.substring(query.indexOf("=") + 1);
        try{//see if ID number can be found
          ID = Integer.valueOf(value);
        }catch(Exception e){
          return response;
        }
        if(data.delete(ID)) {//check for correct deletion
          response = "Deleted entry {" + ID + "}";
        }else {
          response = "No entry found for {" + ID + "}";
        }
      }

      //seeing if clear all was queried
      if (uri.getPath().equals("/clear-all")){
          if(data.clearAll()){
            response = "All entries cleared";
          }
      }

      data.save();
      return response;
    }

    /*
     * method to handle POST(and PUT) request
     * request should include raw bytes for an audio file that will be recombined into a file
     * audio will be transcribed with the Whisper API and then given to the Chat GPT AI
     * server should respond with question and answer
     * @param httpExchange, httpExchange for request passed through by handle()
     * @throws IOException if the POST or PUT request is broken
     * proper command is /ask, all others will send back base response
     * @return a string with the server response
     */
    private String handlePost(HttpExchange httpExchange) throws IOException {
      
      String response = "Invalid POST Request";
      
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

      // Save the audio bytes as a sound file
      String soundFilePath = saveAudioFile(audioBytes);

      //access Whisper API
      IWhisper whisper = new Whisper(Constants.OPENAI_API_KEY);
      WhisperCheck whisperCheck = new WhisperCheck(whisper, new File(soundFilePath));

      String question = whisperCheck.output();

      if (whisperCheck.isExceptionThrown()) {
        response = question;
        return response;
      }

      //if audio is transcribed, pass to Chat GPT
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

      response = entry.toString() + " ID: " + newID;

      data.save();

      return response;
  }

  /*
   * method to take bytes of audio and convert to a file
   * @param audioBytes, byte array as created from server erquest
   * @throws IOException if there is a problem with the output stream
   * @return a string for the Path of the file which will be turned into a new file
   */
  private String saveAudioFile(byte[] audioBytes) throws IOException {
    String soundFilePath = "question.wav"; // Provide the desired file path
    try (OutputStream outStream = new FileOutputStream(soundFilePath)) {
      outStream.write(audioBytes);
    }
    return soundFilePath;
  }
    
}
