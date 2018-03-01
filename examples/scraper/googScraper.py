# -*- coding: utf-8 -*-
import urllib2, csv, sys
from bs4 import BeautifulSoup

def get_company_data(ticker):
    url = 'https://finance.google.com/finance?q=lon:'
    url += ticker
    pathToCSV = '/Users/Michal/Downloads/dialogflow-java-client-master2/samples/clients/VirtualTradingAssistant/src/main/java/ai/api/examples/fileStore/file.csv'
    #pathToCSV = 'C:\\Users\\ojwoo\\Documents\\Warwick\\CS261\\Coursework\\dialogflow-java-client-master\\samples\\clients\\VirtualTradingAssistant\\src\\main\\java\\ai\\api\\examples\\fileStore\\file.csv'
    page = urllib2.urlopen(url)
    soup = BeautifulSoup(page, 'html.parser')

    div = soup.find('div', attrs={'id' : 'price-panel'})

    with open(pathToCSV, 'w') as csvfile:
        wr = csv.writer(csvfile, delimiter='@', quotechar='#')
        wr.writerow([div.find('span', attrs={'class' : 'pr'}).text.strip().encode('utf-8')])
        for e in div.find('div', attrs={'class' : 'id-price-change nwp'}).text.strip().split('\n'):
            wr.writerow([e])

        div = soup.find('div', attrs={'class' : 'snap-panel'})

        tables = div.find_all('table')

        for table in tables:
            rows = table.find_all('tr')
            for row in rows:
                wr.writerow([row.find('td', attrs={'class' : 'val'}).text.strip().encode('utf-8')])


def main():
    get_company_data(sys.argv[1])

if __name__ == '__main__':
    main()
