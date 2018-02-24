import java.io.*;
import java.util.*;


public class DataBridge {

  /*public static void main(String[] args) throws IOException {

    String[] companyData = DataBridge.getCompanyData("iii");

    for (int i = 0; i < companyData.length; i++) {
      System.out.println(companyData[i]);
    }

    HashMap<String, String[]> risersFallers = getRisersFallers(false);

    risersFallers.forEach((k,v) -> System.out.println("key: "+k+" value: "+v[0]+" "+v[1]+" "+v[2]+" "+v[3]));

    HashMap<String, String> companyTicker = fillCompany();

    companyTicker.forEach((k,v) -> System.out.println("key: "+k+" value: "+v));

    String[] historical = getHistoricalData("lse.uk", "m", "20161111");


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


  }*/

  public static String[] getCompanyData(String ticker) throws IOException {
    String cmd = "python googScraper.py "+ticker;
    String s = "";
    String csvSplitBy = "@";
    String[] data = new String[15];

    Process p = Runtime.getRuntime().exec(cmd);

		BufferedReader stdInput = new BufferedReader(new
		InputStreamReader(p.getInputStream()));

    int i = 0;
		while ((s = stdInput.readLine()) != null) {
      data[i] = s;
      i++;
		}

    return data;
  }

  public static HashMap<String, String[]> getRisersFallers(Boolean getRisers) throws IOException {
    String cmd;
    if (getRisers)
      cmd = "python risersFallers.py risers";
    else
      cmd = "python risersFallers.py fallers";

    String s = "";
    String csvSplitBy = "@";
    HashMap<String, String[]> data = new HashMap<String, String[]>();
    String[] arr;

    Process p = Runtime.getRuntime().exec(cmd);

		BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));


		while ((s = stdInput.readLine()) != null) {
      arr = s.split(csvSplitBy);
      String[] temp = {arr[0], arr[2], arr[3], arr[4]};
      data.put(arr[1], temp);
		}

    return data;
  }

  public static HashMap<String, String> fillCompany() throws IOException {
    String cmd = "python ftseTickerCompanies.py";
    String s = "";
    String csvSplitBy = "@";
    HashMap<String, String> data = new HashMap<String, String>();
    String[] arr;


    Process p = Runtime.getRuntime().exec(cmd);

		BufferedReader stdInput = new BufferedReader(new
		InputStreamReader(p.getInputStream()));


		while ((s = stdInput.readLine()) != null) {
      arr = s.split(csvSplitBy);
      data.put(arr[1], arr[0]);
		}

    return data;
  }

  public static String[] getHistoricalData(String ticker, String interval, String date) throws IOException {
    String cmd = "python historicalScrape.py "+ticker+" "+interval+" "+date+" "+date;
    String s = "";
    String csvSplitBy = "@";
    String[] data = null;


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

    return data;
	}

  public static ArrayList<String[]> getSectorData(String sectorNum) throws IOException {

    String cmd = "python sectorsScraper.py "+sectorNum;
    String s = "";
    String csvSplitBy = "@";
    ArrayList<String[]> data = new ArrayList<String[]>();


    Process p = Runtime.getRuntime().exec(cmd);

    BufferedReader stdInput = new BufferedReader(new
    InputStreamReader(p.getInputStream()));


    while ((s = stdInput.readLine()) != null) {
      data.add(s.split(csvSplitBy));
    }

    return data;
  }

}
