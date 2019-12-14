package com.hjc.http.resp;

import java.io.Serializable;

public class RedirectResponse implements Serializable {
    private String location;

    public RedirectResponse() {
    }

    public RedirectResponse(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
