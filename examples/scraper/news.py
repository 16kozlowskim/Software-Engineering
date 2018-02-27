import feedparser, csv, sys
from pyteaser import SummarizeUrl


def get_rss(search):
    url = 'https://news.google.com/news/rss/search/section/q/'+search+'/'+search+'?hl=en&gl=GB&ned=us'
    d = feedparser.parse(url)
    return d

def get_data(rss, num):
    #pathToCSV = '../fileStore/file.csv'
    pathToCSV = './src/main/java/ai/api/examples/fileStore/file.csv'
    data= []
    with open(pathToCSV, 'w') as csvfile:
        wr = csv.writer(csvfile, delimiter='@', quotechar='#')
        index = 0
        for e in rss['entries']:
            if (index == int(num)):
                break

            wr.writerow([(e['title']).encode('utf-8')])
            wr.writerow([(e['link']).encode('utf-8')])

            summary = []
            try:
                for elem in SummarizeUrl(e['link'].encode('utf-8')):
                    summary.append(elem)
                wr.writerow([' '.join(summary).encode('utf-8').strip().replace('\n', '')])
            except TypeError:
                wr.writerow(['Summary Unavailable'])

            index = index + 1



def main():
    get_data(get_rss(sys.argv[1]), sys.argv[2])

if __name__ == "__main__":
	main()
