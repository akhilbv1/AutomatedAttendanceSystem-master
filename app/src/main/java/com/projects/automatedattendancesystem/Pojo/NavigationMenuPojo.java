package com.projects.automatedattendancesystem.Pojo;

public class NavigationMenuPojo {

    private String menuName;
    private int position;

    public NavigationMenuPojo(String menuName, int position) {
        this.menuName = menuName;
        this.position = position;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
