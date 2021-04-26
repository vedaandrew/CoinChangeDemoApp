package com.va.vendingmachine.coins.service;

import com.va.vendingmachine.coins.pojo.CoinBox;

import java.util.List;
import java.util.Map;

public interface ICoinsChangeService<T, T1> {

    String loadCoinsToInventory(List<CoinBox> coinBoxes);

    Map<T, T1> getInventoryStatus();

    Map<T, T1> getChangeCoins(List<CoinBox> coinBoxes);

    Map<T, T1> acceptAndDispenseCoins(List<CoinBox> coinBoxes);

    void clearCoinBox();
}
