package ai.api.examples;
import java.io.*;
import java.util.*;

public class DataBridge {

  private static final String csvFile = "./src/main/java/ai/api/examples/fileStore/file.csv";


  /**
   * Gets a news article on a company
   * @param  String ticker
   * @return        [title, link, summary]
   */
  public static String[] getNews(String ticker) {
    String cmd = "python ./src/main/java/ai/api/examples/scraper/news.py "+ticker;
    String s = "";
    String csvSplitBy = "@";
    String[] data = new String[3];

    try {
      Process p = Runtime.getRuntime().exec(cmd);
      try {
        p.waitFor();
      } catch (InterruptedException e) {
        System.out.println(e.getMessage());
      }
      BufferedReader stdInput = new BufferedReader(new FileReader(csvFile));

      int i = 0;
  		while ((s = stdInput.readLine()) != null) {
        data[i] = s;
        i++;
  		}
    } catch (IOException e) {
      System.out.print(e.getMessage());
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
    String cmd = "python ./src/main/java/ai/api/examples/scraper/googScraper.py "+ticker;
    String s = "";
    String csvSplitBy = "@";
    String[] data = new String[14];

    try {
      Process p = Runtime.getRuntime().exec(cmd);
      try {
        p.waitFor();
      } catch (InterruptedException e) {
        System.out.println(e.getMessage());
      }

      BufferedReader stdInput = new BufferedReader(new FileReader(csvFile));

      int i = 0;
  		while (((s = stdInput.readLine()) != null) && (i < 14)) {
        data[i] = s;
        i++;
  		}
    } catch (IOException e) {
      System.out.print(e.getMessage());
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
      cmd = "python ./src/main/java/ai/api/examples/scraper/risersFallers.py risers";
    else
      cmd = "python ./src/main/java/ai/api/examples/scraper/risersFallers.py fallers";

    String s = "";
    String csvSplitBy = "@";
    ArrayList<String[]> data = new ArrayList<String[]>();
    String[] arr;

    try {
      Process p = Runtime.getRuntime().exec(cmd);
      try {
        p.waitFor();
      } catch (InterruptedException e) {
        System.out.println(e.getMessage());
      }

  		BufferedReader stdInput = new BufferedReader(new FileReader(csvFile));


  		while ((s = stdInput.readLine()) != null) {
        data.add(s.split(csvSplitBy));
  		}
    } catch (IOException e) {
      System.out.print(e.getMessage());
    }
    return data;
  }

  public static HashMap<String, String> fillCompany() {
    String cmd = "python ./src/main/java/ai/api/examples/scraper/ftseTickerCompanies.py";
    String s = "";
    String csvSplitBy = "@";
    HashMap<String, String> data = new HashMap<String, String>();
    String[] arr;

    try {
      Process p = Runtime.getRuntime().exec(cmd);
        try {
          p.waitFor();
        } catch (InterruptedException e) {
          System.out.println(e.getMessage());
        }
  		BufferedReader stdInput = new BufferedReader(new FileReader(csvFile));


  		while ((s = stdInput.readLine()) != null) {
          arr = s.split(csvSplitBy);
          data.put(arr[1], arr[0]);
  		}
    } catch (IOException e) {
      System.out.print(e.getMessage());
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

    String cmd = "python ./src/main/java/ai/api/examples/scraper/historicalScrape.py "+symbol+" "+interval+" "+date+" "+date;
    String s = "";
    String csvSplitBy = "@";
    String[] data = null;

    try {
      Process p = Runtime.getRuntime().exec(cmd);
      try {
        p.waitFor();
      } catch (InterruptedException e) {
        System.out.println(e.getMessage());
      }

  		BufferedReader stdInput = new BufferedReader(new FileReader(csvFile));

      int i = 0;
  		while ((s = stdInput.readLine()) != null) {
        if (i == 0) {
          i++;
          continue;
        }
        data = s.split(csvSplitBy);
  		}
    } catch (IOException e) {
      System.out.print(e.getMessage());
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

    String cmd = "python ./src/main/java/ai/api/examples/scraper/sectorsScraper.py "+sectorNum;
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

      BufferedReader stdInput = new BufferedReader(new FileReader(csvFile));


      while ((s = stdInput.readLine()) != null) {
        data.add(s.split(csvSplitBy));
      }
    } catch (IOException e) {
      System.out.print(e.getMessage());
    }
    return data;
  }

}
