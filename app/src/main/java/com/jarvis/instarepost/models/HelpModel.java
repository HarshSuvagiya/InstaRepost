package com.jarvis.instarepost.models;

public class HelpModel {
    private String help;
    private int image;

    public String getHelp() {
        return this.help;
    }

    public int getImage() {
        return this.image;
    }

    public HelpModel setHelp(String str) {
        this.help = str;
        return this;
    }

    public HelpModel setImage(int i) {
        this.image = i;
        return this;
    }
}
