# install pyteaser before running
from pyteaser import SummarizeUrl
import sys

def get_summary(url):
    summaries = SummarizeUrl(url)

    summary = []
    for elem in summaries:
        summary.append(elem)
    print ' '.join(summary)

def main():
    get_summary(sys.argv[1])
    
if __name__ == '__main__':
    main()
