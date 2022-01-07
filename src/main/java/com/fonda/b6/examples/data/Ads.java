package com.fonda.b6.examples.data;

public class Ads {
    Long id;
    Long group;
    Double length;

    public Ads(Long id, Long group, Double length) {
        this.id = id;
        this.group = group;
        this.length = length;
    }

    public Long getId() {
        return this.id;
    }

    public Long getGroup() {
        return this.group;
    }

    public Double getLength() {
        return this.length;
    }

    @Override
    public String toString() {
        return "Ads{" +
                "id=" + id +
                ", group=" + group +
                ", length=" + length +
                "}";
    }
}