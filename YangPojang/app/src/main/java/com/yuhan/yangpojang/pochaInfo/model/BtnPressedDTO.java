// Updated BtnPressedDTO class
package com.yuhan.yangpojang.pochaInfo.model;

import java.util.List;

public class BtnPressedDTO {
    private List<String> pressedShops;
    private int verifiedCount;
    private int singoCount;

    public BtnPressedDTO() {
        // Default constructor
    }

    public BtnPressedDTO(List<String> pressedShops, int verifiedCount, int singoCount) {
        this.pressedShops = pressedShops;
        this.verifiedCount = verifiedCount;
        this.singoCount = singoCount;
    }

    public List<String> getPressedShops() {
        return pressedShops;
    }

    public void setPressedShops(List<String> pressedShops) {
        this.pressedShops = pressedShops;
    }

    public int getVerifiedCount() {
        return verifiedCount;
    }

    public void setVerifiedCount(int verifiedCount) {
        this.verifiedCount = verifiedCount;
    }

    public int getSingoCount() {
        return singoCount;
    }

    public void setSingoCount(int singoCount) {
        this.singoCount = singoCount;
    }
}
