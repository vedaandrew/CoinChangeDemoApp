package com.va.vendingmachine.coins.repository;

import com.va.vendingmachine.coins.pojo.Coin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class CoinsRepositoryImpl implements IInventoryRepository<Coin, Integer> {

    private static final Logger logger = LoggerFactory.getLogger(CoinsRepositoryImpl.class);
    private final Map<Coin, Integer> coinsInventory;

    public CoinsRepositoryImpl() {
        this.coinsInventory = new HashMap<>();
    }

    /**
     * Method to get the coin quantity
     *
     * @param coin - Coin type
     * @return int - number of quantity for give coin
     */
    @Override
    public int getQuantity(Coin coin) {
        Integer value = coinsInventory.get(coin);
        return value == null ? 0 : value;
    }

    /**
     * Method to add coin in inventory
     *
     * @param coin - Coin type
     * @param qty  - number of quantity to add in inventory
     */
    @Override
    public void add(Coin coin, Integer qty) {
        int numberOfCoin = getQuantity(coin);
        numberOfCoin = qty + numberOfCoin;
        coinsInventory.put(coin, numberOfCoin);
        logger.info("coin added to inventory");
    }

    /**
     * Method to remove coin from inventory
     *
     * @param coin - Coin type
     * @param qty  - number of quantity to add in inventory
     */
    @Override
    public void remove(Coin coin, Integer qty) {
        int numberOfCoin = getQuantity(coin);
        numberOfCoin = numberOfCoin - qty;
        coinsInventory.put(coin, numberOfCoin);
        logger.info("coin removed from inventory");
    }

    /**
     * @return Map<Coin, Integer> - current summary of coins
     */
    @Override
    public Map<Coin, Integer> getInventory() {
        return coinsInventory;
    }

}
