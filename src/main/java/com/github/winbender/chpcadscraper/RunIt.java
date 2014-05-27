package com.github.winbender.chpcadscraper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;

public class RunIt {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		File f = new File("/Users/chris/Desktop/OurArea.kml");
		File f = new File("/Users/chris/Desktop/TestArea.kml");
		LocationEventFilter lef = new LocationEventFilter();
		lef.loadFromKML(f);
		int period = 1000*60*5;
		ChpCadScraper ccs = new ChpCadScraper("GGCC",period);
		ccs.setDetailFilter(lef);
		Thread t = new Thread(ccs);
		t.setName("cad scraper");
		t.start();
		List<CHPEvent> state = new ArrayList<CHPEvent>();
		while(true) {
			try {
				Thread.sleep(1000 * 60);
			} catch (InterruptedException e) {
			}
			List<CHPEvent> l = ccs.returnFiltered(lef);
			doDiff(l,state);
			state = l;
		}
		
	}

	private static void doDiff(List<CHPEvent> newlist, List<CHPEvent> currentList) {
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
		for(CHPEvent a: addedList) {
			System.out.println(""+dt+" added "+a);
		}
		for(CHPEvent r: removedList) {
			System.out.println(""+dt+" remov "+r);
		}
		
	}

}
