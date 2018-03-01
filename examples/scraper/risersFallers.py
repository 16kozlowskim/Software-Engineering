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

    data = []
    page = urllib2.urlopen(url)
    soup = BeautifulSoup(page, 'html.parser')

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
