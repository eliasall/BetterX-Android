package com.betterx.featureslogger.datamodel;

import java.io.Serializable;

public class ScreenSize implements Serializable {

    public int height;
    public int width;

    public ScreenSize() {
    }

    public ScreenSize(int height, int width) {
        this.height = height;
        this.width = width;
    }

    @Override
    public String toString() {
        return "ScreenSize{" +
                "height=" + height +
                ", width=" + width +
                '}';
    }

}
