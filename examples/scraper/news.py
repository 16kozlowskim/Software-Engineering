import feedparser, csv, sys, math
from pattern.en import ngrams, sentiment
from goose import Goose


def get_rss(search):
    url = 'https://news.google.com/news/rss/search/section/q/'+search+'/'+search+'?hl=en&gl=GB&ned=us'
    d = feedparser.parse(url)
    return d

def get_data(rss, num):

    #pathToCSV = '/Users/Michal/Downloads/dialogflow-java-client-master2/samples/clients/VirtualTradingAssistant/src/main/java/ai/api/examples/fileStore/file.csv'
    pathToCSV = 'C:\\Users\\ojwoo\\Documents\\Warwick\\CS261\\Coursework\\dialogflow-java-client-master\\samples\\clients\\VirtualTradingAssistant\\src\\main\\java\\ai\\api\\examples\\fileStore\\file.csv'

    with open(pathToCSV, 'w') as csvfile:
        wr = csv.writer(csvfile, delimiter='@', quotechar='#')
        index = 0
        for e in rss['entries']:
            if (index == int(num)):
                break

            wr.writerow([(e['title']).encode('utf-8')])
            wr.writerow([(e['link']).encode('utf-8')])

            try:
                g = Goose()
                article = g.extract(url=e['link'])

                cleaned_text = article.cleaned_text

                sent = sentiment(cleaned_text)


                if sent[0] < 0 :
                   sent = 50 - (sent[0]*-50)
                else :
                   sent = sent[0]*50 + 50


                wr.writerow([str(round(sent, 2))+'%'])
                #wr.writerow(['987'])

            except TypeError:
                wr.writerow(['Sentiment Unavailable'])

            index = index + 1



def main():
    get_data(get_rss(sys.argv[1]), sys.argv[2])

if __name__ == "__main__":
	main()
