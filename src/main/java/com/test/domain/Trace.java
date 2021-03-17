package com.test.domain;

import java.util.List;

/**
 * @program: DataMatchDemo
 * @description: 信令数据
 * @author: JayDragon
 * @create: 2020-12-29 14:38
 **/
public class Trace {

    private String id;
    private List<Double> traceLng;
    private List<Double> traceLat;
    private List<String> tracetime;

    public Trace(String id, List<Double> traceLng, List<Double> traceLat, List<String> tracetime) {
        this.id = id;
        this.traceLng = traceLng;
        this.traceLat = traceLat;
        this.tracetime = tracetime;
    }

    public Trace() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Double> getTraceLng() {
        return traceLng;
    }

    public void setTraceLng(List<Double> traceLng) {
        this.traceLng = traceLng;
    }

    public List<Double> getTraceLat() {
        return traceLat;
    }

    public void setTraceLat(List<Double> traceLat) {
        this.traceLat = traceLat;
    }

    public List<String> getTracetime() {
        return tracetime;
    }

    public void setTracetime(List<String> tracetime) {
        this.tracetime = tracetime;
    }

    @Override
    public String toString() {
        return "Trace{" +
                "id='" + id + '\'' +
                ", traceLng=" + traceLng +
                ", traceLat=" + traceLat +
                ", tracetime=" + tracetime +
                '}';
    }
}
