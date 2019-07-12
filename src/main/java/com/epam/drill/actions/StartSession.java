package com.epam.drill.actions;

public class StartSession {

    private String type;
    private StartPayload payload;

    public StartSession(String type, StartPayload payload) {
        this.type = type;
        this.payload = payload;
    }

    public String getType() {
        return type;
    }

    public StartPayload getPayload() {
        return payload;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPayload(StartPayload payload) {
        this.payload = payload;
    }
}
