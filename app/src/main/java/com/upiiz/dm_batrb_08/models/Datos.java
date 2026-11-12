package com.upiiz.dm_batrb_08.models;

import java.util.List;

public class Datos {
    private List<Cliente> content;
    private List<Link> links;

    public List<Cliente> getContent() {
        return content;
    }

    public void setContent(List<Cliente> content) {
        this.content = content;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }
}
