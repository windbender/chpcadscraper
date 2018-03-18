package com.github.windbender.chpcadscraper;

import java.util.*;

import com.github.windbender.chpcadscraper.chpdata.*;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class ChpCadScraper  extends DefaultHandler implements Runnable {


	private final AlertListener alerters;
	private DateTime last;

	public static void main(String[] args) {
		ChpCadScraper c  = new ChpCadScraper("xxx",1234, null);
		c.scrape();

	}

	Logger logger = LoggerFactory.getLogger(ChpCadScraper.class);

	int scrapeCount = 0;
	private String regionCode;
	private boolean keepRunning = true;
	private int period;
	private EventFilter detailFilter;
	public ChpCadScraper(String regionCode, int period, AlertListener alerters) {
		this.regionCode = regionCode;
		this.period = period;
		this.alerters = alerters;
	}
	private Set<CHPEvent> store = new HashSet<CHPEvent>();

	private Set<String> eventTypesKnown = new HashSet<String>();

	public synchronized void scrape() {
		try {
//			URL cadDataUrl = new URL("https://media.chp.ca.gov/sa_xml/sa.xml");
			String cadDataUrl = new String("https://media.chp.ca.gov/sa_xml/sa.xml");
//			File f = new File("/Users/chris/workspace/chpcadscraper/sa.xml");

			//Create a "parser factory" for creating SAX parsers
			SAXParserFactory spfac = SAXParserFactory.newInstance();

			//Now use the parser factory to create a SAXParser object
			SAXParser sp = spfac.newSAXParser();

			current = null;
			//Finally, tell the parser to parse the input and notify the handler
			sp.parse(cadDataUrl, this);

			State state = (State) current;

			store = processToStore(state);
			scrapeCount++;
		} catch(Exception e) {
			emailAdmin("parse cad stuff because "+e);
			logger.error("can't parse cad stuff because ",e);
		}
   	}

	private Set<CHPEvent> processToStore(State state) {
		Set<CHPEvent> set = new HashSet<CHPEvent>();
		for(Center c: state.getCenters()) {
			for(Dispatch d : c.getDispatches()) {
				for(Log l : d.getLogs()) {
					CHPEvent ce = convertLog(l);
					set.add(ce);
				}
			}
		}
		return set;
	}

	private CHPEvent convertLog(Log l) {
		EventKey ek = new EventKey(l.getID());
		CHPEvent e = new CHPEvent(ek);
		e.setLocation(l.getArea());
		e.setType(l.getLogType());
		e.setLocation(l.getLocation());
		e.setLocationDesc(l.getLocationDesc());
		String[] ll = l.getLATLON().split("\\:");
		String latString = ll[0];
		String lonString = ll[1];
		Double lat = Double.parseDouble("0."+latString.substring(1));
		Double lon = Double.parseDouble("-0."+lonString.substring(0,lonString.length()-1));
		lat = lat * 100;
		lon = lon * 1000;
		e.setLat(lat);
		e.setLon(lon);
		for(Detail ld : l.getLogDetails()) {
			CHPLine cl = new CHPLine(ld.getTime(),ld.getTxt());
			e.getLines().add(cl);
		}
		return e;
	}

	Able current;
	Able parent;
	Stack<Able> stack = new Stack<Able>();

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		if(current != null) {
			stack.push(current);
			parent = current;
		}
		boolean doAttributes = true;
		if(qName.equals("State")) {
			current = new State();
		} else if(qName.equals("Center")) {
			State state = (State) parent;
			Center center = new Center();
			state.add(center);
			current= center;
		} else if(qName.equals("Dispatch")) {
			Center center = (Center) parent;
			Dispatch dispatch = new Dispatch();
			center.add(dispatch);
			current= dispatch;
		} else if(qName.equals("Log")) {
			Dispatch dispatch = (Dispatch) parent;
			Log log = new Log();
			dispatch.add(log);
			current= log;
		} else {
			// these are all "inside" log no ?
			// so pop the stack back where it was

			Log log = (Log) current;
			log.startElement(qName, attributes);
			doAttributes = false;
		}

		if(doAttributes ) {
			current.doAttributes(attributes);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);
		if(current instanceof Log) {
			Log log = (Log) current;
			log.endElement(qName);
			eventTypesKnown.add(log.getLogType());
		}
		if(stack.size() > 0) {
			current = stack.pop();
			if(stack.size() >0 ) {
				parent = stack.peek();
			} else {
				parent = null;
			}
		} else {
			// don't set current to null
		}

	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);
		current.addChars(new String(ch,start,length));
	}



	public void run() {
		// get an email on start
		last = new DateTime().minusDays(1);
		emailAdmin("yes, you restarted me!");
		logger.info("scraper thread running");
		while(keepRunning ) {
			try {
				scrape();
				try {
					Thread.sleep(period);
				} catch (InterruptedException e) {
				}
				DateTime now = new DateTime();
				if(now.minusDays(1).isBefore(last)) {
					emailAdmin("we have scraped "+scrapeCount);
					scrapeCount=0;
					last = now;
				}
			} catch (Exception e1) {
				emailAdmin("can't scrape because "+e1);
				logger.error("can't scrape because ",e1);
			}
		}
		logger.info("scraper thread STOPPED");
	}

	private void emailAdmin(String s) {
		alerters.emailAdmin(s);
	}

	public synchronized List<CHPEvent> returnFiltered(EventFilter eventFilter) {
		List<CHPEvent> out = new ArrayList<CHPEvent>();
		for(CHPEvent e: store) {
			if(eventFilter == null) {
				out.add(e);
			} else if(eventFilter.test(e)) {
				out.add(e);
			}
        }
		return out;
	}

	public void setDetailFilter(EventFilter lef) {
		detailFilter = lef;
		
	}

	public void stop() {
		keepRunning = false;

	}
}
