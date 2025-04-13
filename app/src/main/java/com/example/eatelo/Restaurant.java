package com.example.eatelo;

public class Restaurant {
    private String name;
    private int elo;
    private String address;

    public Restaurant(String name, int elo) {
        this.name = name;
        this.elo = elo;
    }

    public Restaurant(String name, int elo, String address) {
        this.name = name;
        this.elo = elo;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public int getElo() {
        return elo;
    }
    public String getAddress() {
        return address;
    }
}
