# -*- coding: utf-8 -*-
# install BeautifulSoup4 before running
#
# prints out an array of data for each company on FTSE 100 in the following format:
#
# [company symbol, company name, spot price (15 min delay),
# change in price (absolute), change in price (%),
# change in price after 1 year (absolute), change in price after 1 year (%),
# volume, time]

import urllib2, csv, sys
from bs4 import BeautifulSoup


page_1 = 'https://stooq.pl/q/i/?s=^ukx'
page_2 = 'https://stooq.pl/q/i/?s=^ukx&l=2'
page_list = [page_1, page_2]
data = []
for elem in page_list:
    page = urllib2.urlopen(elem)
    soup = BeautifulSoup(page, 'html.parser')

    table = soup.find('table', attrs={'class' : 'fth1'})
    table_body = table.find('tbody')

    rows = table_body.find_all('tr')
    for row in rows:
        cols = row.find_all('td')
        cols = [ele.text.strip() for ele in cols]
        data.append([ele for ele in cols if ele])

#with open('../fileStore/companyData2.csv', 'w') as csvfile:
#    wr = csv.writer(csvfile, delimiter='@', quotechar='#')
#    wr.writerows(data)

wr = csv.writer(sys.stdout, delimiter='@', quotechar='#')

wr.writerows(data)
