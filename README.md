chpcadscraper
=============

This java code utilizes Selenium webdriver to scrape the California CHP dispatchers web page located at http://cad.chp.ca.gov  

The data is filtered based on CHP dispatch center ( there are about 20 ), and then can be filtered via a KML file outline. Details are scraped and pushed into a listener pattern.  Right now there are only two listeners print and email.  This code will email you when it finds a new event.

To run.  clone the project and then:

mvn clean package

to produce:  cadchpscraper-0.0.1-SNAPSHOT-jar-with-dependencies.jar

which is run from the command line like:

java -jar cadchpscraper-0.0.1-SNAPSHOT-jar-with-dependencies.jar -h <smtphost> -p <smtppass> -u <smtpuser> -t <toemail> -f <fromemail> -k FilterArea.kml


