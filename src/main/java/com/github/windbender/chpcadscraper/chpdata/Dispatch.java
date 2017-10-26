package com.github.windbender.chpcadscraper.chpdata;

import lombok.Data;
import org.xml.sax.Attributes;

import java.util.ArrayList;
import java.util.List;

@Data
public class Dispatch implements Able {
    String ID;

    public void addChars(String chars) {

    }

    public void doAttributes(Attributes attributes) {
        ID = attributes.getValue("ID");
    }
    List<Log> logs = new ArrayList<Log>();

    public void add(Log log) {
        logs.add(log);
    }
}
