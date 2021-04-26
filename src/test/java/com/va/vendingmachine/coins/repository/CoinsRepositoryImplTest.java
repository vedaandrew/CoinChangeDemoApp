package com.va.vendingmachine.coins.repository;

import com.va.vendingmachine.coins.pojo.Coin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class CoinsRepositoryImplTest {

    @Autowired
    private CoinsRepositoryImpl coinsRepository;

    @BeforeEach
    public void setUp() {
        Map<Coin, Integer> coinMap = coinsRepository.getInventory();
        coinMap.put(Coin.HUNDRED_PENCE, 1);
        coinMap.put(Coin.FIFTY_PENCE, 1);
        coinMap.put(Coin.TWENTY_PENCE, 2);
        coinMap.put(Coin.TEN_PENCE, 5);
        coinMap.put(Coin.FIVE_PENCE, 5);
    }

    @Test
    void testGetQuantity() {
        int qyt = coinsRepository.getQuantity(Coin.FIFTY_PENCE);
        assertEquals(1, qyt);
    }

    @Test
    void testAdd() {
        coinsRepository.add(Coin.TWENTY_PENCE, 1);
        assertEquals(3, coinsRepository.getQuantity(Coin.TWENTY_PENCE));
    }

    @Test
    void testRemove() {
        coinsRepository.remove(Coin.TEN_PENCE, 2);
        assertEquals(3, coinsRepository.getQuantity(Coin.TEN_PENCE));
    }

    @Test
    void testGetInventory() {
        Map<Coin, Integer> coinMap = coinsRepository.getInventory();
        assertNotNull(coinMap);
    }
}