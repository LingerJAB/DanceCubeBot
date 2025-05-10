package com.dancecube.ratio;

/**
 * 舞立方成绩精确度评级
 */
public enum AccGrade {
    SSS_AP(100),
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

    /**
     * 通过精确度获取精度评级
     *
     * @param acc 精确度
     * @return 精度评级
     */
    public static AccGrade get(float acc) {
        for(AccGrade grade : AccGrade.values()) {
            if(acc>=grade.getMinAcc()) {
                return grade;
            }
        }
        return AccGrade.D;
    }

    /**
     * 仅测试用，获取随机精度评级
     *
     * @return 随机的AccGrade
     */
    public static AccGrade getRandom() {
        return values()[(int) (Math.random() * values().length)];
    }

}