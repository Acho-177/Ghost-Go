package edu.msu.hujiahui.team13.Cloud.Models;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * Nested class to store one catalog row
 */
@Root(name = "hatting")
public final class Item {
    @Attribute
    private String id;

    @Attribute
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Item(String id, String name) {
        this.name = name;
        this.id = id;
    }

    public Item() {}

}
