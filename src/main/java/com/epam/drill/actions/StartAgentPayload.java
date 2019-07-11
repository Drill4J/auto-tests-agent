package com.epam.drill.actions;

public class StartAgentPayload {

    private String sessionId;
    private StartPayload payload;

    public String getSessionId() {
        return sessionId;
    }

    public StartPayload getPayload() {
        return payload;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setPayload(StartPayload payload) {
        this.payload = payload;
    }
}
