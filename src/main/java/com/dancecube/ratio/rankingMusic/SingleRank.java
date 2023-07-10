package com.dancecube.ratio.rankingMusic;


import com.dancecube.ratio.image.AccGrade;

public class SingleRank {
    int difficulty;
    int level;
    int combo;
    int miss;
    float acc;
    float ratio;

    public int getMiss() {
        return miss;
    }

    public int getCombo() {
        return combo;
    }

    public int getScore() {
        return score;
    }

    public int getRank() {
        return rank;
    }

    int score;
    int rank;

    public int getDifficulty() {
        return difficulty;
    }

    public int getLevel() {
        return level;
    }

    public float getAcc() {
        return acc;
    }

    public AccGrade getGrade() {
        if(acc>=98) return AccGrade.SSS;
        else if(acc>=95) return AccGrade.SS;
        else if(acc>=90) return AccGrade.S;
        else if(acc>=80) return AccGrade.A;
        else if(acc>=70) return AccGrade.B;
        else if(acc>=60) return AccGrade.C;
        else return AccGrade.D;
    }

    public float getRatio() {
        return ratio;
    }

    public SingleRank(int difficulty, int level, float acc, int score, int rank, int combo, int miss) {
        this.difficulty = difficulty;
        this.level = level;
        this.acc = acc;
        this.score = score;
        this.rank = rank;
        this.combo = combo;
        this.miss = miss;
        this.ratio = (level + 2) * acc;
    }

    @Override
    public String toString() {
        return "SingleRank{" +
                "difficulty=" + difficulty +
                ", level=" + level +
                ", acc=" + acc +
                ", ratio=" + ratio +
                '}';
    }
}

