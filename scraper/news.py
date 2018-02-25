import feedparser, csv, sys
from pyteaser import SummarizeUrl

def get_summary(url):
    summary = []
    for elem in SummarizeUrl(url):
        summary.append(elem)
    print ' '.join(summary).encode('utf-8').strip()

def get_rss(ticker):
    url = 'https://news.google.com/news/rss/search/section/q/lon:'+ticker+'/lon:'+ticker+'?hl=en&gl=GB&ned=us'
    d = feedparser.parse(url)
    return d

def get_data(rss):
    data= []
    for e in rss['entries']:
        print (e['title']).encode('utf-8')
        print (e['link']).encode('utf-8')
        get_summary(e['link'])
        break

def main():
    get_data(get_rss(sys.argv[1]))

if __name__ == "__main__":
	main()
