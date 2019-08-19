package com.github.windbender.chpcadscraper;

import org.geojson.Feature;

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

	@Override
	public void alertNws(Feature feature) {
		for(AlertListener al: alerters) {
			al.alertNws(feature);
		}
	}

	@Override
	public void alertNwsRemoved(Feature feature) {
		for(AlertListener al: alerters) {
			al.alertNwsRemoved(feature);
		}
	}

}
