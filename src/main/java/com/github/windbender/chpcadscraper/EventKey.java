package com.github.windbender.chpcadscraper;


import lombok.Data;

@Data
public class EventKey implements Comparable<EventKey> {
	String id;
	public EventKey(String chipidStr) {
		this.id = chipidStr;
	}


	public int compareTo(EventKey o) {
		return this.id.compareTo(o.id);
	}
}
