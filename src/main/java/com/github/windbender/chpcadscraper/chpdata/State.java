package com.github.windbender.chpcadscraper.chpdata;

import lombok.Data;
import org.xml.sax.Attributes;

import java.util.ArrayList;
import java.util.List;

@Data
public class State implements Able {
    List<Center> centers = new ArrayList<Center>();
    public void add(Center center) {
        centers.add(center);
    }

    public void addChars(String chars) {

    }

    public void doAttributes(Attributes attributes) {

    }

}
