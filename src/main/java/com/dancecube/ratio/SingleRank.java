package com.dancecube.ratio;


import com.tools.image.AccGrade;

/**
 * RankMusic的一个其中难度记录
 */
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
        return AccGrade.get(score);
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

