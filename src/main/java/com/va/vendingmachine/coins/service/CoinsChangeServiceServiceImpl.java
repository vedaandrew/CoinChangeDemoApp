package com.va.vendingmachine.coins.service;

import com.va.vendingmachine.coins.exception.InvalidInputException;
import com.va.vendingmachine.coins.exception.NotEnoughChangeException;
import com.va.vendingmachine.coins.pojo.Coin;
import com.va.vendingmachine.coins.pojo.CoinBox;
import com.va.vendingmachine.coins.repository.IInventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CoinsChangeServiceServiceImpl implements ICoinsChangeService<Coin, Integer> {

    private static final Logger logger = LoggerFactory.getLogger(CoinsChangeServiceServiceImpl.class);

    @Autowired
    private IInventoryRepository<Coin, Integer> coinsRepository;

    public Map<Coin, Integer> userMapCache = new HashMap<>();
    public final Map<Coin, Integer> coinsInventoryMapCache = new HashMap<>();

    /**
     * Method to insert coins to coins inventory
     *
     * @param coinBoxes - - list of coins with quantity
     * @return string
     */
    @Override
    public String loadCoinsToInventory(List<CoinBox> coinBoxes) {
        logger.info("load coins to inventory");
        coinBoxes.forEach(ci -> coinsRepository.put(ci.getCoin(), ci.getQuantity()));
        return coinsRepository.getInventory().size() > 0 ? "Success" : "Failed";
    }

    private int getCurrentBalanceFromInventory() {
        logger.info("calculate current inventory balance");
        int currBal = 0;
        if (this.coinsRepository.getInventory().size() > 0) {
            currBal = this.coinsRepository.getInventory().entrySet()
                    .stream().map(e -> e.getKey().getValue() * e.getValue())
                    .reduce(Integer::sum).orElse(0);
            logger.info("current inventory total balance: {}", currBal);
        } else {
            logger.error("inventory is empty");
        }
        return currBal;
    }

    /**
     * Get the current coins status from coins inventory
     *
     * @return Map<Coin, Integer> - - Coin type, integer - refers to quantity
     */
    @Override
    public Map<Coin, Integer> getInventoryStatus() {
        return coinsRepository.getInventory();
    }

    /**
     * Service method to register user entered coins,
     * calculate and get summary of coins to dispense for the user entered total amount
     *
     * @param coinBoxes - list of coins with quantity
     * @return Map<Coin, Integer> - Coin type, integer - refers to quantity
     */
    @Override
    public Map<Coin, Integer> getChangeCoins(List<CoinBox> coinBoxes) {
        //add user entered coins to user cache map
        userMapCache = coinBoxes.stream().collect(Collectors.toMap(CoinBox::getCoin, CoinBox::getQuantity));
        List<Integer> cashCoinList = coinBoxes
                .stream().map(e -> e.getCoin().getValue() * e.getQuantity())
                .collect(Collectors.toList());
        Optional<Integer> totalAmt = cashCoinList.stream().reduce(Integer::sum);
        int userTotalEnteredAmt = totalAmt.orElse(0);
        logger.info("total amount user entered: {}", userTotalEnteredAmt);
        return getChanges(userTotalEnteredAmt);
    }

    /**
     * Method to clear the inventory map
     */
    @Override
    public void clearCoinBox() {
        coinsRepository.clear();
    }

    /**
     * Service method to accept summary of coins and dispense to the user
     * also update the coins inventory with dispensed and added coins quantity
     *
     * @param coinBoxes - list of coins with quantity
     * @return Map<Coin, Integer> - Coin type, integer - refers to quantity
     */
    @Override
    public Map<Coin, Integer> acceptAndDispenseCoins(List<CoinBox> coinBoxes) {
        Map<Coin, Integer> coinMap = coinBoxes.stream()
                .collect(Collectors.toMap(CoinBox::getCoin, CoinBox::getQuantity));
        logger.info("accept and dispense coins");
        if (userMapCache.isEmpty()) {
            logger.error("user entered amount is empty");
            throw new InvalidInputException("user entered amount is empty");
        }
        acceptAndUpdateInventory(coinMap);
        return coinMap;
    }

    private void acceptAndUpdateInventory(Map<Coin, Integer> coinIntegerMap) {
        //remove the coins to be dispensed from inventory
        coinIntegerMap.forEach((key, value) -> {
            if (value > 0) removeCoinFromInventory(key, value);
        });

        // add user entered coin to inventory
        userMapCache.forEach((key, value) -> {
            if (value > 0) addCoinToInventory(key, value);
        });
        userMapCache.clear();
        coinsInventoryMapCache.clear();
    }

    private void addCoinToInventory(Coin coin, Integer qty) {
        this.coinsRepository.add(coin, qty);
    }

    private void removeCoinFromInventory(Coin coin, Integer qty) {
        this.coinsRepository.remove(coin, qty);
    }

    private boolean isCoinExists(Coin coin) {
        return getCoinRemains(coin) > 0;
    }

    private int getCoinRemains(Coin coin) {
        Integer value = this.coinsInventoryMapCache.get(coin);
        return value == null ? 0 : value;
    }

    private void removeCoinFromCache(Coin coin, Integer qty) {
        int numberOfCoin = this.coinsInventoryMapCache.get(coin);
        numberOfCoin = numberOfCoin - qty;
        coinsInventoryMapCache.put(coin, numberOfCoin);
    }

    /**
     * Method calculate number of coins can be given to user based on current inventory status
     * ensure minimum of one coin dispense in each denomination based on remaining amount
     *
     * @param userEnterAmt - total of user entered coins
     * @return Map<Coin, Integer - summary of coins for give total amount, ensure
            * throws NotEnoughChangeException - throws when there is not enough change in inventory
     */
    private Map<Coin, Integer> getChanges(int userEnterAmt) throws NotEnoughChangeException {
        Map<Coin, Integer> coinMap;
        int currentBalance = getCurrentBalanceFromInventory();
        logger.info("userEnterAmt: {} currentBalance: {}", userEnterAmt, currentBalance);
        if (userEnterAmt > 0 && userEnterAmt <= currentBalance) {
            //init coin map
            coinMap = new HashMap<>();
            coinMap.put(Coin.HUNDRED_PENCE, 0);
            coinMap.put(Coin.FIFTY_PENCE, 0);
            coinMap.put(Coin.TWENTY_PENCE, 0);
            coinMap.put(Coin.TEN_PENCE, 0);
            coinMap.put(Coin.FIVE_PENCE, 0);

            coinsInventoryMapCache.putAll(coinsRepository.getInventory());

            int remainingAmt = userEnterAmt;
            while (remainingAmt > 0) {
                int hundredPVal = Coin.HUNDRED_PENCE.getValue();
                if (userEnterAmt != hundredPVal && remainingAmt >= hundredPVal && isCoinExists(Coin.HUNDRED_PENCE)) {
                    int qyt = Optional.ofNullable(coinMap.get(Coin.HUNDRED_PENCE)).orElse(0);
                    coinMap.put(Coin.HUNDRED_PENCE, qyt + 1);
                    removeCoinFromCache(Coin.HUNDRED_PENCE, 1);
                    remainingAmt = remainingAmt - hundredPVal;
                }

                int fiftyPVal = Coin.FIFTY_PENCE.getValue();
                if (userEnterAmt != fiftyPVal && remainingAmt >= fiftyPVal && isCoinExists(Coin.FIFTY_PENCE)) {
                    int qyt = Optional.ofNullable(coinMap.get(Coin.FIFTY_PENCE)).orElse(0);
                    coinMap.put(Coin.FIFTY_PENCE, qyt + 1);
                    removeCoinFromCache(Coin.FIFTY_PENCE, 1);
                    remainingAmt = remainingAmt - fiftyPVal;
                }

                int twentyPVal = Coin.TWENTY_PENCE.getValue();
                if (userEnterAmt != twentyPVal && remainingAmt >= twentyPVal && isCoinExists(Coin.TWENTY_PENCE)) {
                    int qyt = Optional.ofNullable(coinMap.get(Coin.TWENTY_PENCE)).orElse(0);
                    coinMap.put(Coin.TWENTY_PENCE, qyt + 1);
                    removeCoinFromCache(Coin.TWENTY_PENCE, 1);
                    remainingAmt = remainingAmt - twentyPVal;
                }

                int tenPVal = Coin.TEN_PENCE.getValue();
                if (userEnterAmt != tenPVal && remainingAmt >= tenPVal && isCoinExists(Coin.TEN_PENCE)) {
                    int qyt = Optional.ofNullable(coinMap.get(Coin.TEN_PENCE)).orElse(0);
                    coinMap.put(Coin.TEN_PENCE, qyt + 1);
                    removeCoinFromCache(Coin.TEN_PENCE, 1);
                    remainingAmt = remainingAmt - tenPVal;
                }

                int fivePVal = Coin.FIVE_PENCE.getValue();
                if (userEnterAmt != fivePVal && remainingAmt >= fivePVal && isCoinExists(Coin.FIVE_PENCE)) {
                    int qyt = Optional.ofNullable(coinMap.get(Coin.FIVE_PENCE)).orElse(0);
                    coinMap.put(Coin.FIVE_PENCE, qyt + 1);
                    removeCoinFromCache(Coin.FIVE_PENCE, 1);
                    remainingAmt = remainingAmt - fivePVal;
                }
            }
        } else {
            logger.error("NotEnoughChange in inventory for the given amount");
            throw new NotEnoughChangeException("NotEnoughChange in inventory for the given amount, Please try later");
        }
        logger.info("change coins denominations: {}", coinMap);
        return coinMap;
    }

}
