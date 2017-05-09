package com.aams.firebase.system.aams.models.beacon;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Student {
    private String studentId;
    private String studentName;
    private String parentName;
    private String parentPhone;

    public Student() {
        // Default constructor required for calls to DataSnapshot.getValue(Student.class)
    }

    public Student(String studentId, String studentName, String parentName, String parentPhone) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.parentName = parentName;
        this.parentPhone = parentPhone;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentPhone() {
        return parentPhone;
    }

    public void setParentPhone(String parentPhone) {
        this.parentPhone = parentPhone;
    }
}
