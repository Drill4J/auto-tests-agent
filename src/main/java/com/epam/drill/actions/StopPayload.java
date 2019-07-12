package com.epam.drill.actions;

public class StopPayload {

    private String sessionId;

    public StopPayload(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
