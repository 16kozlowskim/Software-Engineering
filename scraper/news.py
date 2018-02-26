import feedparser, csv, sys
from pyteaser import SummarizeUrl


def get_rss(ticker):
    url = 'https://news.google.com/news/rss/search/section/q/lon:'+ticker+'/lon:'+ticker+'?hl=en&gl=GB&ned=us'
    d = feedparser.parse(url)
    return d

def get_data(rss):
    pathToCSV = '../fileStore/file.csv'
    data= []
    with open(pathToCSV, 'w') as csvfile:
        wr = csv.writer(csvfile, delimiter='@', quotechar='#')
        for e in rss['entries']:
            wr.writerow([(e['title']).encode('utf-8')])
            wr.writerow([(e['link']).encode('utf-8')])

            summary = []
            for elem in SummarizeUrl(e['link'].encode('utf-8')):
                summary.append(elem)
            wr.writerow([' '.join(summary).encode('utf-8').strip().replace('\n', '')])
            break

def main():
    get_data(get_rss(sys.argv[1]))

if __name__ == "__main__":
	main()
