package com.github.windbender.chpcadscraper;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.geojson.Feature;
import org.geojson.FeatureCollection;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class NWSScrapper {
    private final String alertZone;
    private final ObjectMapper objectMapper;
    public static void main(String[] args) {
        NWSScrapper c  = new NWSScrapper("CAZ507");
        c.scrape();

    }
    public NWSScrapper( String alertZone) {
        this.alertZone = alertZone;
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }


    public synchronized Map<String,Feature> scrape() {
        try {
            String cadDataUrl = new String("https://api.weather.gov/alerts/active/zone/"+this.alertZone);

            InputStream inputStream = new URL(cadDataUrl).openStream();

            FeatureCollection featureCollection = objectMapper.readValue(inputStream, FeatureCollection.class);

            Map<String,Feature> l = featureCollection.getFeatures().stream().filter(f -> {
                Object o = f.getProperties().get("event");
                return "Red Flag Warning".equalsIgnoreCase((String) o);
            }).collect(Collectors.toMap(Feature::getId, f->f));

            return l;
        } catch(Exception e) {
            emailAdmin("could not parse NWS alerts for "+alertZone+"  because  "+e);
            log.error("can't parse NWS stuff because ",e);
        }
        return new HashMap<>();
    }

    private void emailAdmin(String s) {
    }
}
