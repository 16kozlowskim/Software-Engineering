# -*- coding: utf-8 -*-
import urllib2, sys, csv
from bs4 import BeautifulSoup

def get_ftse_companies():

    url = 'http://www.hl.co.uk/shares/stock-market-summary/ftse-100'

    data = []
    page = urllib2.urlopen(url)
    soup = BeautifulSoup(page, 'html.parser')

    table = soup.find('table', attrs={'class' : 'stockTable'})
    table_body = table.find('tbody')
    rows = table_body.find_all('tr')
    for row in rows:
        cols = row.find('td', attrs={'class' : 'name-col align-left'})
        link = cols.find('a')
        print link.get('href')

def main():

    get_ftse_companies()

if __name__ == '__main__':
    main()
