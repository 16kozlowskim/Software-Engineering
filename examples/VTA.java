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

	private static boolean start = true;
	private static final String INPUT_PROMPT = "> ";
	private static int newsPreference = 3;
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
					interpretQuery(response);

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
		if (attribute.toLowerCase().equals("day low-high") || attribute.toLowerCase().equals("day low") || attribute.toLowerCase().equals("day high")) {
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
	public static String companyQuery(AIResponse response){
		String ticker = "";
		String data[] = new String[2];
		JsonArray attributes;
		for (Entry<String, JsonElement> parameter : response.getResult().getParameters().entrySet()) {
			if (parameter.getKey().equals("CompanyName")){
				ticker = parameter.getValue().getAsString();
				DataStore.incrementCompany(ticker);
			}
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
				if(attributes.size()==0){
					String outputData = ticker+" top stats:<br />";
					ArrayList<String> favouriteAttributes = DataStore.getFavouriteAttributes(5);
					String[] companyData = DataBridge.getCompanyData(ticker);
					for(int i = 0; i < favouriteAttributes.size(); i++){
						try{
							outputData += favouriteAttributes.get(i)+": "+companyData[getIndexOfAttribute2(favouriteAttributes.get(i))]+"<br />";
						}catch(IndexOutOfBoundsException e){

						}
					}
					companyData = DataBridge.getNews(ticker,true,1);
					outputData += "<p>News on "+ticker+": <br />";
					for (int i = 0; i < companyData.length; i++) {
						if((i+1) % 3 == 0 && companyData[i] != null){
							outputData += "<a href=\""+companyData[i-1]+"\">"+companyData[i-2]+"</a><br />"+companyData[i]+"<br />";
						}
					}
					return outputData;
				}
			}
		}
		String outputData = response.getResult().getFulfillment().getSpeech()+": " +"<br />";
		for (int i = 0; i < 2; i++) {
			if (data[i] != null) {
				System.out.println(data[i]);
				outputData += data[i] + "<br />";
			}
		}
		return outputData;
	}

	/**
	 * Handles historical company queries
	 * @param response The chatbot's response to the user's query
	 */
	public static String companyDateQuery(AIResponse response){
		String ticker = "";
		String date = "";
		String data[] = new String[2];
		JsonArray attributes;
		for (Entry<String, JsonElement> parameter : response.getResult().getParameters().entrySet()) {
			if (parameter.getKey().equals("CompanyName"))
				ticker = parameter.getValue().getAsString();
			DataStore.incrementCompany(ticker);
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
				if(attributes.size()==0){
					String[] companyData = DataBridge.getHistoricalData(ticker,"d",date);
					String outputData = ticker+" top stats from "+companyData[0]+":<br />";
					outputData += "Open: "+companyData[1]+"<br />High: "+companyData[2]+"<br />Low:"+companyData[3]+"<br />Close:"+companyData[4]+"<br />Volume:"+companyData[5];
					return outputData;
				}
			}
		}
		String outputData = response.getResult().getFulfillment().getSpeech()+": "+"<br />";
		for (int i = 0; i < 2; i++) {
			if (data[i] != null) {
				System.out.println(data[i]);
				outputData += data[i] + "<br />";
			}
		}
		return outputData;
	}

	/**
	 * Handles current sector queries
	 * @param response The chatbot's response to the user's query
	 */
	public static String sectorQuery(AIResponse response){
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
		if(attributes.size()==0){
			ArrayList<String[]> sectorData = DataBridge.getSectorData(DataStore.getSectorNum(ticker));
			String outputData = ticker+": <br />";
			for (int i = 0; i < sectorData.size(); i++) {
				if (sectorData.get(i) != null) {
					outputData += sectorData.get(i)[1]+" ("+sectorData.get(i)[0]+"): "+"<br />";
					outputData += "Price: "+sectorData.get(i)[getIndexOfAttribute3("price")]+" Absolute Change:"+sectorData.get(i)[getIndexOfAttribute3("absolute change")]+" Percentage Change: "+sectorData.get(i)[getIndexOfAttribute3("percentage change")];
				}
				outputData += "<br />";
			}
			return outputData;
		}
		String outputData = response.getResult().getFulfillment().getSpeech()+": "+"<br />";
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i) != null) {
				outputData += data.get(i)[1]+" ("+data.get(i)[0]+"): "+"<br />";
				System.out.print(data.get(i)[1]+" ("+data.get(i)[0]+"): ");
				for (int j = 0; j < attributes.size(); j++) {
					try {
						outputData += data.get(i)[getIndexOfAttribute3(attributes.get(j).getAsString())]+" ";
						System.out.print(data.get(i)[getIndexOfAttribute3(attributes.get(j).getAsString())]+" ");
					} catch (IndexOutOfBoundsException e) {
						System.out.print("Sorry, I couldn't find that for you");
						outputData = "Sorry, I couldn't find that for you";
					}
				}
			}
			System.out.println();
			outputData += "<br />";
		}
		return outputData;
	}

	/**
	 * Handles historical sector queries
	 * @param response The chatbot's response to the user's query
	 */
	public static String sectorDateQuery(AIResponse response){
		System.out.println("Sorry, I can't find historical data on sectors yet");
		return "Sorry, I can't find historical data on sectors yet";
	}

	/**
	 * Handles queries about the risers and fallers
	 * @param response The chatbot's response to the user's query
	 * @param risers <code>true</code> To lookup the risers
	 *               <code>false</code> To lookup the fallers
	 */
	public static String risersOrFallers(AIResponse response, Boolean risers){
		int top;
		String outputData = response.getResult().getFulfillment().getSpeech()+": <br />";
		for (Entry<String, JsonElement> parameter : response.getResult().getParameters().entrySet()) {
			if (parameter.getKey().equals("number-integer")) {
				top = parameter.getValue().getAsInt();
				ArrayList<String[]> data = DataBridge.getRisersFallers(risers);
				for (int i = 0; i < data.size() && i < top; i++) {
					for (int j = 0; j < data.get(i).length; j++) {
						System.out.print(data.get(i)[j]);
						outputData += data.get(i)[j];
					}
					System.out.println();
					outputData += "<br />";
				}
			}
		}
		return outputData;
	}

	/**
	 * Handles news queries
	 * @param response The chatbot's response to the user's query
	 */
	public static String newsQuery(AIResponse response){
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
		String outputData = "<p>"+response.getResult().getFulfillment().getSpeech()+": <br />";
		for (int i = 0; i < data.length; i++) {

			if((i+1) % 3 == 0 && data[i] != null){
				outputData += "<a href=\""+data[i-1]+"\">"+data[i-2]+"</a><br />"+data[i]+"<br />";
			}
			else if(data[i]!= null){
				System.out.println(data[i]);
			}
		}
		return outputData+"</p>";
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

	public static ArrayList<String[]> aiNews() {
		ArrayList<String> favouriteCompanies = DataStore.getFavouriteCompanies(1);
		ArrayList<String> favouriteSectors = DataStore.getFavouriteSectors(1);

		ArrayList<String[]> companyData = new ArrayList<>();
		ArrayList<String[]> sectorData = new ArrayList<>();

		for (String search : favouriteCompanies) {
			companyData.add(DataBridge.getNews(search, true, 5));
		}
		for (String search : favouriteSectors) {
			sectorData.add(DataBridge.getNews(search, false, 5));
		}

		ArrayList<String[]> outputData = new ArrayList<>();
		System.out.println("companyData.size(): "+companyData.size());
		for (int i = 0; i < companyData.size(); i++) {
			outputData.add(companyData.get(i));
			outputData.add(sectorData.get(i));
		}
		return outputData;
		/*String outputDataString = "";
		for(int i = 0; i < outputData.size();i++){
			for(int j = 0; j < outputData.get(i).length; j++){
				outputDataString += outputData.get(i)[j]+"<br />";
			}
		}
		return outputDataString;*/
	}

	/**
	 * Returns the favourites in risers and fallers as two arraylists in an array:
	 * [riserCompany1Data[], riserCompany2Data[], ...]
	 * [fallerCompany1Data[], fallerCompany2Data[], ...]
	 * .
	 * .
	 * .
	 * @return
	 */
	public static String favouritesInRisersFallers(boolean rise, boolean falls) {
		ArrayList<String[]> risers = DataBridge.getRisersFallers(true);
		ArrayList<String[]> fallers = DataBridge.getRisersFallers(false);

		ArrayList<String> favourites = DataStore.getFavouriteCompanies(5);
		ArrayList<String[]> favouritesInRisers = new ArrayList<>();
		ArrayList<String[]> favouritesInFallers = new ArrayList<>();

		for (int i = 0; i < favourites.size(); i++) {
			for (int j = 0; j < risers.size(); j++) {
				if (favourites.get(i).equals(risers.get(j)[0])) {
					favouritesInRisers.add(risers.get(j));
				}
			}
		}

		for (int i = 0; i < favourites.size(); i++) {
			for (int j = 0; j < fallers.size(); j++) {
				if (favourites.get(i).equals(fallers.get(j)[0])){
					favouritesInFallers.add(fallers.get(j));
				}
			}
		}
		String outputData = "";
		if(rise) {
			for (int i = 0; i < favouritesInRisers.size(); i++) {
				outputData += favouritesInRisers.get(i)[1]+" ("+favouritesInRisers.get(i)[0]+"): Price:"+favouritesInRisers.get(i)[2]+" Day Change:"+favouritesInRisers.get(i)[3]+", "+favouritesInRisers.get(i)[4]+"<br />";
				for (int j = 0; j < favouritesInRisers.get(i).length; j++) {
					outputData += favouritesInRisers.get(i)[j] + "<br />";
				}
			}
		}
		if(falls){
			for (int i = 0; i < favouritesInFallers.size(); i++) {
				for (int j = 0; j < favouritesInFallers.get(i).length; j++) {
					outputData += favouritesInFallers.get(i)[j] +" ";
				}
				outputData+="<br />";
			}
		}
		return outputData;
	}

	public static String aiData(){
		ArrayList<String> companies = DataStore.getFavouriteCompanies(3);
		String outputData = "<table>";
		ArrayList<String> favouriteAttributes = DataStore.getFavouriteAttributes(3);
		outputData += "<tr><td>NAME</td><td>"+favouriteAttributes.get(0).toUpperCase()+"</td>"+"<td>"+favouriteAttributes.get(1).toUpperCase()+"</td>"+"<td>"+favouriteAttributes.get(2).toUpperCase()+"</td></tr>";

		for(int i = 0; i < companies.size(); i++){
			outputData += "<tr> <td>"+companies.get(i)+"</td>";
			String[] companyData = DataBridge.getCompanyData(companies.get(i));
			for(int j = 0; j < favouriteAttributes.size(); j++) {
				try {
					outputData += "<td>"+companyData[getIndexOfAttribute2(favouriteAttributes.get(j))]+"</td>";
				} catch (IndexOutOfBoundsException e) {

				}
			}
			outputData += "</tr>";
		}
		outputData += "</table>";
		ArrayList<String> sectors = DataStore.getFavouriteSectors(2);
		for(int i = 0; i < sectors.size(); i++){
			ArrayList<String[]> sectorData = DataBridge.getSectorData(DataStore.getSectorNum(sectors.get(i)));
			outputData += sectors.get(i)+":<br /><table><tr><td>Name</td><td>Price</td><td>Change</td><td>% Change</td></tr>";
			for (int j = 0; j < sectorData.size(); j++) {
				if (sectorData.get(j) != null) {
					outputData += "<tr><td>"+sectorData.get(j)[1]+"("+sectorData.get(j)[0]+")</td><td>"+sectorData.get(j)[getIndexOfAttribute3("price")]+"</td><td>"+sectorData.get(j)[getIndexOfAttribute3("absolute change")]+"</td><td>"+sectorData.get(j)[getIndexOfAttribute3("percentage change")]+"</td></tr>";
				}
			}
			outputData += "</table>";
		}
		return outputData;
	}

	/**
	 * Used to create AI notifications about favourite company or sector with a favourite attribute.
	 * Favourite attributes won't be used in case the query is about a sector, rather all the data will be displayed.
	 * Returns an array of size 2 and in the case the first entry is a company, the second entry will be an attribute,
	 * and if the first entry is a sector, the second entry will be null, like so;
	 * [companyName, attributeName] or [sectorName, null]
	 * @return
	 */
	public static String notificationData() {
		ArrayList<String> favourites = null;
		String company = null;
		String sector = null;

		Random rng = new Random();
		favourites = DataStore.getFavouriteCompanies(5);
		company = favourites.get(rng.nextInt(5));
		favourites = DataStore.getFavouriteSectors(5);
		sector = favourites.get(rng.nextInt(5));
		if (rng.nextInt(10) < 7) {
			ArrayList<String> favouriteAttributes = DataStore.getFavouriteAttributes(5);
			String attribute = favouriteAttributes.get(rng.nextInt(5));

			String[] data = DataBridge.getCompanyData(company);
			try{
				System.out.println("The "+attribute+" of "+company+" is "+data[getIndexOfAttribute2(attribute)]);
				return "The "+attribute+" of "+company+" is "+data[getIndexOfAttribute2(attribute)];
			} catch(IndexOutOfBoundsException e){
				return notificationData();
			}
		}
		else {
			ArrayList<String[]> sectorData = DataBridge.getSectorData(DataStore.getSectorNum(sector));
			String outputData = sector+": <br />";
			for (int i = 0; i < sectorData.size(); i++) {
				if (sectorData.get(i) != null) {
					outputData += sectorData.get(i)[1]+" ("+sectorData.get(i)[0]+"): "+"<br />";
					System.out.print(sectorData.get(i)[1]+" ("+sectorData.get(i)[0]+"): ");
					outputData += sectorData.get(i)[getIndexOfAttribute3("price")]+" "+sectorData.get(i)[getIndexOfAttribute3("absolute change")]+" "+sectorData.get(i)[getIndexOfAttribute3("percentage change")];
				}
				outputData += "<br />";
			}
			System.out.println("NofiticationData() finished");
			return outputData;
		}
	}

	/*public static String rollingAverage(String symbol) {
		double newAvg = DataBridge.getRollingAverage(symbol);

		double oldAvg = DataStore.getRollingAvg(symbol);

		DataStore.updateRollingAvg(symbol, newAvg);

		if (oldAvg == 0) return "SMA: "+newAvg;

		return "SMA: "+newAvg+" Change since last query: "+((newAvg-oldAvg)/2);
	}*/

	public static String rollingAverage(AIResponse response) {
		System.out.println("rollingAverage");
		String ticker = "";
		for (Entry<String, JsonElement> parameter : response.getResult().getParameters().entrySet()) {
			if (parameter.getKey().equals("CompanyName")) {
				ticker = parameter.getValue().getAsString().replace("\"","");
				DataStore.incrementCompany(ticker);
				System.out.println(ticker);
				double newAvg = DataBridge.getRollingAverage(ticker);
				System.out.println(newAvg);
				double oldAvg = 0;
				try{
					oldAvg = DataStore.getRollingAvg(ticker);
				}
				catch (Exception e){
					e.printStackTrace();
				}
				System.out.println(ticker);
				DataStore.updateRollingAvg(ticker, newAvg);

				if (oldAvg == 0) return "SMA: "+newAvg;
				System.out.println("SMA: "+newAvg+" Change since last query: "+((newAvg-oldAvg)/2));
				return "SMA: "+newAvg+" Change since last query: "+((newAvg-oldAvg)/2);
			}
		}
		return "";
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


	public static String interpretQuery(AIResponse response){
		if(start){
			try {
				DataStore.fillRollingAvg();
				resetCompanies();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			start = false;
		}
		if (response.getStatus().getCode() == 200) {
			System.out.println(response.getResult().getFulfillment().getSpeech());
			if (response.getResult().getMetadata().getIntentName().equals("CompanyQuery")) {
				if (checkDate(response)) {
					return companyDateQuery(response);
				} else {
					return companyQuery(response);
				}
			} else if (response.getResult().getMetadata().getIntentName().equals("SectorQuery")) {
				if (checkDate(response)) {
					return sectorDateQuery(response);
				} else {
					return sectorQuery(response);
				}
			} else if (response.getResult().getMetadata().getIntentName().equals("CompanyOrSectorQueryContext")) {
				for (Entry<String, JsonElement> parameter : response.getResult().getParameters().entrySet()) {
					if (parameter.getKey().equals("CompanyName") && !parameter.getValue().equals("")) {
						if (checkDate(response)) {
							return companyDateQuery(response);
						} else {
							return companyQuery(response);
						}
					} else if (parameter.getKey().equals("Sectors") && !parameter.getValue().equals("")) {
						System.out.println("This is a sector context query");
						if (checkDate(response)) {
							return sectorDateQuery(response);
						} else {
							return sectorQuery(response);
						}
					}
					else {
						System.out.println("Sorry, I didn't catch that");
					}
				}
			} else if (response.getResult().getMetadata().getIntentName().equals("News") || response.getResult().getMetadata().getIntentName().equals("NewsContext")) {
				return newsQuery(response);
			} else if (response.getResult().getMetadata().getIntentName().equals("Top")) {
				return risersOrFallers(response,true);
			} else if (response.getResult().getMetadata().getIntentName().equals("Bottom")) {
				return risersOrFallers(response,false);
			} else if (response.getResult().getMetadata().getIntentName().equals("UpdateFrequency")) {
				System.out.println("This is a frequency update");
				return "This is a frequency update";
			} else if (response.getResult().getMetadata().getIntentName().equals("ResetFavourites")) {
				try {
					DataStore.resetDB();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				System.out.println("Done");
				return "Done";
			} else if (response.getResult().getMetadata().getIntentName().equals("NewsPreference")) {
				for (Entry<String, JsonElement> parameter : response.getResult().getParameters().entrySet()) {
					if (parameter.getKey().equals("number-integer")) {
						newsPreference = parameter.getValue().getAsInt();
						System.out.println("Done");
						return "Done";
					}
				}
			} else if (response.getResult().getMetadata().getIntentName().equals("notificationData")) {
				return aiData();
			} else if (response.getResult().getMetadata().getIntentName().equals("favouritesInRisers")) {
				return favouritesInRisersFallers(true, false);
			}else if (response.getResult().getMetadata().getIntentName().equals("favouritesInFallers")) {
				return favouritesInRisersFallers(false, true);
			} else if (response.getResult().getMetadata().getIntentName().equals("favourites")){
				return favouritesInRisersFallers(true,true);
			} else if (response.getResult().getMetadata().getIntentName().equals("rollingAverage")){
				return rollingAverage(response);
			} else if (response.getResult().getMetadata().getIntentName().equals("doingWell")){
				String data = "";
				for (Entry<String, JsonElement> parameter : response.getResult().getParameters().entrySet()) {
					if (parameter.getKey().equals("CompanyName") && !parameter.getValue().equals("") || parameter.getKey().equals("CompanyNameContext") && !parameter.getValue().equals("")) {
						DataStore.incrementCompany(parameter.getValue().getAsString());
						data = DataBridge.getCompanyData(parameter.getValue().getAsString())[getIndexOfAttribute2("percentage change")];
						if(data.charAt(0)=='+'){
							return parameter.getValue().toString()+" is doing well with a rise of "+data.substring(1);
						}
						else return parameter.getValue()+"is not doing well falling at "+data;
					} else if (parameter.getKey().equals("Sectors") && !parameter.getValue().equals("")) {
						DataStore.incrementSector(parameter.getValue().getAsString());
						ArrayList<String[]> sectorData = DataBridge.getSectorData(DataStore.getSectorNum(parameter.getValue().getAsString().replace("\"","")));
						String outputData = "";
						boolean positive = false;
						boolean negative = false;
						for (int i = 0; i < sectorData.size(); i++) {
							if (sectorData.get(i) != null) {
								if(sectorData.get(i)[getIndexOfAttribute3("percentage change")].charAt(0)=='+'){
									positive = true;
								}
								else negative = true;
								outputData += sectorData.get(i)[1]+" ("+sectorData.get(i)[0]+"): "+"<br />";
								outputData += sectorData.get(i)[getIndexOfAttribute3("percentage change")];
							}
							outputData += "<br />";
						}
						// The bank sector is positive/negative/mixed
						if(positive && negative) return "The "+parameter.getValue().getAsString()+" sector is mixed: <br />"+outputData;
						else if(positive) return "The "+parameter.getValue().getAsString()+" sector is doing well: <br />"+outputData;
						return "The "+parameter.getValue().getAsString()+" sector isn't doing well: <br />"+outputData;
					}
				}
				return "Sorry, I didn't catch that";
			}
			return response.getResult().getFulfillment().getSpeech();
		} else {
			System.err.println(response.getStatus().getErrorDetails());
		}
		return "";
	}
}