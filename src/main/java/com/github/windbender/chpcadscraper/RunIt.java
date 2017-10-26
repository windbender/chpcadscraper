package com.github.windbender.chpcadscraper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RunIt {

	static Logger logger = LoggerFactory.getLogger(RunIt.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CommandLine cmd = null;
		try {
			Options options = new Options();
			options.addOption("t", true, "to email");
			options.addOption("f", true, "from email");
			options.addOption("k", true, "kml file");
			options.addOption("r", true, "region - 4 letter code");
			options.addOption("u", true, "SMTP username");
			options.addOption("p", true, "SMTP password");
			options.addOption("h", true, "SMTP host");
			CommandLineParser parser = new BasicParser();
			cmd = parser.parse( options, args);
		} catch (ParseException e1) {
			logger.error("can't read CLI params ",e1);
			return;
		}
		String fs = cmd.getOptionValue("k");
		File f = null;
		if(fs == null) {
			f = new File("/Users/chris/Desktop/TestArea.kml");
		} else {
			f = new File(fs);
		}
		
		String r = cmd.getOptionValue("r");
		if(r == null) {
			r = "GGCC";
		}
		
		String from = cmd.getOptionValue("f");
		String to = cmd.getOptionValue("t");
		CompoundAlertListener alerters = new CompoundAlertListener();
		
		AlertListener print = new PrintAlertListener();
		alerters.add(print);
		String smtpHost = cmd.getOptionValue("h");
		String username = cmd.getOptionValue("u");
		String password = cmd.getOptionValue("p");

		if(smtpHost != null) {
			AlertListener email = new EmailAlertListener(from,to, smtpHost, username, password);
			alerters.add(email);
		}
		LocationFilter lef = new LocationFilter();
		lef.loadFromKML(f);
		
		int period = 1000*60;
		ChpCadScraper ccs = new ChpCadScraper(r,period);
		ccs.setDetailFilter(lef);
		Thread t = new Thread(ccs);
		t.setName("cad scraper");
		t.start();
		logger.info("scraper is started!");
		List<CHPEvent> state = new ArrayList<CHPEvent>();
		
		while(true) {
			try {
				Thread.sleep(1000 * 60);
			} catch (InterruptedException e) {
			}
			List<CHPEvent> l = ccs.returnFiltered(lef);
			doDiff(l,state, alerters);
			state = l;
		}

	}

	private static void doDiff(List<CHPEvent> newlist, List<CHPEvent> currentList, AlertListener alerter) {
		if(newlist == null) return;
		if(currentList == null) return;
		
		List<CHPEvent> addedList = new ArrayList<CHPEvent>();
		{
			Set<EventKey> current = new HashSet<EventKey>();
		
			Set<EventKey> newset = new HashSet<EventKey>();
			for(CHPEvent ce: currentList) {
				current.add(ce.ev);
			}
			for(CHPEvent ce: newlist) {
				newset.add(ce.ev);
			}
			// find new events
			newset.removeAll(current);
			for(CHPEvent ce: newlist) {
				if(newset.contains(ce.ev)) {
					addedList.add(ce);
				}
			}
		}
		// find removed events
		List<CHPEvent> removedList = new ArrayList<CHPEvent>();
		{
			Set<EventKey> current = new HashSet<EventKey>();
			Set<EventKey> newset = new HashSet<EventKey>();
			for(CHPEvent ce: currentList) {
				current.add(ce.ev);
			}
			for(CHPEvent ce: newlist) {
				newset.add(ce.ev);
			}
			// find new events
			current.removeAll(newset);
			for(CHPEvent ce: currentList) {
				if(current.contains(ce.ev)) {
					removedList.add(ce);
				}
			}
		}
		
		DateTime dt = new DateTime();
		if(addedList.size()> 0)
			alerter.alertAdded(addedList);
		if(removedList.size() > 0)
			alerter.alertGone(removedList);
		
	}

}
