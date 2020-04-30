package com.browser.myproxyvpn.dialog;

public class ProxyModel {

    private String name;
    private String country;
    private int port;
    private String type;
    private String initial;
    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountry() {
        return this.country;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return this.port;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public void setInitial(String initial) {
        this.initial = initial;
    }

    public String getInitial() {
        return this.initial;
    }
}
