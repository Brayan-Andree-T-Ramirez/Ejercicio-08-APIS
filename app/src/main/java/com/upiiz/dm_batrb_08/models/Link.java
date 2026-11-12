package com.upiiz.dm_batrb_08.models;

import java.io.Serializable;

public class Link implements Serializable {
    private String rel;
    private String href;

    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}
