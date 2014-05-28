package com.github.windbender.chpcadscraper;


public class RegionUtil {
	public static boolean coordinateInRegion(Region region, Coordinate coord) {
        int i, j;
        boolean isInside = false;
        //create an array of coordinates from the region boundary list
        Coordinate[] verts = (Coordinate[])region.getBoundary().toArray(new Coordinate[region.size()]);
        int sides = verts.length;
        for (i = 0, j = sides - 1; i < sides; j = i++) {
            //verifying if your coordinate is inside your region
            if (
                (
                 (
                  (verts[i].getLongitude() <= coord.getLongitude()) && (coord.getLongitude() < verts[j].getLongitude())
                 ) || (
                  (verts[j].getLongitude() <= coord.getLongitude()) && (coord.getLongitude() < verts[i].getLongitude())
                 )
                ) &&
                (coord.getLatitude() < (verts[j].getLatitude() - verts[i].getLatitude()) * (coord.getLongitude() - verts[i].getLongitude()) / (verts[j].getLongitude() - verts[i].getLongitude()) + verts[i].getLatitude())
               ) {
                isInside = !isInside;
            }
        }
        return isInside;
    }
}
