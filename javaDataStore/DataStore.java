import java.util.*;

public class DataStore {

  private Map<String, Integer> companies;
  private Map<String, Integer> sectors;
  private Map<String, Integer> attributes;

  public DataStore() {
    // AI data
    companies = new TreeMap<String, Integer>();
    sectors = new TreeMap<String, Integer>();
    attributes = new TreeMap<String, Integer>();


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

  public static void main(String[] args) {
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

  }

}
