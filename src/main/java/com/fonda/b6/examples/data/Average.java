package com.fonda.b6.examples.data;

public class Average {
    Long key;
    Long count;
    Double sum;

    public Average(Long key, Long count, Double sum) {
        this.key = key;
        this.count = count;
        this.sum = sum;
    }

    public Long getKey() {
        return this.key;
    }

    public void addValues(Long count, Double sum) {
        this.count += count;
        this.sum += sum;
    }

    public Double getAverage() {
        return this.sum / this.count;
    }

    @Override
    public String toString() {
        return "Average{" +
                "key=" + key +
                ", count=" + count +
                ", sum=" + sum +
                ", avg=" + this.getAverage() +
                '}';
    }
}