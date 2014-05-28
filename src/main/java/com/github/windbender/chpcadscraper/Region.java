package com.github.windbender.chpcadscraper;

import java.util.ArrayList;
import java.util.List;


public class Region {
	List<Coordinate> boundary = new ArrayList<Coordinate>();
	public Region add(Coordinate c) {
		boundary.add(c);
		return this;
	}

	public List<Coordinate> getBoundary() {
		return boundary;
	}
	public int size() {
		return boundary.size();
	}
	
}