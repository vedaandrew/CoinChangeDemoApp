package com.va.vendingmachine.coins.pojo;

/**
 * Enum class to hold coin value based on name
 */
public enum Coin {

    FIVE_PENCE(5), TEN_PENCE(10), TWENTY_PENCE(20), FIFTY_PENCE(50), HUNDRED_PENCE(100);

    private int value;

    Coin(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }


}
