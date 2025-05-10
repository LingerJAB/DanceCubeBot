package com.dancecube.ratio;

/**
 * 已游玩谱面成绩歌曲的父类，包括RankMusic和RecentMusic
 */
public abstract class RecordedMusicInfo {
    final int id;
    final String name;
    final int difficulty;  // show 为-1
    final int level; // 1-19
    final int levelType;  // 经典1x show 10x
    final float accuracy;
    final int score;
    final int combo;
    final int miss;
    final float ratio;

    protected RecordedMusicInfo(int id, String name, int difficulty, int level, int levelType, float accuracy, int score, int combo, int miss) {
        this.id = id;
        this.name = name;
        this.difficulty = difficulty;
        this.level = level;
        this.levelType = levelType;
        this.accuracy = accuracy;
        this.score = score;
        this.combo = combo;
        this.miss = miss;
        this.ratio = accuracy * (level + 2);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getRatio() {
        return ratio;
    }

    public int getRatioInt() {
        return Math.round(getRatio());
    }

    public float getAccuracy() {
        return accuracy;
    }

    public int getScore() {
        return score;
    }

    public int getCombo() {
        return combo;
    }

    public int getMiss() {
        return miss;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public int getLevel() {
        return level;
    }

    public int getLevelType() {
        return levelType;
    }

    public AccGrade getAccGrade() {
        return AccGrade.get(getAccuracy());
    }

    public boolean isFullCombo() {
        return miss == 0;
    }

    public boolean isAllPerfect() {
        return accuracy == 100f;
    }

    abstract boolean isOfficial();
}
