package com.github.windbender.chpcadscraper;

import java.util.ArrayList;
import java.util.List;

public class CompoundAlertListener implements AlertListener {

	private List<AlertListener> alerters = new ArrayList<AlertListener>();

	public void add(AlertListener a) {
		alerters.add(a);
	}
	public void alertAdded(List<CHPEvent> events) {
		for(AlertListener al: alerters) {
			al.alertAdded(events);
		}
	}

	public void alertGone(List<CHPEvent> events) {
		for(AlertListener al: alerters) {
			al.alertGone(events);
		}
	}

	public void alertChanged(List<CHPEvent> events) {
		for(AlertListener al: alerters) {
			al.alertChanged(events);
		}
	}

	public void emailAdmin(String msg) {
		for(AlertListener al: alerters) {
			al.emailAdmin(msg);
		}
	}

}
