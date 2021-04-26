package com.va.vendingmachine.coins.service;

import com.va.vendingmachine.coins.exception.InvalidInputException;
import com.va.vendingmachine.coins.exception.NotEnoughChangeException;
import com.va.vendingmachine.coins.pojo.Coin;
import com.va.vendingmachine.coins.pojo.CoinBox;
import com.va.vendingmachine.coins.repository.IInventoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CoinsChangeServiceImplTest {

    @Autowired
    private CoinsChangeServiceServiceImpl coinsTrackerService;

    @Autowired
    private IInventoryRepository<Coin, Integer> coinsRepository;

    @Test
    void testGetChangeCoins_whenNotEnoughChangeInventory() {
        //Set Inventory for testing
        List<CoinBox> updateCoins = getCoins(0, 0, 1, 2, 1);
        updateCoins.forEach(x -> coinsRepository.put(x.getCoin(), x.getQuantity()));

        //User Input
        List<CoinBox> userInputCoins = getCoins(0, 1, 2, 0, 0);

        assertThrows(NotEnoughChangeException.class, () -> {
            coinsTrackerService.getChangeCoins(userInputCoins);
        });

    }

    @Test
    void testGetChangeCoins() {
        //Set Inventory for testing
        List<CoinBox> updateCoins = getCoins(1, 2, 3, 5, 6);
        updateCoins.forEach(x -> coinsRepository.put(x.getCoin(), x.getQuantity()));
        Map<Coin, Integer> beforeInventory = coinsRepository.getInventory();

        //User Input
        List<CoinBox> userInputCoins = getCoins(0, 0, 1, 2, 0);

        //Expected result
        Map<Coin, Integer> exceptedCoinMap = convertToCoinMap(getCoins(0, 0, 1, 1, 2));
        Map<Coin, Integer> coinMap = coinsTrackerService.getChangeCoins(userInputCoins);
//        System.out.println("========actual========" + coinMap);
//        System.out.println("========expect========" + exceptedCoinMap);
        assertThat(exceptedCoinMap.entrySet(), equalTo(coinMap.entrySet()));

        //no change in the inventory coins
        //assert inventory
        Map<Coin, Integer> afterInventory = coinsRepository.getInventory();
        assertThat(beforeInventory.entrySet(), equalTo(afterInventory.entrySet()));
    }

    @Test
    void testAcceptAndDispenseCoins() {
        //Set Inventory for testing
        List<CoinBox> updateCoins = getCoins(1, 2, 3, 5, 6);
        updateCoins.forEach(x -> coinsRepository.put(x.getCoin(), x.getQuantity()));

        //User Input coins, Set in user Map
        coinsTrackerService.userMapCache = convertToCoinMap(getCoins(0, 0, 1, 2, 0));

        List<CoinBox> userAcceptedChangeCoins = getCoins(0, 0, 1, 1, 2);
        Map<Coin, Integer> exceptedCoinMap = userAcceptedChangeCoins.stream().collect(Collectors.toMap(CoinBox::getCoin, CoinBox::getQuantity));

        Map<Coin, Integer> coinMap = coinsTrackerService.acceptAndDispenseCoins(userAcceptedChangeCoins);
        assertThat(exceptedCoinMap.entrySet(), equalTo(coinMap.entrySet()));

        //Expected coins in inventory after the coins dispense
        Map<Coin, Integer> expectedInventoryMap = convertToCoinMap(getCoins(1, 2, 3, 6, 4));
        //assert inventory
        Map<Coin, Integer> inventoryMap = coinsRepository.getInventory();
        assertThat(expectedInventoryMap.entrySet(), equalTo(inventoryMap.entrySet()));

        assertTrue(coinsTrackerService.userMapCache.isEmpty());
        assertTrue(coinsTrackerService.coinsInventoryMapCache.isEmpty());
    }

    @Test
    void testAcceptAndDispenseCoins_whenUserRegisterAmtIsEmpty() {
        //Set Inventory for testing
        List<CoinBox> updateCoins = getCoins(1, 2, 3, 5, 6);
        updateCoins.forEach(x -> coinsRepository.put(x.getCoin(), x.getQuantity()));
        //clear User cash map
        coinsTrackerService.userMapCache.clear();
        List<CoinBox> userAcceptedCoins = getCoins(0, 0, 1, 1, 2);
        assertThrows(InvalidInputException.class, () -> {
            coinsTrackerService.acceptAndDispenseCoins(userAcceptedCoins);
        });
    }

    @Test
    void testInsertCoins() {
        List<CoinBox> coinBoxes = getCoins(1, 1, 2, 3, 4);
        String status = coinsTrackerService.loadCoinsToInventory(coinBoxes);
        assertEquals("Success", status);
    }

    private List<CoinBox> getCoins(int hundredPence, int fiftyPence,
                                   int twentyPence, int tenPence, int fivePence) {
        CoinBox coinBox1 = new CoinBox(Coin.HUNDRED_PENCE, hundredPence);
        CoinBox coinBox2 = new CoinBox(Coin.FIFTY_PENCE, fiftyPence);
        CoinBox coinBox3 = new CoinBox(Coin.TWENTY_PENCE, twentyPence);
        CoinBox coinBox4 = new CoinBox(Coin.TEN_PENCE, tenPence);
        CoinBox coinBox5 = new CoinBox(Coin.FIVE_PENCE, fivePence);
        return Arrays.asList(coinBox1, coinBox2, coinBox3, coinBox4, coinBox5);
    }

    private Map<Coin, Integer> convertToCoinMap(List<CoinBox> coinBoxes) {
        return coinBoxes.stream().collect(Collectors.toMap(CoinBox::getCoin, CoinBox::getQuantity));
    }

}