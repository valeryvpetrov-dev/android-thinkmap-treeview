package com.gyso.gysotreeviewapplication.base;

import java.util.List;

public class Scene {
    public String id;
    public List<Option> options;

    public Scene(String name, List<Option> options) {
        this.id = name;
        this.options = options;
    }

    @Override
    public String toString() {
        return "Scene["+ id +"]";
    }
}
