package com.fonda.b6.examples.data;

public class PlayerActivity {
    Long id;
    String name;
    String location;
    String activity;

    public PlayerActivity(Long id, String name, String location, String activity) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.activity = activity;
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getLocation() {
        return this.location;
    }

    public String getActivity() {
        return this.activity;
    }

    @Override
    public String toString() {
        return "PlayerActivity{" +
                "id=" + id +
                ", name=" + name +
                ", location=" + location +
                ", activity=" + activity +
                "}";
    }
}