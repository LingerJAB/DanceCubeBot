package com.tools.image;

/**
 * 舞立方成绩游玩评级
 */
public enum AccGrade {
    SSS(98),
    SS(95),
    S(90),
    A(80),
    B(70),
    C(60),
    D(0);

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