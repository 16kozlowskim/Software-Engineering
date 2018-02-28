/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ai.api.examples;

import java.sql.Array;
import java.util.StringJoiner;
import java.util.Random;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.DataOutputStream;

import ai.api.AIConfiguration;
import ai.api.AIDataService;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

import javax.xml.crypto.Data;

/**
 * Text client reads requests line by line from standard input.
 */
public class VTA {

	private static final String INPUT_PROMPT = "> ";
	private static int newsPreference;
	private static final String USER_AGENT = "Mozilla/5.0";


	/**
	 * @param args List of parameters:<br>
	 *             First parameters should be valid api key<br>
	 *             Second and the following args should be file names containing audio data.
	 */
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Please specify API key");
		}

		AIConfiguration configuration = new AIConfiguration(args[0]);
		AIDataService dataService = new AIDataService(configuration);

		String line;
		newsPreference = 3;

		try {
			resetCompanies();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		//Set up database
		try {
			DataStore.initDB();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			System.out.print(INPUT_PROMPT);
			while (null != (line = reader.readLine())) {

				try {
					AIRequest request = new AIRequest(line);

					AIResponse response = dataService.request(request);

					if (response.getStatus().getCode() == 200) {
						System.out.println(response.getResult().getFulfillment().getSpeech());
						if (response.getResult().getMetadata().getIntentName().equals("CompanyQuery")) {
							if (checkDate(response)) {
								companyDateQuery(response);
							} else {
								companyQuery(response);
							}
						} else if (response.getResult().getMetadata().getIntentName().equals("SectorQuery")) {
							if (checkDate(response)) {
								sectorDateQuery(response);
							} else {
								sectorQuery(response);
							}
						} else if (response.getResult().getMetadata().getIntentName().equals("CompanyOrSectorQueryContext")) {
							for (Entry<String, JsonElement> parameter : response.getResult().getParameters().entrySet()) {
								if (parameter.getKey().equals("CompanyName") && !parameter.getValue().equals("")) {
									if (checkDate(response)) {
										companyDateQuery(response);
									} else {
										companyQuery(response);
									}
								} else if (parameter.getKey().equals("Sectors") && !parameter.getValue().equals("")) {
									System.out.println("This is a sector context query");
									if (checkDate(response)) {
										sectorDateQuery(response);
									} else {
										sectorQuery(response);
									}
								}
								else {
									System.out.println("Sorry, I didn't catch that");
								}
							}
						} else if (response.getResult().getMetadata().getIntentName().equals("News") || response.getResult().getMetadata().getIntentName().equals("NewsContext")) {
							newsQuery(response);
						} else if (response.getResult().getMetadata().getIntentName().equals("Top")) {
							risersOrFallers(response,true);
						} else if (response.getResult().getMetadata().getIntentName().equals("Bottom")) {
							risersOrFallers(response,false);
						} else if (response.getResult().getMetadata().getIntentName().equals("UpdateFrequency")) {
							System.out.println("This is a frequency update");
						} else if (response.getResult().getMetadata().getIntentName().equals("ResetFavourites")) {
							DataStore.resetDB();
							System.out.println("Done");
						} else if (response.getResult().getMetadata().getIntentName().equals("NewsPreference")) {
							for (Entry<String, JsonElement> parameter : response.getResult().getParameters().entrySet()) {
								if (parameter.getKey().equals("number-integer")) {
									newsPreference = parameter.getValue().getAsInt();
									System.out.println("Done");
								}
							}
						}
						for (Entry<String, JsonElement> parameter : response.getResult().getParameters().entrySet()) {
							System.out.printf("%s : %s%n", parameter.getKey(), parameter.getValue());
						}
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
	 * Converts attributes to their index for historical company queries
	 * @param attribute The attribute to be converted
	 * @return the index of the given attribute
	 */
	public static int getIndexOfAttribute(String attribute) {
		DataStore.incrementAttribute(attribute);
		attribute = attribute.replace("\"","");
		if (attribute.toLowerCase().equals("open") || attribute.toLowerCase().equals("price")) {
			return 1;
		}
		if (attribute.toLowerCase().equals("high")) {
			return 2;
		}
		if (attribute.toLowerCase().equals("low")) {
			return 3;
		}
		if (attribute.toLowerCase().equals("close")) {
			return 4;
		}
		if (attribute.toLowerCase().equals("volume")) {
			return 5;
		}
		return -1;
	}

	/**
	 * Converts attributes to their index for current company queries
	 * @param attribute The attribute to be converted
	 * @return the index of the given attribute
	 */
	public static int getIndexOfAttribute2(String attribute) {
		DataStore.incrementAttribute(attribute);
		attribute = attribute.replace("\"","");
		if (attribute.toLowerCase().equals("price")) {
			return 0;
		}
		if (attribute.toLowerCase().equals("absolute change")) {
			return 1;
		}
		if (attribute.toLowerCase().equals("percentage change")) {
			return 2;
		}
		if (attribute.toLowerCase().equals("day low-high") || attribute.toLowerCase().equals("low") || attribute.toLowerCase().equals("high")) {
			return 3;
		}
		if (attribute.toLowerCase().equals("52 low-high")) {
			return 4;
		}
		if (attribute.toLowerCase().equals("open")) {
			return 5;
		}
		if (attribute.toLowerCase().equals("volume")) {
			return 6;
		}
		if (attribute.toLowerCase().equals("market cap")) {
			return 7;
		}
		if (attribute.toLowerCase().equals("pe ratio")) {
			return 8;
		}
		if (attribute.toLowerCase().equals("dividend yield")) {
			return 9;
		}
		if (attribute.toLowerCase().equals("earnings per share")) {
			return 10;
		}
		if (attribute.toLowerCase().equals("shares outstanding")) {
			return 11;
		}
		if (attribute.toLowerCase().equals("beta")) {
			return 12;
		}
		if (attribute.toLowerCase().equals("institutional ownership")) {
			return 13;
		}
		return 14;
	}

	/**
	 * Converts attributes to their index for sector queries
	 * @param attribute The attribute to be converted
	 * @return the index of the given attribute
	 */
	public static int getIndexOfAttribute3(String attribute){
		if(attribute.toLowerCase().equals("price")){
			return 3;
		}
		if(attribute.toLowerCase().equals("absolute change")){
			return 4;
		}
		if(attribute.toLowerCase().equals("percentage change")){
			return 5;
		}
		return 7;
	}

	/**
	 * Handles current company queries
	 * @param response The chatbot's response to the user's query
	 */
	public static void companyQuery(AIResponse response){
		String ticker = "";
		String data[] = new String[2];
		JsonArray attributes;
		for (Entry<String, JsonElement> parameter : response.getResult().getParameters().entrySet()) {
			if (parameter.getKey().equals("CompanyName"))
				ticker = parameter.getValue().getAsString();
				if(ticker.equals("")) break;
			if (parameter.getKey().equals("Attributes")) {
				attributes = parameter.getValue().getAsJsonArray();
				for (int i = 0; i < attributes.size(); i++) {
					try {
						data[i] = DataBridge.getCompanyData(ticker)[getIndexOfAttribute2(attributes.get(i).getAsString())];
					} catch (IndexOutOfBoundsException e) {
						System.out.print("Sorry, I couldn't find that for you");
					}
				}
				DataStore.incrementCompany(ticker);
			}
		}
		for (int i = 0; i < 2; i++) {
			if (data[i] != null) System.out.println(data[i]);
		}
	}

	/**
	 * Handles historical company queries
	 * @param response The chatbot's response to the user's query
	 */
	public static void companyDateQuery(AIResponse response){
		String ticker = "";
		String date = "";
		String data[] = new String[2];
		JsonArray attributes;
		for (Entry<String, JsonElement> parameter : response.getResult().getParameters().entrySet()) {
			if (parameter.getKey().equals("CompanyName"))
				ticker = parameter.getValue().getAsString();
			if (parameter.getKey().equals("date")) {
				date = parameter.getValue().getAsString().replace("-", "");
			}
			if (parameter.getKey().equals("Attributes")) {
				attributes = parameter.getValue().getAsJsonArray();
				for (int i = 0; i < attributes.size(); i++) {
					try {
						data[i] = DataBridge.getHistoricalData(ticker, "d", date)[getIndexOfAttribute(attributes.get(i).getAsString())];
					} catch (IndexOutOfBoundsException e) {
						data[i] = "Sorry, I couldn't find that for you";
					}
				}
				DataStore.incrementCompany(ticker);
			}
		}
		for (int i = 0; i < 2; i++) {
			if (data[i] != null) System.out.println(data[i]);
		}
	}

	/**
	 * Handles current sector queries
	 * @param response The chatbot's response to the user's query
	 */
	public static void sectorQuery(AIResponse response){
		String ticker = "";
		String sectorNum = "";
		ArrayList<String[]> data = new ArrayList<>();
		JsonArray attributes = null;
		for (Entry<String, JsonElement> parameter : response.getResult().getParameters().entrySet()) {
			if (parameter.getKey().equals("Sectors")){
				ticker = parameter.getValue().getAsString();
				sectorNum = DataStore.getSectorNum(ticker.replace("\"",""));
				data = DataBridge.getSectorData(sectorNum);
				DataStore.incrementSector(ticker);
			}
			if (parameter.getKey().equals("Attributes")) {
				attributes = parameter.getValue().getAsJsonArray();
			}
		}
		for (int j = 0; j < attributes.size(); j++) {
			DataStore.incrementAttribute(attributes.get(j).getAsString());
		}
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i) != null) {
				System.out.print(data.get(i)[1]+" ("+data.get(i)[0]+"): ");
				for (int j = 0; j < attributes.size(); j++) {
					try {
						System.out.print(data.get(i)[getIndexOfAttribute3(attributes.get(j).getAsString())]+" ");
					} catch (IndexOutOfBoundsException e) {
						System.out.print("Sorry, I couldn't find that for you");
					}
				}
			}
			System.out.println();
		}
	}

	/**
	 * Handles historical sector queries
	 * @param response The chatbot's response to the user's query
	 */
	public static void sectorDateQuery(AIResponse response){
		System.out.println("Sorry, I can't find historical data on sectors yet");
	}

	/**
	 * Handles queries about the risers and fallers
	 * @param response The chatbot's response to the user's query
	 * @param risers <code>true</code> To lookup the risers
	 *               <code>false</code> To lookup the fallers
	 */
	public static void risersOrFallers(AIResponse response, Boolean risers){
		int top;
		for (Entry<String, JsonElement> parameter : response.getResult().getParameters().entrySet()) {
			if (parameter.getKey().equals("number-integer")) {
				top = parameter.getValue().getAsInt();
				ArrayList<String[]> data = DataBridge.getRisersFallers(risers);
				for (int i = 0; i < data.size() && i < top; i++) {
					for (int j = 0; j < data.get(i).length; j++) {
						System.out.print(data.get(i)[j]);
					}
					System.out.println();
				}
			}
		}
	}

	/**
	 * Handles news queries
	 * @param response The chatbot's response to the user's query
	 */
	public static void newsQuery(AIResponse response){
		String ticker = "";
		boolean company = false;
		for (Entry<String, JsonElement> parameter : response.getResult().getParameters().entrySet()) {
			if (parameter.getKey().equals("CompanyName") && !parameter.getValue().equals("")) {
				ticker = parameter.getValue().getAsString();
				company = true;
				DataStore.incrementCompany(ticker);
			}
			else if (parameter.getKey().equals("Sectors") && !parameter.getValue().equals("")){
				company = false;
				ticker = parameter.getValue().getAsString();
				DataStore.incrementSector(ticker);
			}
		}
		String[] data = DataBridge.getNews(ticker,company,newsPreference);
		for (int i = 0; i < data.length; i++) {
			if(data[i]!= null)System.out.println(data[i]);
		}
	}

	/**
	 * Checks if the user's query included a date
	 * @param response The chatbot's response to the user's query
	 * @return <code>true</code> If a date is included
	 * 		   <code>false</code> If a date isn't included
	 */
	public static boolean checkDate(AIResponse response) {
		//Check if a date is specified
		for (Entry<String, JsonElement> parameter : response.getResult().getParameters().entrySet()) {
			if (parameter.getKey().equals("date")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the favourites in risers and fallers as two arraylists in an array:
	 * [riserCompany1, riserCompany2, ...]
	 * [fallerCompany1, fallerCompany2, ...]
	 *
	 * @return
	 */
	public static ArrayList<String>[] favouritesInRisersFallers() {
		ArrayList<String[]> risers = DataBridge.getRisersFallers(true);
		ArrayList<String[]> fallers = DataBridge.getRisersFallers(false);

		ArrayList<String> favourites = DataStore.getFavouriteCompanies(5);
		ArrayList<String> favouritesInRisers = new ArrayList<>();
		ArrayList<String> favouritesInFallers = new ArrayList<>();

		for (int i = 0; i < favourites.size(); i++) {
			for (int j = 0; j < risers.size(); j++) {
				if (favourites.get(i).equals(risers.get(j)[0]));
					favouritesInRisers.add(favourites.get(i));
			}
		}

		for (int i = 0; i < favourites.size(); i++) {
			for (int j = 0; j < fallers.size(); j++) {
				if (favourites.get(i).equals(fallers.get(j)[0]));
				favouritesInFallers.add(favourites.get(i));
			}
		}

		ArrayList<String>[] arr = new ArrayList<String>[2];
		arr[0] = favouritesInRisers;
		arr[1] = favouritesInFallers;

		return arr;
	}

	/**
	 * Used to create AI notifications about favourite company or sector with a favourite attribute.
	 * Favourite attributes won't be used in case the query is about a sector, rather all the data will be displayed.
	 * Returns an array of size 2 and in the case the first entry is a company, the second entry will be an attribute,
	 * and if the first entry is a sector, the second entry will be null, like so;
	 * [companyName, attributeName] or [sectorName, null]
	 * @return
	 */
	public static String[] notificationData() {
		ArrayList<String> favourites = null;
		String company = null;
		String sector = null;
		String[] notification = new String[2];

		Random rng = new Random();

		if (rng.nextInt(10) < 7) {
			favourites = DataStore.getFavouriteCompanies(5);
			company = favourites.get(rng.nextInt(5));
			ArrayList<String> favouriteAttributes = DataStore.getFavouriteAttributes(5);
			String attribute = favouriteAttributes.get(rng.nextInt(5));

			notification[0] = company;
			notification[1] = attribute;

		}
		else {
			favourites = DataStore.getFavouriteSectors(5);
			sector = favourites.get(rng.nextInt(5));
			notification[0] = sector;
			notification[1] = null;
		}

		return notification;


	}

	public static void resetCompanies() throws Exception {
		deleteCompanies();
		fillCompanies();
	}

	public static void deleteCompanies() throws Exception {
		String url = "https://api.dialogflow.com/v1/entities/7567f203-7272-4c87-82a3-0e0aa6e0d7f2/entries?v=20150910";
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

		con.setRequestMethod("DELETE");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Authorization","Bearer 3e87883ff05f4b06abe0a57ada75c486");
		con.setRequestProperty("Content-Type","application/json");
		con.setDoOutput(true);

		HashMap<String, String> companies = DataStore.getCompanyInfo();

		StringJoiner joiner = new StringJoiner(", ");
		companies.forEach((k, v) -> {
			joiner.add("\""+ v +"\"");
		});

		String body = "[" + joiner.toString() + "]";

		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(body);
		wr.flush();
		wr.close();

	}

	public static void fillCompanies() throws Exception {
		String url = "https://api.dialogflow.com/v1/entities/7567f203-7272-4c87-82a3-0e0aa6e0d7f2/entries?v=20150910";
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

		con.setRequestMethod("PUT");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Authorization","Bearer 3e87883ff05f4b06abe0a57ada75c486");
		con.setRequestProperty("Content-Type","application/json");

		HashMap<String, String> companies = DataStore.getCompanyInfo();


		StringJoiner joiner = new StringJoiner(", ");

		companies.forEach((k, v) -> {
			joiner.add("{\"synonyms\": [\""+ k.replaceAll("\\(", "").replaceAll("\\)", "") + "\", \"" + v + "\"], \"value\": \""+ v +"\"}");
		});

		String body = "[" + joiner.toString() + "]";

		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(body);
		wr.flush();
		wr.close();

	}

}