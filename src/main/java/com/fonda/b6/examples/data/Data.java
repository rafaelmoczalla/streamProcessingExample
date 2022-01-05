package com.fonda.b6.examples.data;

public class Data {
    Long key;
    Double value;

    public Data(Long key, Double value) {
        this.key = key;
        this.value = value;
    }

    public Long getKey() {
        return this.key;
    }

    public Double getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return "Data{" +
                "key=" + key +
                ", value=" + value +
                '}';
    }
}