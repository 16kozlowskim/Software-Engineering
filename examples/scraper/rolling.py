import re, csv, sys, urllib2

def get_rolling_average(symbol):

    url = 'https://www.google.com/finance/getprices?q='+symbol+'&x=LON&i=50&p=12d&f=d%2Cc%2Ch%2Cl%2Co%2Cv'

    #pathToCSV = '/Users/Michal/Downloads/dialogflow-java-client-master2/samples/clients/VirtualTradingAssistant/src/main/java/ai/api/examples/fileStore/file.csv'
    #pathToCSV = 'C:\\Users\\ojwoo\\Documents\\Warwick\\CS261\\Coursework\\dialogflow-java-client-master\\samples\\clients\\VirtualTradingAssistant\\src\\main\\java\\ai\\api\\examples\\fileStore\\file.csv'
    pathToCSV = '/Users/Michal/Desktop/apache-tomcat-8.5.28/bin/misc/file.csv'
    #pathToCSV = 'C:\\apache-tomcat-8.5.28\\bin\\misc\\file.csv'

    response = urllib2.urlopen(url)

    cr = csv.reader(response)
    with open(pathToCSV, 'w') as csvfile:
        wr = csv.writer(csvfile, delimiter='@', quotechar='#')
        wr.writerows(cr)

def main():
    args = sys.argv

    get_rolling_average(args[1])

if __name__ == '__main__':
    main()
