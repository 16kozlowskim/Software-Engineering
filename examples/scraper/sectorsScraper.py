# -*- coding: utf-8 -*-
import urllib2, cookielib, csv, sys
from bs4 import BeautifulSoup

# gets company data by sector in the following format
# [ticker, name]
# sector_num:
# Aerospace & Defense - 2710
# Alternative Energy - 0580
# Automobiles & Parts - 3350
# Banks - 8350
# Beverages - 3530
# Chemicals - 1350
# Construction & Materials - 2350
# Electricity - 7530
# Electronic & Electrical - 2730
# Equity Investment Instruments - 8980
# Financial Services - 8770
# Fixed Line Telecom - 6530
# Food & Drug Retailers - 5330
# Food Producers - 3570
# Forestry & Paper - 1730
# Gas, Water & Multiutilities - 7570
# General Industrials - 2720
# General Retailers - 5370
# Health Care Equipment & Services - 4530
# Household Goods & Home Construction - 3720
# Industrial Enginnering - 2750
# Industrial Metals & Mining - 1750
# Industrial Transportation - 2770
# Leisure Goods - 3740
# Life Insurance - 8570
# Media - 5550
# Mining - 1770
# Mobile Telecommunications - 6570
# Nonequity Investment Instruments - 8990
# Nonlife Insurance - 8530
# Oil & Gas Producers - 0530
# Oil Equipment & Services - 0570
# Personal Goods - 3760
# Pharmaceuticals & Biotechnology - 4570
# Real Estate Investment & Services - 8630
# Real Estate Investment Trusts - 8670
# Software & Computer Sertvices - 9530
# Support Services - 2790
# Technology Hardware & Equipment - 9570
# Tobacco - 3780
# Travel & Leisure - 5750
def get_sector_data(sector_num):

    #pathToCSV = 'C:\\Users\\ojwoo\\Documents\\Warwick\\CS261\\Coursework\\dialogflow-java-client-master\\samples\\clients\\VirtualTradingAssistant\\src\\main\\java\\ai\\api\\examples\\fileStore\\file.csv'
    #pathToCSV = '/Users/Michal/Downloads/dialogflow-java-client-master2/samples/clients/VirtualTradingAssistant/src/main/java/ai/api/examples/fileStore/file.csv'
    pathToCSV = '/Users/Michal/Desktop/apache-tomcat-8.5.28/bin/misc/file.csv'
    #pathToCSV = 'C:\\apache-tomcat-8.5.28\\bin\\misc\\file.csv'

    url_builder = []
    url_builder.append('http://www.londonstockexchange.com/exchange/prices-and-markets/stocks/indices/constituents-indices.html?index=UKX&industrySector=')
    url_builder.append(sector_num)
    url_builder.append('&page=1')

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

    soup = BeautifulSoup(content, 'html.parser')


    table = soup.find('table', attrs={'class' : 'table_dati'})

    try:
        table_body = table.find('tbody')
    except AttributeError:
        with open(pathToCSV, 'w') as csvfile:
            wr = csv.writer(csvfile, delimiter='@', quotechar='#')
            wr.writerow('')
            exit()

    rows = table_body.find_all('tr')

    data = []

    for row in rows:
        cols = row.find_all('td')
        cols = [ele.text.strip() for index, ele in enumerate(cols) if index < 10]
        data.append([ele for ele in cols if ele])

    with open(pathToCSV, 'w') as csvfile:
        wr = csv.writer(csvfile, delimiter='@', quotechar='#')
        wr.writerows(data)


def main():
    args = sys.argv
    get_sector_data(args[1])


if __name__ == '__main__':
    main()
