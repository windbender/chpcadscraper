package com.github.windbender.chpcadscraper;

import java.util.List;

public interface AlertListener {
	public void alertAdded(List<CHPEvent> events);
	public void alertGone(List<CHPEvent> events);
	public void alertChanged(List<CHPEvent> events);
}
