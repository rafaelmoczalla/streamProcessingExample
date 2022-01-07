package com.fonda.b6.examples.data;

public class Data<T> {
    Long key;
    T value;

    public Data(Long key, T value) {
        this.key = key;
        this.value = value;
    }

    public Long getKey() {
        return this.key;
    }

    public T getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return "Data{" +
                "key=" + key +
                ", value=" + value +
                "}";
    }
}