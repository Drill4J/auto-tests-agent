package com.epam.drill.actions;

public class StatusResponse {
    Integer code;
    StartAgentSession data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public StartAgentSession getData() {
        return data;
    }

    public void setData(StartAgentSession data) {
        this.data = data;
    }
}
