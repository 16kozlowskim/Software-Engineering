# install pyteaser before running 
from pyteaser import SummarizeUrl
# url of article that is going to be summarized
url = 'https://uk.finance.yahoo.com/news/could-novavax-inc-millionaire-maker-110400596.html'
summaries = SummarizeUrl(url)
# prints out the summary
print summaries
