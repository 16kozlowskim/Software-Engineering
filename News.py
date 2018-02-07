import feedparser, sys

# Provide the full name of a stock, get list of headlines
# TODO: extend to return links, sources etc.

def getRss(stock):
	url = 'https://news.google.com/news/rss/search/section/q/'+stock+'/'+stock+'?hl=en&gl=GB&ned=us'
	d = feedparser.parse(url)
	return d

def getHeadlines(rss):
	entries = rss['entries']
	headlines = []
	for e in entries:
		headlines.append((e['title']).encode('utf-8'))
	return headlines

def main():
	args = sys.argv
	if(len(args) < 2):
		print("Needs at least 1 arg")
		exit(0)
	name = args[1]
	print(getHeadlines(getRss(name)))
	
if __name__ == "__main__":
	main()