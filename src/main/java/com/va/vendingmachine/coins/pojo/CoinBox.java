package com.va.vendingmachine.coins.pojo;

import java.util.Objects;

/**
 * Class to hold coin and quantity
 */
public class CoinBox {

    private Coin coin;

    private int quantity;

    public CoinBox() {
    }

    public CoinBox(Coin coin, int quantity) {
        this.coin = coin;
        this.quantity = quantity;
    }

    public Coin getCoin() {
        return coin;
    }

    public void setCoin(Coin coin) {
        this.coin = coin;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "CoinBucket{" +
                "coin=" + coin +
                ", quantity=" + quantity +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoinBox that = (CoinBox) o;
        return quantity == that.quantity && coin == that.coin;
    }

    @Override
    public int hashCode() {
        return Objects.hash(coin, quantity);
    }
}
