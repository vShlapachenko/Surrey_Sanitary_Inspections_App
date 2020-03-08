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

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
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
