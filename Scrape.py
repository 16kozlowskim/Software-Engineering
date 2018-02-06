# Install this before running https://github.com/RomelTorres/alpha_vantage

from alpha_vantage.timeseries import TimeSeries
from pprint import pprint

ts = TimeSeries(key='45SBWT4C6DPHUPNR', output_format='pandas')

# Get json object with the intraday data and another with  the call's metadata
#data, meta_data = ts.get_daily(symbol='ABF', outputsize='compact')
# data, meta_data = ts.get_monthly(symbol='ABF')
# pprint(data)

def getRow(abbreviation):
    data, meta_data = ts.get_intraday(symbol=abbreviation,interval='1min', outputsize='compact')
    return data.iloc[-1]

def currentPrice(abbreviation):
    data, meta_data = ts.get_intraday(symbol=abbreviation,interval='1min', outputsize='compact')
    return data.iloc[-1]["4. close"]

def priceMinutesAgo(abbreviation, minutes):
    if(minutes <= 100):
        data, meta_data = ts.get_intraday(symbol=abbreviation,interval='1min', outputsize='compact')
        return data.iloc[-minutes-1]["4. close"]
    elif(minutes < 100 * 15):
        steps = int(round(minutes / 15.0))
        data, meta_data = ts.get_intraday(symbol=abbreviation,interval='15min', outputsize='compact')
        return data.iloc[-steps-1]["4. close"]
    else:
        raise ValueError("Must be less than 1500 minutes")

def priceDaysAgo(abbreviation, days):
    if(days <= 100):
        data, meta_data = ts.get_daily(symbol=abbreviation, outputsize='compact')
        return data.iloc[-days-1]["4. close"]
    else:
        raise ValueError("Must be less than 100 days")

def priceMonthsAgo(abbreviation, months):
    data, meta_data = ts.get_monthly(symbol=abbreviation)
    return data.iloc[-months-1]["4. close"]

def priceAtTime(abbreviation, timestamp):
    # TODO: infer relative time from current time and timestamp;
    pass

# TODO: extend for other parameters than price
# TODO: test more stocks and times
# TODO: figure out sector indicators

def main():
    print(currentPrice('ABF'))

if __name__ == "__main__":
    main()
