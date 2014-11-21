package com.github.windbender.chpcadscraper;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrintAlertListener implements AlertListener {

	Logger logger = LoggerFactory.getLogger(PrintAlertListener.class);

	public void alertAdded(List<CHPEvent> events) {
		for(CHPEvent e: events) {
			logger.info("added "+e);
		}
	}

	public void alertGone(List<CHPEvent> events) {
		for(CHPEvent e: events) {
			logger.info("removed "+e);
		}
	}

	public void alertChanged(List<CHPEvent> events) {
		for(CHPEvent e: events) {
			logger.info("changed "+e);
		}
	}

}
