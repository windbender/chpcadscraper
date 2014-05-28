package com.github.windbender.chpcadscraper;

import java.util.List;

public class PrintAlertListener implements AlertListener {

	public void alertAdded(List<CHPEvent> events) {
		for(CHPEvent e: events) {
			System.out.println("added "+e);
		}
	}

	public void alertGone(List<CHPEvent> events) {
		for(CHPEvent e: events) {
			System.out.println("removed "+e);
		}
	}

	public void alertChanged(List<CHPEvent> events) {
		for(CHPEvent e: events) {
			System.out.println("changed "+e);
		}
	}

}
