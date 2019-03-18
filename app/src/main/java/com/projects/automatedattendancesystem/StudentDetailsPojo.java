package com.projects.automatedattendancesystem;

public class StudentDetailsPojo {
    private boolean isCheckedIn;

    private boolean className;

    public boolean isCheckedIn() {
        return isCheckedIn;
    }

    public void setCheckedIn(boolean checkedIn) {
        isCheckedIn = checkedIn;
    }

    public boolean isClassName() {
        return className;
    }

    public void setClassName(boolean className) {
        this.className = className;
    }
}
