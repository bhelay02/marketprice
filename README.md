
This application uses Java17

**To run this application from command line**

mvn spring-boot:run

It can be run from IDE ( like IntelliJ ) by running MarketpriceApplication class

**Notes :** 

* MarketpriceListener : This class has a onMessage method with assumption it will be called in the client stream to process messages

* MarketpriceController : Has REST API which returns latest price for a particular instrument

* This application uses in memory H2 DB for storing the data. Which is configurable to be replaced by actual DB using properties configuration

* BidCommission and AskCommission amounts are configurable via application.properties file.

* REST API to get latest price for an instrument GET /api/v1/prices with request param instrument. e.g. value like EUR/USD

* On start application inserts few records in DB as defined in MarketpriceListener

* This application uses Spring Reactive framework which supports reactive programming and non-blocking I/O.


**Possibilities :**

This application can be further extended to have caching support which will improve timings.

Using libraries like ChronicleMap which will be much faster , if storing data in files is considered viable options.