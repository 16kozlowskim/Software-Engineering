import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DataStore {
  /*
  private static Map<String, Integer> companies;
  private static Map<String, Integer> sectors;
  private static Map<String, Integer> attributes;
  */

  private static final Map<String, String> companyInfo = DataBridge.fillCompany();
  private static final Map<String, String> sectorNumbers = getSectorNumbers();

  public static HashMap<String, String> getSectorNumbers() {

    HashMap<String, String> sectorNum = new HashMap<String, String>();

    sectorNum.put("Aerospace & Defense", "2710");
    sectorNum.put("Alternative Energy", "0580");
    sectorNum.put("Automobiles & Parts", "3350");
    sectorNum.put("Banks", "8350");
    sectorNum.put("Beverages", "3530");
    sectorNum.put("Chemicals", "1350");
    sectorNum.put("Construction & Materials", "2350");
    sectorNum.put("Electricity", "7530");
    sectorNum.put("Electronic & Electrical", "2730");
    sectorNum.put("Equity Investment Instruments", "8980");
    sectorNum.put("Financial Services", "8770");
    sectorNum.put("Fixed Line Telecom", "6530");
    sectorNum.put("Food & Drug Retailers", "5330");
    sectorNum.put("Food Producers", "3570");
    sectorNum.put("Forestry & Paper", "1730");
    sectorNum.put("Gas, Water & Multiutilities", "7570");
    sectorNum.put("General Industrials", "2720");
    sectorNum.put("General Retailers", "5370");
    sectorNum.put("Health Care Equipment & Services", "4530");
    sectorNum.put("Household Goods & Home Construction", "3720");
    sectorNum.put("Industrial Enginnering", "2750");
    sectorNum.put("Industrial Metals & Mining", "1750");
    sectorNum.put("Industrial Transportation", "2770");
    sectorNum.put("Leisure Goods", "3740");
    sectorNum.put("Life Insurance", "8570");
    sectorNum.put("Media", "5550");
    sectorNum.put("Mining", "1770");
    sectorNum.put("Mobile Telecommunications", "6570");
    sectorNum.put("Nonequity Investment Instruments", "8990");
    sectorNum.put("Nonlife Insurance", "8530");
    sectorNum.put("Oil & Gas Producers", "0530");
    sectorNum.put("Oil Equipment & Services", "0570");
    sectorNum.put("Personal Goods", "3760");
    sectorNum.put("Pharmaceuticals & Biotechnology", "4570");
    sectorNum.put("Real Estate Investment & Services", "8630");
    sectorNum.put("Real Estate Investment Trusts", "8670");
    sectorNum.put("Software & Computer Sertvices", "9530");
    sectorNum.put("Support Services", "2790");
    sectorNum.put("Technology Hardware & Equipment", "9570");
    sectorNum.put("Tobacco", "3780");
    sectorNum.put("Travel & Leisure", "5750");
  }
  /*
  public static <K,V extends Comparable<? super V>> SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
    SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
      new Comparator<Map.Entry<K,V>>() {
        @Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
          int res = e2.getValue().compareTo(e1.getValue());
          return res != 0 ? res : 1;
        }
      }
    );
    sortedEntries.addAll(map.entrySet());
    return sortedEntries;
  }

  public static void incrementCompany(String company) {
    companies.replace(company, companies.get(company)+1);
  }

  public static void incrementSector(String sector) {
    companies.replace(sector, sectors.get(sector)+1);
  }

  public static void incrementAttribute(String attribute) {
    companies.replace(attribute, attributes.get(attribute)+1);
  }

  public static List<String> getFavouriteCompanies(int num) {
    List<String> favourites = new ArrayList<String>();
    SortedSet<Map.Entry<String, Integer>> set = entriesSortedByValues(companies);
    Iterator<Map.Entry<String, Integer>> itr = set.iterator();

    while (itr.hasNext() && favourites.size() < num) {
      favourites.add(itr.next().getKey());
    }

    return favourites;
  }

  public static List<String> getFavouriteSectors(int num) {
    List<String> favourites = new ArrayList<String>();
    SortedSet<Map.Entry<String, Integer>> set = entriesSortedByValues(sectors);
    Iterator<Map.Entry<String, Integer>> itr = set.iterator();

    while (itr.hasNext() && favourites.size() < num) {
      favourites.add(itr.next().getKey());
    }

    return favourites;
  }

  public static List<String> getFavouriteAttributes(int num) {
    List<String> favourites = new ArrayList<String>();
    SortedSet<Map.Entry<String, Integer>> set = entriesSortedByValues(attributes);
    Iterator<Map.Entry<String, Integer>> itr = set.iterator();

    while (itr.hasNext() && favourites.size() < num) {
      favourites.add(itr.next().getKey());
    }

    return favourites;
  }
  */

  public static void initDB() {
    Connection conn = getConnection();
    Statement s = conn.createStatement();

    ResultSet rs = s.executeQuery("select count(*) from company");

    if (rs.next() == 0) {
      conn.close();
      resetDB();
    }
  }

  public static void resetDB() {
    Connection conn = getConnection();
    Statement s = conn.createStatement();

    s.executeUpdate("drop table company")
    s.executeUpdate("drop table attribute")
    s.executeUpdate("drop table sector")
    s.executeUpdate("create table company (name string, count integer)");
    s.executeUpdate("create table attribute (name string, count integer)");
    s.executeUpdate("create table sector (name string, count integer)");

    companyInfo.forEach((k,v) -> s.executeUpdate("insert into company values('"+k+"', 0)"));
    sectorNumbers.forEach((k,v) -> s.executeUpdate("insert into sector values('"+k+"', 0)"));

  }

  public static Connection getConnection() {
    try {
      Class.forName("org.sqlite.JDBC");
    } catch (ClassNotFoundException e) {
      System.out.println("Database was not found")
    }

    Connection connection = null;
    try {
      // create a database connection
      connection = DriverManager.getConnection("jdbc:sqlite:ai.db");
      return connection;
    }
    catch(SQLException e) {
      // if the error message is "out of memory",
      // it probably means no database file is found
      System.err.println(e.getMessage());
    }
  }
}
