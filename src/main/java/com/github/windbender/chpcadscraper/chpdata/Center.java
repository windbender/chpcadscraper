package com.github.windbender.chpcadscraper.chpdata;

import lombok.Data;
import org.xml.sax.Attributes;

import java.util.ArrayList;
import java.util.List;

@Data
public class Center implements Able{
    String ID;
    public void doAttributes(Attributes attributes) {
        ID = attributes.getValue("ID");
    }

    List<Dispatch> dispatches = new ArrayList<Dispatch>();

    public void add(Dispatch dispatch) {
        dispatches.add(dispatch);
    }

    public void addChars(String chars) {

    }

}
