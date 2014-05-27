package com.github.winbender.chpcadscraper;

public class CHPLine {
	String time;
	String detail;
	public CHPLine(String time, String detail) {
		this.time = time;
		this.detail = detail;
	}
	public String getTime() {
		return time;
	}
	public String getDetail() {
		return detail;
	}
	@Override
	public String toString() {
		return "CHPLine [time=" + time + ", detail=" + detail + "]";
	}

}
