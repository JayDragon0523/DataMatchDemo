package com.test.domain;

import java.util.List;

/**
 * @program: DataMatchDemo
 * @description: 微博数据实体
 * @author: JayDragon
 * @create: 2020-12-29 14:31
 **/
public class Weibo {

    private String id;
    private List<Double> traceLng;
    private List<Double> traceLat;
    private List<String> tracetime;
    private List<String> location;

    public Weibo() {
    }

    public Weibo(String id, List<String> tracetime, List<String> location) {
        this.id = id;
        this.tracetime = tracetime;
        this.location = location;
    }

    public Weibo(String id, List<Double> traceLng, List<Double> traceLat, List<String> tracetime) {
        this.id = id;
        this.traceLng = traceLng;
        this.traceLat = traceLat;
        this.tracetime = tracetime;
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

    public List<String> getLocation() {
        return location;
    }

    public void setLocation(List<String> location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Weibo{" +
                "id='" + id + '\'' +
                ", traceLng=" + traceLng +
                ", traceLat=" + traceLat +
                ", tracetime=" + tracetime +
                ", location=" + location +
                '}';
    }
}
