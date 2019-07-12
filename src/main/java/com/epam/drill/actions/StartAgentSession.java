package com.epam.drill.actions;

public class StartAgentSession {

    private String type;
    private StartAgentPayload payload;

    public String getType() {
        return type;
    }

    public StartAgentPayload getPayload() {
        return payload;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPayload(StartAgentPayload payload) {
        this.payload = payload;
    }
}
