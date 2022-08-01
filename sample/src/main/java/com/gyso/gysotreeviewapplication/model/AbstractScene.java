package com.gyso.gysotreeviewapplication.model;

public abstract class AbstractScene {
    protected String id;

    public AbstractScene(String name) {
        this.id = name;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "AbstractScene{" +
                "id='" + id + '\'' +
                '}';
    }
}
