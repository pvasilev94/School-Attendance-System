package com.aams.firebase.system.aams.models.student;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;
import com.aams.firebase.system.aams.models.device.MonitoringDevice;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class StudentBeacon {
    private String studentId;
    private String beaconMAC;
    @JsonProperty
    private Object TIMESTAMP;
    boolean lastSeen;
    private MonitoringDevice deviceData;
    private String contactNumber;
    private String contactName;

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public StudentBeacon() {
        // Default constructor required for calls to DataSnapshot.getValue(StudentBeacon.class)
    }

    public StudentBeacon(String studentId, String beaconMAC, boolean isAvailable, MonitoringDevice deviceData) {
        this.studentId = studentId;
        this.beaconMAC = beaconMAC;
        this.TIMESTAMP = ServerValue.TIMESTAMP;
        this.lastSeen = isAvailable;
        this.deviceData = deviceData;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getBeaconMAC() {
        return beaconMAC;
    }

    public void setBeaconMAC(String beaconMAC) {
        this.beaconMAC = beaconMAC;
    }

    public boolean isLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(boolean lastSeen) {
        this.lastSeen = lastSeen;
    }

    public Object getTIMESTAMP() {
        return TIMESTAMP;
    }

    public void setTIMESTAMP(Object TIMESTAMP) {
        this.TIMESTAMP = TIMESTAMP;
    }

    public MonitoringDevice getDeviceData() {
        return deviceData;
    }

    public void setDeviceData(MonitoringDevice deviceData) {
        this.deviceData = deviceData;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("beaconMAC", beaconMAC);
        result.put("studentId", studentId);
        result.put("lastSeen", lastSeen);
        result.put("timestamp", TIMESTAMP);
        result.put("contactNumber", contactNumber);
        result.put("contactName", contactName);
        result.put("device", deviceData.toMap());

        return result;
    }
    @JsonIgnore
    public Long getCreatedTimestamp() {
        if (TIMESTAMP instanceof Long) {
            return (Long) TIMESTAMP;
        }
        else {
            return null;
        }
    }

    @Override
    public String toString() {
        return "StudentBeacon{" +
                "studentId='" + studentId + '\'' +
                ", beaconMAC='" + beaconMAC + '\'' +
                ", TIMESTAMP=" + TIMESTAMP +
                ", lastSeen=" + lastSeen +
                ", deviceData=" + deviceData.getName() + deviceData.getLevel() +

                '}';
    }

}
