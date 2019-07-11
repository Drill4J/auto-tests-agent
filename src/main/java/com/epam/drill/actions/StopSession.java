package com.epam.drill.actions;

public class StopSession {

    private String type;
    private StopPayload payload;

    public StopSession(String type, StopPayload payload) {
        this.type = type;
        this.payload = payload;
    }

    public String getType() {
        return type;
    }

    public StopPayload getPayload() {
        return payload;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPayload(StopPayload payload) {
        this.payload = payload;
    }
}
