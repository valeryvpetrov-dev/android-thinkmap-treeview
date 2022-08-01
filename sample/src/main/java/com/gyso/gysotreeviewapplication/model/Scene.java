package com.gyso.gysotreeviewapplication.model;

import java.util.List;

public class Scene extends AbstractScene {
    private List<Option> options;

    public Scene(String id, List<Option> options) {
        super(id);
        this.options = options;
    }

    public List<Option> getOptions() {
        return options;
    }

    @Override
    public String toString() {
        return "Scene{" +
                "id='" + id + '\'' +
                ", options=" + options +
                '}';
    }
}
