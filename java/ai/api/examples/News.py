import feedparser, sys

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

def getSummary(rss):
	entries = rss['entries']
	data = []
	for e in entries:
		data.append([(e['title']).encode('utf-8'), (e['link']).encode('utf-8')])
	return data

def main():
	args = sys.argv
	if(len(args) < 3):
		print("Needs at least 2 args")
		exit(0)
	cmnd = args[1].lower()
	name = args[2]
	if(cmnd == "getheadlines"):
		print(getHeadlines(getRss(name)))
	elif(cmnd == "getsummary"):
		print(getSummary(getRss(name)))
	
if __name__ == "__main__":
	main()