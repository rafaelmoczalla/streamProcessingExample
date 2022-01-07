package com.fonda.b6.examples.data;

public class ShopActivity {
    Long id;
    String name;
    Integer playerId;
    Double price;
    String activity;

    public ShopActivity(Long id, String name, Integer playerId, Double price, String activity) {
        this.id = id;
        this.name = name;
        this.playerId = playerId;
        this.price = price;
        this.activity = activity;
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Integer getPlayerId() {
        return this.playerId;
    }

    public Double getPrice() {
        return this.price;
    }

    public String getActivity() {
        return this.activity;
    }

    @Override
    public String toString() {
        return "ShopActivity{" +
                "id=" + id +
                ", name=" + name +
                ", playerId=" + playerId +
                ", price=" + price +
                ", activity=" + activity +
                "}";
    }
}