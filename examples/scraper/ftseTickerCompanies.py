# -*- coding: utf-8 -*-
import urllib2, sys, csv
from bs4 import BeautifulSoup

def get_ftse_companies():


    #pathToCSV = 'C:\\Users\\ojwoo\\Documents\\Warwick\\CS261\\Coursework\\dialogflow-java-client-master\\samples\\clients\\VirtualTradingAssistant\\src\\main\\java\\ai\\api\\examples\\fileStore\\file.csv'
    pathToCSV = '/Users/Michal/Downloads/dialogflow-java-client-master2/samples/clients/VirtualTradingAssistant/src/main/java/ai/api/examples/fileStore/file.csv'

    url = 'http://www.hl.co.uk/shares/stock-market-summary/ftse-100'

    data = []
    page = urllib2.urlopen(url)
    soup = BeautifulSoup(page, 'html.parser')

    table = soup.find('table', attrs={'class' : 'stockTable'})
    table_body = table.find('tbody')
    rows = table_body.find_all('tr')
    for row in rows:
        cols = row.find_all('td')
        cols = [ele.text.strip() for index, ele in enumerate(cols) if index < 2]
        data.append([ele for ele in cols if ele])

    with open(pathToCSV, 'w') as csvfile:
        wr = csv.writer(csvfile, delimiter='@', quotechar='#')
        wr.writerows(data)

    #wr = csv.writer(sys.stdout, delimiter='@', quotechar='#')

    #wr.writerows(data)

def main():

    get_ftse_companies()

if __name__ == '__main__':
    main()
