import java.io.*;
import java.util.ArrayList;


public class DataBridge {

  public static ArrayList<String[]> getHistoricalData(String ticker, String interval, String startDate, String endDate) {
    String cmd = "python historicalScrape.py "+ticker+" "+interval+" "+startDate+" "+endDate;
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

      /*for (int j = 0; j < data.size(); j++) {
        for (int k = 0; k < 5; k++) {
          System.out.print(data.get(j)[k]+",");
        }
        System.out.println("");
      }*/
		}
    catch (IOException e) {
      System.out.println("exception happened - here's what I know: ");
		  e.printStackTrace();
			System.exit(-1);
		}

    return data;
	}

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

      /*for (int j = 0; j < data.size(); j++) {
        for (int k = 0; k < 5; k++) {
          System.out.print(data.get(j)[k]+",");
        }
        System.out.println("");
      }*/
    }
    catch (IOException e) {
      System.out.println("exception happened - here's what I know: ");
      e.printStackTrace();
      System.exit(-1);
    }

    return data;
  }

}
