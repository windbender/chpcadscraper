package com.github.windbender.chpcadscraper;

import org.geojson.Feature;

import java.util.List;

public interface AlertListener {
	void alertAdded(List<CHPEvent> events);
	void alertGone(List<CHPEvent> events);
	void alertChanged(List<CHPEvent> events);

	void emailAdmin(String msg);

	void alertNws(Feature feature);
	void alertNwsRemoved(Feature feature);
}
