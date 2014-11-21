package com.github.windbender.chpcadscraper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.joda.time.DateMidnight;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChpCadScraper implements Runnable {
	
	Logger logger = LoggerFactory.getLogger(ChpCadScraper.class);

	private String regionCode;
	private boolean keepRunning = true;
	private int period;
	private EventFilter detailFilter;
	public ChpCadScraper(String regionCode, int period) {
		this.regionCode = regionCode;
		this.period = period;
	}
	private static SortedMap<EventKey,CHPEvent> store = Collections.synchronizedSortedMap(new TreeMap<EventKey,CHPEvent>());

	/**
	 * @param args
	 */
	public synchronized void scrape() {
		// Create a new instance of the Firefox driver
        // Notice that the remainder of the code relies on the interface, 
        // not the implementation.
        WebDriver driver = new FirefoxDriver();
        Set<EventKey> keep = new HashSet<EventKey>();
//        WebDriver driver = new HtmlUnitDriver();
        boolean eventFound = true;
        do {
        	eventFound = false;
	        driver.get("http://cad.chp.ca.gov");
	        try {
				Select select = new Select(driver.findElement(By.id("ddlComCenter")));
		        select.selectByValue(regionCode);
		        
		        WebElement element = driver.findElement(By.id("btnCCGo"));
		        element.submit();
		
		        WebElement baseTable = driver.findElement(By.id("gvIncidents"));
		        
		        List<WebElement> tableRows = baseTable.findElements(By.xpath(".//tr[position()>1]"));
		        for(WebElement we: tableRows) {
		        	WebElement chpid = we.findElement(By.xpath(".//td[2]"));
		        	String chipidStr = chpid.getText();
		        	EventKey ev = new EventKey(chipidStr,new DateMidnight());
		        	if(store .get(ev) == null) {
		        		CHPEvent event = new CHPEvent(ev);
			        		WebElement td = we.findElement(By.className("GVSelectColumn"));
							if(td != null) {
								WebElement link = td.findElement(By.xpath(".//a"));
								link.click();
								(new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
						            public Boolean apply(WebDriver d) {
						            	WebElement detail = d.findElement(By.id("Details"));
						            	if(detail != null) {
						            		return true;
						            	}
						                return false;
						            }
						        });
								WebElement detail = driver.findElement(By.id("Details"));
								WebElement ll = detail.findElement(By.id("lblLatLon"));
								WebElement loc = detail.findElement(By.id("lblLocation"));
								WebElement type = detail.findElement(By.id("lblType"));
								event.setLl(ll);
								event.setLoc(loc);
								event.setType(type);
								if(detailFilter.test(event)) {
									getDetailLines(event,detail);
								}
							}
							store.put(ev,event);
			        		keep.add(ev);
				        	eventFound = true;
			        	break;
		        	} else {
		        		keep.add(ev);
		        	}
		        }
			} catch (NoSuchElementException e) {
				logger.error("------- not found",e);
			} catch(Exception e) {
				logger.error("bad exception, no donut:",e);
				
			}
		} while(eventFound);
        
        //Close the browser
        driver.quit();
        
        // remove things which are not "keep"
        Set<EventKey> current = new HashSet<EventKey>(store.keySet());
        current.removeAll(keep);
        for(EventKey key: current) {
        	store.remove(key);
        }
   	}

	private void getDetailLines(CHPEvent event, WebElement detail) {
		WebElement detailsTable = detail.findElement(By.id("tblDetails"));
		if(detailsTable != null) {
			event.clearLines();
			List<WebElement> tableRows = detailsTable.findElements(By.xpath(".//tr[position()>1]"));
	        for(WebElement tr: tableRows) {
	        	try {
					WebElement timetd = tr.findElement(By.xpath(".//td[1]"));
					WebElement detailtd = tr.findElement(By.xpath(".//td[3]"));
					if(detailtd != null) {
						event.addLine(new CHPLine(timetd.getText(),detailtd.getText()));
					}
				} catch (NoSuchElementException e) {
					// ignore
				}
	        }
		}
	}

	public synchronized  void print() {
		logger.info("We found a total of "+store.size()+"  events");
        for(Entry<EventKey, CHPEvent> e: store.entrySet()) {
        	CHPEvent ce = e.getValue();
        	logger.info("Event: "+ce);
        }
		
	}

	public void run() {
		while(keepRunning ) {
			try {
				logger.info("now scraping...");
				scrape();
			} catch (Exception e1) {
				logger.error("can't scrape because ",e1);
			}
			logger.info("there are "+store.size()+" events currently ");
			//print();
			try {
				Thread.sleep(period);
			} catch (InterruptedException e) {
			}
		}
	}

	public synchronized List<CHPEvent> returnFiltered(EventFilter eventFilter) {
		List<CHPEvent> out = new ArrayList<CHPEvent>();
		for(Entry<EventKey, CHPEvent> e: store.entrySet()) {
			if(eventFilter == null) {
				out.add(e.getValue());
			} else if(eventFilter.test(e.getValue())) {
				out.add(e.getValue());
			}
        }
		return out;
	}

	public void setDetailFilter(EventFilter lef) {
		detailFilter = lef;
		
	}
	

}
