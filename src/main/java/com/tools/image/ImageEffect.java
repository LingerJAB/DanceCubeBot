package com.tools.image;

public class ImageEffect {
    int arcW = -1;
    int arcH = -1;
    int blur = -1;

    public ImageEffect(int arcW, int arcH) {
        this.arcW = arcW;
        this.arcH = arcH;
    }

    public ImageEffect(int arcW, int arcH, int blur) {
        this.arcW = arcW;
        this.arcH = arcH;
        this.blur = blur;
    }
}
