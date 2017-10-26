package com.github.windbender.chpcadscraper;

import java.io.File;
import java.util.List;

import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Feature;
import de.micromata.opengis.kml.v_2_2_0.Geometry;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.LineString;
import de.micromata.opengis.kml.v_2_2_0.Placemark;

public class LocationFilter implements EventFilter {
	Region r = new Region();
	public LocationFilter() {
		
	}
	public void loadFromKML(File file) {
		Kml kml = Kml.unmarshal(file);
		Feature f = kml.getFeature();
		Document d = (Document) f;
		List<Feature> fl = d.getFeature();
		Placemark pm = (Placemark) fl.get(0);
		Geometry g = pm.getGeometry();
		LineString ls = (LineString) g;
		for(de.micromata.opengis.kml.v_2_2_0.Coordinate c: ls.getCoordinates()) {
			Coordinate c2 = makeCoord(c);
			r.add(c2);
		}
	}
	private Coordinate makeCoord(de.micromata.opengis.kml.v_2_2_0.Coordinate c) {
		Coordinate c2 = new Coordinate(c.getLatitude(),c.getLongitude());
		return c2;
	}
	public boolean test(CHPEvent ce) {
		if(ce.lat == null) return false;
		if(ce.lon == null) return false;
		Coordinate c = new Coordinate(ce.lat,ce.lon);
		return RegionUtil.coordinateInRegion(r, c);
	}


}
