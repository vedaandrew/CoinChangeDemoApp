package com.va.vendingmachine.coins.pojo;

import java.util.List;

/**
 * Coins class to hold list of coins
 */
public class Coins {

    public Coins() {
    }

    private List<CoinBox> coinBoxes;

    public Coins(List<CoinBox> coinBoxes) {
        this.coinBoxes = coinBoxes;
    }

    public List<CoinBox> getCoinBuckets() {
        return coinBoxes;
    }

    public void setCoinBuckets(List<CoinBox> coinBoxes) {
        this.coinBoxes = coinBoxes;
    }


}
