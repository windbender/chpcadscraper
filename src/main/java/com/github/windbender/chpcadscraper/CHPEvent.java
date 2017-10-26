package com.github.windbender.chpcadscraper;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class CHPEvent {
	Double lat;
	Double lon;
	String location;
	String locationDesc;
	String type;

	public boolean isFIRE() {
		return type.toLowerCase().contains("fire");
	}

	List<CHPLine> lines = new ArrayList<CHPLine>();
	EventKey ev;
	public CHPEvent(EventKey ev) {
		this.ev = ev;
	}


	public void clearLines() {
		List<CHPLine> lines = new ArrayList<CHPLine>();
		
	}

	public void addLine(CHPLine chpLine) {
		lines.add(chpLine);
	}

}
