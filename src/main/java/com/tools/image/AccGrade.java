package com.tools.image;

/**
 * 舞立方成绩游玩评级
 */
public enum AccGrade {
    A(80),
    B(70),
    C(60),
    D(0),
    S(90),
    SS(95),
    SSS(98);

    private final float minAcc;

    AccGrade(int minAcc) {
        this.minAcc = minAcc;
    }

    public float getMinAcc() {
        return minAcc;
    }

    public static AccGrade get(float acc) {
        for(AccGrade grade : AccGrade.values()) {
            if(acc>=grade.getMinAcc()) {
                return grade;
            }
        }
        return AccGrade.D;
    }
}