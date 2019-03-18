package com.projects.automatedattendancesystem.Pojo;

import com.projects.automatedattendancesystem.Tables.Class;

import java.util.List;
import java.util.Map;

public class ReportPojo {

    private String SlNo;

    private String Stud_Id,Stud_Name,Father_Name,Class_Name;

    private List<Class> attendanceList;

    private Map<String,String> dateByTimingsMap;

    public Map<String, String> getDateByTimingsMap() {
        return dateByTimingsMap;
    }

    public void setDateByTimingsMap(Map<String, String> dateByTimingsMap) {
        this.dateByTimingsMap = dateByTimingsMap;
    }

    public String getSlNo() {
        return SlNo;
    }

    public void setSlNo(String slNo) {
        SlNo = slNo;
    }

    public String getStud_Id() {
        return Stud_Id;
    }

    public void setStud_Id(String stud_Id) {
        Stud_Id = stud_Id;
    }

    public String getStud_Name() {
        return Stud_Name;
    }

    public void setStud_Name(String stud_Name) {
        Stud_Name = stud_Name;
    }

    public String getFather_Name() {
        return Father_Name;
    }

    public void setFather_Name(String father_Name) {
        Father_Name = father_Name;
    }

    public String getClass_Name() {
        return Class_Name;
    }

    public void setClass_Name(String class_Name) {
        Class_Name = class_Name;
    }

    public List<Class> getAttendanceList() {
        return attendanceList;
    }

    public void setAttendanceList(List<Class> attendanceList) {
        this.attendanceList = attendanceList;
    }
}
