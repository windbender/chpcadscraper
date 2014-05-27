package com.github.winbender.chpcadscraper;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebElement;

public class CHPEvent {
	Double lat;
	Double lon;
	String location;
	String type;
	List<CHPLine> lines = new ArrayList<CHPLine>();
	EventKey ev;
	public CHPEvent(EventKey ev) {
		this.ev = ev;
	}

	@Override
	public String toString() {
		return "CHPEvent [lat=" + lat + ", lon=" + lon + ", location="
				+ location + ", type=" + type + ", lines=" + lines + ", ev="
				+ ev + "]";
	}

	public void setLl(WebElement ll) {
		String s= ll.getText();
		if(s.contains(":")) {
			return;
		}
		String[] parts = s.split(" ");
		lat = Double.parseDouble(parts[0]);
		lon = Double.parseDouble(parts[1]);
	}

	public void setLoc(WebElement loc) {
		location = loc.getText();
	
	}

	public void setType(WebElement t) {
		type = t.getText();
	}

	public void clearLines() {
		List<CHPLine> lines = new ArrayList<CHPLine>();
		
	}

	public void addLine(CHPLine chpLine) {
		lines.add(chpLine);
	}

}
