package com.github.windbender.chpcadscraper.chpdata;

import org.xml.sax.Attributes;

public interface Able {
    void addChars(String chars);
    void doAttributes(Attributes attributes);
}
