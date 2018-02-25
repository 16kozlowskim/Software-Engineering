import urllib2, csv, sys
from bs4 import BeautifulSoup

def get_company_data(ticker):
    url = 'https://finance.google.com/finance?q=lon:'
    url += ticker

    page = urllib2.urlopen(url)
    soup = BeautifulSoup(page, 'html.parser')

    div = soup.find('div', attrs={'id' : 'price-panel'})

    print div.find('span', attrs={'class' : 'pr'}).text.strip()

    print div.find('div', attrs={'class' : 'id-price-change nwp'}).text.strip()

    div = soup.find('div', attrs={'class' : 'snap-panel'})

    tables = div.find_all('table')

    for table in tables:
        rows = table.find_all('tr')
        for row in rows:
            print row.find('td', attrs={'class' : 'val'}).text.strip()


def main():
    get_company_data(sys.argv[1])

if __name__ == '__main__':
    main()
