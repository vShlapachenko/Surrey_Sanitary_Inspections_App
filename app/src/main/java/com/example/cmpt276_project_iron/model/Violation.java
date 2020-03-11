package com.example.cmpt276_project_iron.model;

/**
 * Stores information about single violation
 */

public class Violation {
    private final int violationNum;
    private final boolean critical;
    private final String problemDescription;
    private final boolean repeat;
    private int iconId;
    private int hazIconId;

    public int getHazIconId() {
        return hazIconId;
    }

    public void setHazIconId(int hazIconId) {
        this.hazIconId = hazIconId;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    @Override
    public String toString() {
        return "Violation{" +
                "violationNum=" + violationNum +
                ", critical=" + critical +
                ", problemDescription='" + problemDescription + '\'' +
                ", repeat=" + repeat +
                ", iconId=" + iconId +
                ", hazIconId=" + hazIconId +
                '}';
    }

    public Violation(int violationNum, boolean critical, String problemDescription, boolean repeat) {
        this.violationNum = violationNum;
        this.critical = critical;
        this.problemDescription = problemDescription;
        this.repeat = repeat;
        this.iconId = 0;
    }

    public int getViolationNum() {
        return violationNum;
    }

    public boolean isCritical() {
        return critical;
    }

    public String getProblemDescription() {
        return problemDescription;
    }

    public boolean isRepeat() {
        return repeat;
    }
}
