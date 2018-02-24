# -*- coding: utf-8 -*-
import urllib2, sys, csv
from bs4 import BeautifulSoup

def get_ftse_companies(link):

    url_builder = []
    url_builder.append('http://www.hl.co.uk/')
    url_builder.append(link)
    url = ''.join(url_builder)

    data = []
    page = urllib2.urlopen(url)
    soup = BeautifulSoup(page, 'html.parser')

    div = soup.find('div', attrs={'id' : 'security-detail'})
    div_body = soup.find_all('div', attrs={'class' : 'columns large-3 medium-4 small-6'})
    for index, field in enumerate(div_body):
        if index == 8:
            continue
        element = field.text.strip()
        print ''.join(element)


def main():
    args = sys.argv

    get_ftse_companies(args[1])

if __name__ == '__main__':
    main()
