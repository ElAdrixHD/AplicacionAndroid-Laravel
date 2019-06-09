package com.example.rss.model;

/**
 * Created by paco
 */

import java.io.Serializable;

public class Site implements Serializable {
    private int id;
    private String name;
    private String link;
    private String email;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getLink() {
        return link;
    }
    public void setLink(String link) {
        this.link = link;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public Site(int id, String name, String link, String email) {
        super();
        this.id = id;
        this.name = name;
        this.link = link;
        this.email = email;
    }
    public Site(String name, String link, String email) {
        super();
        this.name = name;
        this.link = link;
        this.email = email;
    }

    public  Site() {}

    @Override
    public String toString() {
        return  name + '\n' + email;
    }
}
