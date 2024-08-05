package com.tools.image;

public class TextEffect {
    private Integer maxWidth = null;
    private Integer spaceHeight = null;

    public TextEffect() {
    }

    public Integer getMaxWidth() {
        return maxWidth;
    }

    public TextEffect setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
        return this;
    }

    public Integer getSpaceHeight() {
        return spaceHeight;
    }

    public TextEffect setSpaceHeight(int spaceHeight) {
        this.spaceHeight = spaceHeight;
        return this;
    }
}
