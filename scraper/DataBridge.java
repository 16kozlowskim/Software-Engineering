import java.io.*;
import java.util.*;

public class DataBridge {

  public static void main(String[] args) {

    /*String[] companyData = DataBridge.getCompanyData("iii");

    for (int i = 0; i < companyData.length; i++) {
      System.out.println(companyData[i]);
    }

    ArrayList<String[]> risersFallers = getRisersFallers(false);

    risersFallers.forEach(k -> System.out.println(k[0] +" "+k[1]+" "+k[2]));

    HashMap<String, String> companyTicker = fillCompany();

    companyTicker.forEach((k,v) -> System.out.println("key: "+k+" value: "+v));

    String[] historical = getHistoricalData("III.uk", "m", "20180202");


    for (int j = 0; j < 6; j++) {
      System.out.println(historical[j]);
    }

    ArrayList<String[]> sector = getSectorData("2720");

    for (int i = 0; i < sector.size(); i++) {
      System.out.println(sector.size());
      for (int j = 0; j < sector.get(i).length; j++) {
        System.out.println(sector.get(i)[j]);
      }
    }

    String[] news = getNews("lse");
    for (int i = 0; i < 3; i++) {
      System.out.println(news[i]);
    }*/


  }

  /**
   * Gets a news article on a company
   * @param  String ticker
   * @return        [title, link, summary]
   */
  public static String[] getNews(String ticker) {
    String cmd = "python news.py "+ticker;
    String s = "";
    String csvSplitBy = "@";
    String[] data = new String[3];

    try {
      Process p = Runtime.getRuntime().exec(cmd);

  		BufferedReader stdInput = new BufferedReader(new
  		InputStreamReader(p.getInputStream()));

      int i = 0;
  		while ((s = stdInput.readLine()) != null) {
        data[i] = s;
        i++;
  		}
    } catch (IOException e) {
      System.out.print("Help");
    }
    return data;
  }

  /**
   * Returns data for a particular company
   * @param  String      ticker        has to be fetched from companyInfo in
   *                                   DataStore
   * @return             [spot price, absolute change, % change, low-high in latest
   *                      trading day, 52-week low-high, opening price on latest trading
   *                      day, volume/avg volume, Mkt cap, PE ratio, latest dividen/
   *                      divident yield, earnings per share, shares, beta, institutional
   *                      ownership]
   * @throws IOException
   */
  public static String[] getCompanyData(String ticker) {
    String cmd = "python googScraper.py "+ticker;
    String s = "";
    String csvSplitBy = "@";
    String[] data = new String[14];

    try {
      Process p = Runtime.getRuntime().exec(cmd);

  		BufferedReader stdInput = new BufferedReader(new
  		InputStreamReader(p.getInputStream()));

      int i = 0;
  		while (((s = stdInput.readLine()) != null) && (i < 14)) {
        data[i] = s;
        i++;
  		}
    } catch (IOException e) {
      System.out.print("Help");
    }
    return data;
  }
  /**
   * Gets the risers and faller on ftse
   * @param  Boolean     getRisers     set true if you want risers, false for fallers
   * @return             [ticker, company name, spot price, absolute day change, % day change]
   * @throws IOException
   */
  public static ArrayList<String[]> getRisersFallers(Boolean getRisers) {
    String cmd;
    if (getRisers)
      cmd = "python risersFallers.py risers";
    else
      cmd = "python risersFallers.py fallers";

    String s = "";
    String csvSplitBy = "@";
    ArrayList<String[]> data = new ArrayList<String[]>();
    String[] arr;

    try {
      Process p = Runtime.getRuntime().exec(cmd);

  		BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));


  		while ((s = stdInput.readLine()) != null) {
        data.add(s.split(csvSplitBy));
  		}
    } catch (IOException e) {
      System.out.print("Help");
    }
    return data;
  }

  public static HashMap<String, String> fillCompany() {
    String cmd = "python ftseTickerCompanies.py";
    String s = "";
    String csvSplitBy = "@";
    HashMap<String, String> data = new HashMap<String, String>();
    String[] arr;

    try {
      Process p = Runtime.getRuntime().exec(cmd);

  		BufferedReader stdInput = new BufferedReader(new
  		InputStreamReader(p.getInputStream()));


  		while ((s = stdInput.readLine()) != null) {
        arr = s.split(csvSplitBy);
        data.put(arr[1], arr[0]);
  		}
    } catch (IOException e) {
      System.out.print("Help");
    }
    return data;
  }
  /**
   *
   * @param  String      ticker        fetch from companyInfo in datastore
   * @param  String      interval      'd', 'm', 'q', 'y' (daily, monthly, quarterly, yearly)
   * @param  String      date          YYYYMMDD
   * @return             [date, open, high, low, close, volume]
   * @throws IOException
   */
  public static String[] getHistoricalData(String ticker, String interval, String date) {
    String symbol = ticker;
    if (symbol.endsWith(".")) symbol += "uk";
    else symbol += ".uk";

    String cmd = "python historicalScrape.py "+symbol+" "+interval+" "+date+" "+date;
    String s = "";
    String csvSplitBy = "@";
    String[] data = null;

    try {
      Process p = Runtime.getRuntime().exec(cmd);

  		BufferedReader stdInput = new BufferedReader(new
  		InputStreamReader(p.getInputStream()));

      int i = 0;
  		while ((s = stdInput.readLine()) != null) {
        if (i == 0) {
          i++;
          continue;
        }
        data = s.split(csvSplitBy);
  		}
    } catch (IOException e) {
      System.out.print("Help");
    }
    return data;
	}
  /**
   *
   * @param  String      sectorNum     fetch from sectorNum in DataStore
   * @return             [ticker, name, currency, price, day change, % day change]
   * @throws IOException
   */
  public static ArrayList<String[]> getSectorData(String sectorNum) {

    String cmd = "python sectorsScraper.py "+sectorNum;
    String s = "";
    String csvSplitBy = "@";
    ArrayList<String[]> data = new ArrayList<String[]>();

    try {
      Process p = Runtime.getRuntime().exec(cmd);

      BufferedReader stdInput = new BufferedReader(new
      InputStreamReader(p.getInputStream()));


      while ((s = stdInput.readLine()) != null) {
        data.add(s.split(csvSplitBy));
      }
    } catch (IOException e) {
      System.out.print("Help");
    }
    return data;
  }

}
