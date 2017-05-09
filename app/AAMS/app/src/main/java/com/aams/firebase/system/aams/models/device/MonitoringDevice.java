package com.aams.firebase.system.aams.models.device;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class MonitoringDevice {

    private String name;
    private int level;
    private String key;

    public MonitoringDevice() {

    }

    public MonitoringDevice(String name, int deviceLevel, String key) {
        this.name = name;
        this.level = deviceLevel;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("level", level);
        result.put("key", key);

        return result;
    }
}
