package com.tools.image;

public class ImageEffect {
    private int arcW = -1;
    private int arcH = -1;
    private int blur = -1;


    public ImageEffect setArc(int Arc) {
        this.arcW = Arc;
        this.arcH = Arc;
        return this;
    }

    public int getArcW() {
        return arcW;
    }

    public ImageEffect setArcW(int arcW) {
        this.arcW = arcW;
        return this;
    }

    public int getArcH() {
        return arcH;
    }

    public ImageEffect setArcH(int arcH) {
        this.arcH = arcH;
        return this;
    }

    public int getBlur() {
        return blur;
    }

    public ImageEffect setBlur(int blur) {
        this.blur = blur;
        return this;
    }
}
