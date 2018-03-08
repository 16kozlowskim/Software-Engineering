# install BeautifulSoup4 before running
#
# prints out historical data in csv format:
#
# [date, open, high, low, close, volume]
#
import re, csv, sys, urllib2
from bs4 import BeautifulSoup

# If start date and end date is the same only one value will be returned and
# if not the multiple values which can be used to make calculations
#
# ticker (company symbol)
# interval (d (daily), m (monthly), q (quarterly), y (yearly))
# start_date (YYYYMMDD)
# end_date (YYYYMMDD)
def get_historical_data(ticker, interval, start_date, end_date):

    #pathToCSV = '/Users/Michal/Downloads/dialogflow-java-client-master2/samples/clients/VirtualTradingAssistant/src/main/java/ai/api/examples/fileStore/file.csv'
    #pathToCSV = 'C:\\Users\\ojwoo\\Documents\\Warwick\\CS261\\Coursework\\dialogflow-java-client-master\\samples\\clients\\VirtualTradingAssistant\\src\\main\\java\\ai\\api\\examples\\fileStore\\file.csv'
    #pathToCSV = '/Users/Michal/Desktop/apache-tomcat-8.5.28/bin/misc/file.csv'
    pathToCSV = 'C:\\apache-tomcat-8.5.28\\bin\\misc\\file.csv'

    url_builder = []
    url_builder.append('https://stooq.com/q/d/?s=')
    url_builder.append(ticker)
    url_builder.append('&c=0&d1=')
    url_builder.append(start_date)
    url_builder.append('&d2=')
    url_builder.append(end_date)
    url_builder.append('&i=')
    url_builder.append(interval)

    url = ''.join(url_builder)

    page = urllib2.urlopen(url)

    soup = BeautifulSoup(page, 'html.parser')

    link = soup.findAll('a', href=re.compile('^q/d/l/'))

    link = re.search('"(.*)"', str(link))

    try:
        link = link.group(1)
    except AttributeError:
        with open(pathToCSV, 'w') as csvfile:
            wr = csv.writer(csvfile, delimiter='@', quotechar='#')
            wr.writerow('')
            exit()

    link = link.replace('amp;', '')

    arr = []

    arr.append('https://stooq.com/')

    arr.append(link)

    link = ''.join(arr)

    response = urllib2.urlopen(link)

    cr = csv.reader(response)
    with open(pathToCSV, 'w') as csvfile:
        wr = csv.writer(csvfile, delimiter='@', quotechar='#')
        wr.writerows(cr)

def main():
    args = sys.argv

    get_historical_data(args[1], args[2], args[3], args[4])

if __name__ == '__main__':
    main()
