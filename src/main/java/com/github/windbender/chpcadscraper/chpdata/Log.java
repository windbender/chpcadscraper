package com.github.windbender.chpcadscraper.chpdata;

import lombok.Data;
import org.xml.sax.Attributes;

import java.util.ArrayList;
import java.util.List;

@Data
public class Log implements Able {
    String ID;
    String logTime;
    String logType;
    String LATLON;
    String area;
    String location;
    String locationDesc;

    List<Detail> logDetails = new ArrayList<Detail>();

    Detail currentDetail;
    public void addChars(String chars) {
        if(which != null) {
            if ("LogTime".equals(which)) {
                this.logTime = this.logTime == null ? chars : this.logTime + chars;
            } else if ("LogType".equals(which)) {
                this.logType = this.logType == null ? chars : this.logType + chars;
            } else if ("LATLON".equals(which)) {
                this.LATLON = this.LATLON == null ? chars : this.LATLON + chars;
            } else if ("Area".equals(which)) {
                this.area = this.area == null ? chars : this.area + chars;
            } else if ("DetailTime".equals(which)) {
                this.currentDetail.time = this.currentDetail.time == null ? chars : this.currentDetail.time + chars;
            } else if ("IncidentDetail".equals(which)) {
                this.currentDetail.txt = this.currentDetail.txt == null ? chars : this.currentDetail.txt + chars;
            } else if ("Location".equals(which)) {
                this.location = this.location == null ? chars : this.location + chars;
            } else if ("LocationDesc".equals(which)) {
                this.locationDesc = this.locationDesc == null ? chars : this.locationDesc + chars;
            }
        }
    }

    public void doAttributes(Attributes attributes) {
        ID = attributes.getValue("ID");
    }

    String which = null;
    public void startElement(String qName, Attributes attributes) {
        which = qName;
        if(qName.equals("details")) {
            this.currentDetail = new Detail();
        }
    }
    public void endElement(String qName) {
        if(qName.equals("details")) {
            this.logDetails.add(this.currentDetail);
        }
        which = null;
    }

}
