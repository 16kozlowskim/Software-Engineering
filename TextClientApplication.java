/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package ai.api.examples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.DataOutputStream;

import ai.api.AIConfiguration;
import ai.api.AIDataService;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;

import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import java.util.Map.Entry;
import com.google.gson.JsonElement;

/**
 * Text client reads requests line by line from standard input.
 */
public class TextClientApplication {

  private static final String INPUT_PROMPT = "> ";
  /**
   * Default exit code in case of error
   */
  private static final int ERROR_EXIT_CODE = 1;
  private static final String USER_AGENT = "Mozilla/5.0";

  /**
   * @param args List of parameters:<br>
   *        First parameters should be valid api key<br>
   *        Second and the following args should be file names containing audio data.
   */
  public static void main(String[] args) {
    if (args.length < 1) {
      showHelp("Please specify API key", ERROR_EXIT_CODE);
    }

    AIConfiguration configuration = new AIConfiguration(args[0]);

    AIDataService dataService = new AIDataService(configuration);

    String line;

    try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
      System.out.print(INPUT_PROMPT);
      while (null != (line = reader.readLine())) {

        try {
          if(line.equals("Add company")){
            sendPost();
          }
          AIRequest request = new AIRequest(line);

          AIResponse response = dataService.request(request);

          if (response.getStatus().getCode() == 200) {
            for (Entry<String,JsonElement> parameter : response.getResult().getParameters().entrySet()) {
              System.out.printf("%s : %s%n", parameter.getKey(), parameter.getValue());
            }
            System.out.println(response.getResult().getFulfillment().getSpeech());
          } else {
            System.err.println(response.getStatus().getErrorDetails());
          }
        } catch (Exception ex) {
          ex.printStackTrace();
        }

        System.out.print(INPUT_PROMPT);
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    System.out.println("See ya!");
  }


  /**
   * Output application usage information to stdout and exit. No return from function.
   * 
   * @param errorMessage Extra error message. Would be printed to stderr if not null and not empty.
   * 
   */
  private static void showHelp(String errorMessage, int exitCode) {
    if (errorMessage != null && errorMessage.length() > 0) {
      System.err.println(errorMessage);
      System.err.println();
    }

    System.out.println("Usage: APIKEY");
    System.out.println();
    System.out.println("APIKEY  Your unique application key");
    System.out.println("        See https://docs.api.ai/docs/key-concepts for details");
    System.out.println();
    System.exit(exitCode);
  }

  private static void sendPost() throws Exception{
    String url = "https://api.dialogflow.com/v1/entities/7567f203-7272-4c87-82a3-0e0aa6e0d7f2/entries?v=20150910";
    URL obj = new URL(url);
    HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

    con.setRequestMethod("POST");
    con.setRequestProperty("User-Agent", USER_AGENT);
    con.setRequestProperty("Authorization","Bearer 3e87883ff05f4b06abe0a57ada75c486");
    con.setRequestProperty("Content-Type","application/json");
A
    String body = "{\"synonyms\": [\"comp\"],\"value\": \"Company\"}";

    con.setDoOutput(true);
    DataOutputStream wr = new DataOutputStream(con.getOutputStream());
    wr.writeBytes(body);
    wr.flush();
    wr.close();

    int responseCode = con.getResponseCode();
    System.out.println("\nSending 'POST' request to URL : " + url);
    System.out.println("Post parameters : " + body);
    System.out.println("Response Code : " + responseCode);

    BufferedReader in = new BufferedReader(
            new InputStreamReader(con.getInputStream()));
    String inputLine;
    StringBuffer response = new StringBuffer();

    while ((inputLine = in.readLine()) != null) {
      response.append(inputLine);
    }
    in.close();

    //print result
    System.out.println(response.toString());

  }
}