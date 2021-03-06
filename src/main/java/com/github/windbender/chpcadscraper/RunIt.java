package com.github.windbender.chpcadscraper;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.geojson.Feature;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RunIt {

	static Logger logger = LoggerFactory.getLogger(RunIt.class);
	static boolean keeprunning = true;
	static Thread t;
	private static Thread curt;
	private static int lastNwsHash;
	private static long lastRun;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AlertListener email = null;
		try {
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
				cmd = parser.parse(options, args);
			} catch (ParseException e1) {
				logger.error("can't read CLI params ", e1);
				return;
			}
			String fs = cmd.getOptionValue("k");
			File f = null;
			if (fs == null) {
				f = new File("/Users/chris/Desktop/TestArea.kml");
			} else {
				f = new File(fs);
			}

			String r = cmd.getOptionValue("r");
			if (r == null) {
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

			if (smtpHost != null) {
				email = new EmailAlertListener(from, to, smtpHost, username, password);
				alerters.add(email);
			}
			LocationFilter lef = new LocationFilter();
			lef.loadFromKML(f);

			// Start the CAD Scraper
			int period = 1000 * 60;
			final ChpCadScraper ccs = new ChpCadScraper(r, period, alerters);
			ccs.setDetailFilter(lef);
			t = new Thread(ccs);
			t.setName("cad scraper");
			t.start();
			logger.info("emails will be " + to);
			logger.info("file will be " + fs);
			logger.info("scraper is started!");
			List<CHPEvent> state = new ArrayList<CHPEvent>();
			curt = Thread.currentThread();

			// Start the NEW scraper
	// colorado
	//		final NWSScrapper nwss = new NWSScrapper("COZ205");
			// north bay mountains
			final NWSScrapper nwss = new NWSScrapper("CAZ507");


			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					logger.info("I got a signal to stop!");
					ccs.stop();
					t.interrupt();
					this.interrupt();
					curt.interrupt();
					keeprunning = false;
				}
			});

			while (keeprunning) {
				try {
					try {
						Thread.sleep(1000 * 60);
					} catch (InterruptedException e) {
					}
					List<CHPEvent> l = ccs.returnFiltered(lef);
					doCadChpDiff(l, state, alerters);
					doNwsDiff(nwss.scrape(), alerters);
					state = l;
				} catch(Exception e) {
					logger.error("main run thread exception caught and ignored",e);
				}
			}
		}
		catch(Exception e) {
			if(email!=null) {
				email.emailAdmin("TOP LEVEL EXCEPTION, will stop "+e.getMessage());
			}
		}
	}

	private static Map<String, Feature> lastFeatures = new HashMap();
	private static void doNwsDiff(Map<String,Feature> features, CompoundAlertListener alerters) {

		// find the id's that are new
		List<String> addedList = new ArrayList<String>();
		Set<String> added = new HashSet(features.keySet());
		features.remove("sent");
		Set<String> b = new HashSet(lastFeatures.keySet());
		added.removeAll(b);

		Set<String> a = new HashSet(features.keySet());
		a.remove("sent");
		Set<String> removed = new HashSet(lastFeatures.keySet());
		removed.removeAll(a);

		added.stream().forEach(s-> {
			alerters.alertNws(features.get(s));
		});

		removed.stream().forEach(s-> {
			alerters.alertNwsRemoved(lastFeatures.get(s));
		});

		lastFeatures = features;
		lastFeatures.remove("sent");
	}

	private static void doCadChpDiff(List<CHPEvent> newlist, List<CHPEvent> currentList, AlertListener alerter) {
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
