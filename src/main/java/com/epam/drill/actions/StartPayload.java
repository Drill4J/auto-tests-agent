package com.epam.drill.actions;

public class StartPayload {

    private String testType;

    public StartPayload(String testType) {
        this.testType = testType;
    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

}
