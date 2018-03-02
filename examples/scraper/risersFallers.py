# -*- coding: utf-8 -*-
import urllib2, sys, csv
from bs4 import BeautifulSoup


# type - ('risers' or 'fallers')
def get_risers_fallers(type):
    pathToCSV = '/Users/Michal/Downloads/dialogflow-java-client-master2/samples/clients/VirtualTradingAssistant/src/main/java/ai/api/examples/fileStore/file.csv'
    #pathToCSV = 'C:\\Users\\ojwoo\\Documents\\Warwick\\CS261\\Coursework\\dialogflow-java-client-master\\samples\\clients\\VirtualTradingAssistant\\src\\main\\java\\ai\\api\\examples\\fileStore\\file.csv'
    url_builder = []
    url_builder.append('http://www.hl.co.uk/shares/stock-market-summary/ftse-100/')
    url_builder.append(type)
    url = ''.join(url_builder)

    hdr = {'User-Agent': 'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.64 Safari/537.11',
           'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8',
           'Accept-Charset': 'ISO-8859-1,utf-8;q=0.7,*;q=0.3',
           'Accept-Encoding': 'none',
           'Accept-Language': 'en-US,en;q=0.8',
           'Connection': 'keep-alive'}

    req = urllib2.Request(url, headers=hdr)

    try:
        page = urllib2.urlopen(req)
    except urllib2.HTTPError, e:
        print e.fp.read()

    content = page.read()
    data = []
    #page = urllib2.urlopen(url)
    soup = BeautifulSoup(content, 'html.parser')
    #print soup

    table = soup.find('table', attrs={'class' : 'stockTable'})
    table_body = table.find('tbody')
    rows = table_body.find_all('tr')
    for row in rows:
        cols = row.find_all('td')
        cols = [ele.text.strip() for index, ele in enumerate(cols) if index < 5]
        data.append([ele for ele in cols if ele])

    file_builder = []
    file_builder.append('../fileStore/')
    file_builder.append(type)
    file_builder.append('.csv')
    file_path = ''.join(file_builder)

    #with open(file_path, 'w') as csvfile:
    #    wr = csv.writer(csvfile, delimiter='@', quotechar='#')
    #    wr.writerows(data)
    with open(pathToCSV, 'w') as csvfile:
        wr = csv.writer(csvfile, delimiter='@', quotechar='#')
        wr.writerows(data)

def main():
    args = sys.argv

    get_risers_fallers(args[1])

if __name__ == '__main__':
    main()
