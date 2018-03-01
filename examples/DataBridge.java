package ai.api.examples;

import java.io.*;
import java.util.*;

public class DataBridge {

	private static final String csvFile = "C:\\Users\\ojwoo\\Documents\\Warwick\\CS261\\Coursework\\dialogflow-java-client-master\\samples\\clients\\VirtualTradingAssistant\\src\\main\\java\\ai\\api\\examples\\fileStore\\file.csv";


	/**
	 * Gets a news article on a company
	 * @param  String search
	 * @return        [title, link, summary]
	 */
	public static String[] getNews(String search, Boolean isCompany, int num) {
		String query = null;
		if (!isCompany) query = "ftse%20" + search.replaceAll(" ", "%20");
		else query = "lon:" + search;
		String cmd = "C:\\Python27\\python C:\\Users\\ojwoo\\Documents\\Warwick\\CS261\\Coursework\\dialogflow-java-client-master\\samples\\clients\\VirtualTradingAssistant\\src\\main\\java\\ai\\api\\examples\\scraper\\news.py " + query + " " + num;
		String s = "";
		String csvSplitBy = "@";
		String[] data = new String[num*3];

		try {
			Process p = Runtime.getRuntime().exec(cmd);
			try {
				p.waitFor();
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
			FileReader file = new FileReader(csvFile);
			BufferedReader stdInput = new BufferedReader(file);

			int i = 0;
			while ((s = stdInput.readLine()) != null) {
				if (s.length() > 0){
					data[i] = s;
					i++;
				}
			}
			stdInput.close();
			file.close();
		} catch (IOException e) {
			System.out.print(e.getMessage());
		}
		return data;
	}

	/**
	 * Returns data for a particular company
	 *
	 * @param String ticker        has to be fetched from companyInfo in
	 *               DataStore
	 * @return [spot price, absolute change, % change, low-high in latest
	 * trading day, 52-week low-high, opening price on latest trading
	 * day, volume/avg volume, Mkt cap, PE ratio, latest dividend/
	 * dividend yield, earnings per share, shares, beta, institutional
	 * ownership]
	 * @throws IOException
	 */
	public static String[] getCompanyData(String ticker) {
		String cmd = "C:\\Python27\\python C:\\Users\\ojwoo\\Documents\\Warwick\\CS261\\Coursework\\dialogflow-java-client-master\\samples\\clients\\VirtualTradingAssistant\\src\\main\\java\\ai\\api\\examples\\scraper\\googScraper.py "+ticker;
		String s = "";
		String csvSplitBy = "@";
		String[] data = new String[14];

		try {
			Process p = Runtime.getRuntime().exec(cmd);
			try {
				System.out.println("Alive:"+p.isAlive());
				BufferedReader bs = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				while((s=bs.readLine()) != null){
					System.out.println(s);
				}
				p.waitFor();
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
			FileReader file = new FileReader(csvFile);
			BufferedReader stdInput = new BufferedReader(file);
			int i = 0;
			while (((s = stdInput.readLine()) != null) && (i < 14)) {
				if(s.length()>0){
					data[i] = s;
					i++;
				}
			}
			stdInput.close();
			file.close();
		} catch (IOException e) {
			System.out.print(e.getMessage());
		}
		return data;
	}

	/**
	 * Gets the risers and faller on ftse
	 *
	 * @param Boolean getRisers     set true if you want risers, false for fallers
	 * @return [ticker, company name, spot price, absolute day change, % day change]
	 * @throws IOException
	 */
	public static ArrayList<String[]> getRisersFallers(Boolean getRisers) {
		String cmd;
		if (getRisers)
			cmd = "C:\\Python27\\python C:\\Users\\ojwoo\\Documents\\Warwick\\CS261\\Coursework\\dialogflow-java-client-master\\samples\\clients\\VirtualTradingAssistant\\src\\main\\java\\ai\\api\\examples\\scraper\\risersFallers.py risers";
		else
			cmd = "C:\\Python27\\python C:\\Users\\ojwoo\\Documents\\Warwick\\CS261\\Coursework\\dialogflow-java-client-master\\samples\\clients\\VirtualTradingAssistant\\src\\main\\java\\ai\\api\\examples\\scraper\\risersFallers.py fallers";
		String s = "";
		String csvSplitBy = "@";
		ArrayList<String[]> data = new ArrayList<String[]>();
		String[] arr = new String[5];

		try {
			Process p = Runtime.getRuntime().exec(cmd);
			try {
				p.waitFor();
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}

			FileReader file = new FileReader(csvFile);
			BufferedReader stdInput = new BufferedReader(file);

			while ((s = stdInput.readLine()) != null) {
				if (s.split(csvSplitBy).length == 5) data.add(s.split(csvSplitBy));
			}
			stdInput.close();
			file.close();
		} catch (IOException e) {
			System.out.print(e.getMessage());
		}
		return data;
	}

	public static HashMap<String, String> fillCompany() {
		String cmd = "C:\\Python27\\python C:\\Users\\ojwoo\\Documents\\Warwick\\CS261\\Coursework\\dialogflow-java-client-master\\samples\\clients\\VirtualTradingAssistant\\src\\main\\java\\ai\\api\\examples\\scraper\\ftseTickerCompanies.py";
		String s = "";
		String csvSplitBy = "@";
		HashMap<String, String> data = new HashMap<String, String>();
		String[] arr = null;

		try {
			Process p = Runtime.getRuntime().exec(cmd);
			try {
				p.waitFor();
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
			FileReader file = new FileReader(csvFile);
			BufferedReader stdInput = new BufferedReader(file);

			while ((s = stdInput.readLine()) != null) {
				if (s.split(csvSplitBy).length == 2) {
					arr = s.split(csvSplitBy);
					data.put(arr[1], arr[0]);
				}
			}
			stdInput.close();
			file.close();
		} catch (IOException e) {
			System.out.print(e.getMessage());
		}
		return data;
	}

	/**
	 * @param String ticker        fetch from companyInfo in datastore
	 * @param String interval      'd', 'm', 'q', 'y' (daily, monthly, quarterly, yearly)
	 * @param String date          YYYYMMDD
	 * @return [date, open, high, low, close, volume]
	 * @throws IOException
	 */
	public static String[] getHistoricalData(String ticker, String interval, String date) {
		String symbol = ticker;
		if (symbol.endsWith(".")) symbol += "uk";
		else symbol += ".uk";

		String cmd = "C:\\Python27\\python C:\\Users\\ojwoo\\Documents\\Warwick\\CS261\\Coursework\\dialogflow-java-client-master\\samples\\clients\\VirtualTradingAssistant\\src\\main\\java\\ai\\api\\examples\\scraper\\historicalScrape.py " + symbol + " " + interval + " " + date + " " + date;
		System.out.println(cmd);
		String s = "";
		String csvSplitBy = "@";
		String[] data = new String[6];

		try {
			Process p = Runtime.getRuntime().exec(cmd);
			try {
				p.waitFor();
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}

			FileReader file = new FileReader(csvFile);
			BufferedReader stdInput = new BufferedReader(file);
			int i = 0;
			while ((s = stdInput.readLine()) != null) {
				if (i == 0) {
					i++;
					continue;
				}
				if (s.split(csvSplitBy).length == 6) data = s.split(csvSplitBy);
			}
			stdInput.close();
			file.close();
		} catch (IOException e) {
			System.out.print(e.getMessage());
		}
		return data;
	}

	/**
	 * @param String sectorNum     fetch from sectorNum in DataStore
	 * @return [ticker, name, currency, price, day change, % day change]
	 * @throws IOException
	 */
	public static ArrayList<String[]> getSectorData(String sectorNum) {

		String cmd = "C:\\Python27\\python C:\\Users\\ojwoo\\Documents\\Warwick\\CS261\\Coursework\\dialogflow-java-client-master\\samples\\clients\\VirtualTradingAssistant\\src\\main\\java\\ai\\api\\examples\\scraper\\sectorsScraper.py " + sectorNum;
		String s = "";
		String csvSplitBy = "@";
		ArrayList<String[]> data = new ArrayList<String[]>();

		try {
			Process p = Runtime.getRuntime().exec(cmd);
			try {
				p.waitFor();
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}

			FileReader file = new FileReader(csvFile);
			BufferedReader stdInput = new BufferedReader(file);

			while ((s = stdInput.readLine()) != null) {
				if(s.length()>0)data.add(s.split(csvSplitBy));
			}
			stdInput.close();
			file.close();
		} catch (IOException e) {
			System.out.print(e.getMessage());
		}
		return data;
	}
}