import java.util.*;

public class DataStore {

  private Map<String, Integer> companies;
  private Map<String, Integer> sectors;
  private Map<String, Integer> attributes;

  private Map<String, String> companyInfo;
  private Map<String, String> sectorNum;

  public DataStore() {
    // AI data
    companies = new TreeMap<String, Integer>();
    sectors = new TreeMap<String, Integer>();
    attributes = new TreeMap<String, Integer>();

    // General data
    sectorNum = new HashMap<String, String>();
  }

  public void fillCompanyInfo() {
    companyInfo = DataBridge.fillCompany();
  }

  public void fillSectorNum() {
    sectorNums.put("Aerospace & Defense", "2710");
    sectorNums.put("Alternative Energy", "0580");
    sectorNums.put("Automobiles & Parts", "3350");
    sectorNums.put("Banks", "8350");
    sectorNums.put("Beverages", "3530");
    sectorNums.put("Chemicals", "1350");
    sectorNums.put("Construction & Materials", "2350");
    sectorNums.put("Electricity", "7530");
    sectorNums.put("Electronic & Electrical", "2730");
    sectorNums.put("Equity Investment Instruments", "8980");
    sectorNums.put("Financial Services", "8770");
    sectorNums.put("Fixed Line Telecom", "6530");
    sectorNums.put("Food & Drug Retailers", "5330");
    sectorNums.put("Food Producers", "3570");
    sectorNums.put("Forestry & Paper", "1730");
    sectorNums.put("Gas, Water & Multiutilities", "7570");
    sectorNums.put("General Industrials", "2720");
    sectorNums.put("General Retailers", "5370");
    sectorNums.put("Health Care Equipment & Services", "4530");
    sectorNums.put("Household Goods & Home Construction", "3720");
    sectorNums.put("Industrial Enginnering", "2750");
    sectorNums.put("Industrial Metals & Mining", "1750");
    sectorNums.put("Industrial Transportation", "2770");
    sectorNums.put("Leisure Goods", "3740");
    sectorNums.put("Life Insurance", "8570");
    sectorNums.put("Media", "5550");
    sectorNums.put("Mining", "1770");
    sectorNums.put("Mobile Telecommunications", "6570");
    sectorNums.put("Nonequity Investment Instruments", "8990");
    sectorNums.put("Nonlife Insurance", "8530");
    sectorNums.put("Oil & Gas Producers", "0530");
    sectorNums.put("Oil Equipment & Services", "0570");
    sectorNums.put("Personal Goods", "3760");
    sectorNums.put("Pharmaceuticals & Biotechnology", "4570");
    sectorNums.put("Real Estate Investment & Services", "8630");
    sectorNums.put("Real Estate Investment Trusts", "8670");
    sectorNums.put("Software & Computer Sertvices", "9530");
    sectorNums.put("Support Services", "2790");
    sectorNums.put("Technology Hardware & Equipment", "9570");
    sectorNums.put("Tobacco", "3780");
    sectorNums.put("Travel & Leisure", "5750");
  }

  static <K,V extends Comparable<? super V>> SortedSet<Map.Entry<K,V>>
  entriesSortedByValues(Map<K,V> map) {
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

  public void incrementCompany(String company) {
    companies.replace(company, companies.get(company)+1);
  }

  public void incrementSector(String sector) {
    companies.replace(sector, sectors.get(sector)+1);
  }

  public void incrementAttribute(String attribute) {
    companies.replace(attribute, attributes.get(attribute)+1);
  }

  public List<String> getFavouriteCompanies(int num) {
    List<String> favourites = new ArrayList<String>();
    SortedSet<Map.Entry<String, Integer>> set = entriesSortedByValues(companies);
    Iterator<Map.Entry<String, Integer>> itr = set.iterator();

    while (itr.hasNext() && favourites.size() < num) {
      favourites.add(itr.next().getKey());
    }

    return favourites;
  }

  public List<String> getFavouriteSectors(int num) {
    List<String> favourites = new ArrayList<String>();
    SortedSet<Map.Entry<String, Integer>> set = entriesSortedByValues(sectors);
    Iterator<Map.Entry<String, Integer>> itr = set.iterator();

    while (itr.hasNext() && favourites.size() < num) {
      favourites.add(itr.next().getKey());
    }

    return favourites;
  }

  public List<String> getFavouriteAttributes(int num) {
    List<String> favourites = new ArrayList<String>();
    SortedSet<Map.Entry<String, Integer>> set = entriesSortedByValues(attributes);
    Iterator<Map.Entry<String, Integer>> itr = set.iterator();

    while (itr.hasNext() && favourites.size() < num) {
      favourites.add(itr.next().getKey());
    }

    return favourites;
  }

  /*public static void main(String[] args) {
    DataStore data = new DataStore();
    data.companies.put("ape", 1);
    data.companies.put("cow", 3);
    data.companies.put("pig", 1);
    data.companies.put("frog", 2);

    List<String> a = data.getFavouriteCompanies(4);

    for (int i = 0; i < a.size(); i++) {
      System.out.println(a.get(i));
    }

    data.incrementCompany("frog");
    data.incrementCompany("frog");
    data.incrementCompany("frog");

    a = data.getFavouriteCompanies(4);

    for (int i = 0; i < a.size(); i++) {
      System.out.println(a.get(i));
    }

  }*/

}
