package com.gyso.gysotreeviewapplication.base;

public class Scene {
    public String id;

    public Scene(String name) {
        this.id = name;
    }

    @Override
    public String toString() {
        return "Scene["+ id +"]";
    }
}
