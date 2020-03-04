package com.example.cmpt276_project_iron.model;

/**
 * Stores information about single violation
 */

public class Violation {
    private final int violationNum;
    private final boolean critical;
    private final String problemDescription;
    private final boolean repeat;

    public Violation(int violationNum, boolean critical, String problemDescription, boolean repeat) {
        this.violationNum = violationNum;
        this.critical = critical;
        this.problemDescription = problemDescription;
        this.repeat = repeat;
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
