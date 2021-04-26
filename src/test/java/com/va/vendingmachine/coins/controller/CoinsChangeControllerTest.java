package com.va.vendingmachine.coins.controller;

import com.va.vendingmachine.coins.CoinChangeDemoApp;
import com.va.vendingmachine.coins.pojo.Coin;
import com.va.vendingmachine.coins.pojo.CoinBox;
import com.va.vendingmachine.coins.pojo.Coins;
import com.va.vendingmachine.coins.pojo.ErrorResponse;
import com.va.vendingmachine.coins.service.CoinsChangeServiceServiceImpl;
import com.va.vendingmachine.coins.repository.IInventoryRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CoinChangeDemoApp.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CoinsChangeControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CoinsChangeServiceServiceImpl coinsTrackerService;

    @Autowired
    private IInventoryRepository<Coin, Integer> coinsRepository;

    HttpHeaders headers = new HttpHeaders();

    private String baseUrl = "/api/v1/coins";

    @Test
    void testLoadInventory() {
        List<CoinBox> coinBoxes = getCoins(1, 1, 2, 3, 4);
        Coins coins = new Coins(coinBoxes);

        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Coins> entity = new HttpEntity<>(coins, headers);
        // Rest api
        ResponseEntity<String> response = restTemplate.exchange(createURLWithPort(baseUrl + "/loadCoins"),
                HttpMethod.PUT, entity, String.class);

        //assert Success response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Inventory Successfully Updated", response.getBody());
    }

    @Test
    void testGetChanges_whenTotalQuantityIsZero() {
        //User input
        List<CoinBox> coinBoxes = getCoins(0, 0, 0, 0, 0);
        Coins userCoins = new Coins(coinBoxes);

        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Coins> entity = new HttpEntity<>(userCoins, headers);
        //Rest Api
        ResponseEntity<ErrorResponse> response = restTemplate.exchange(createURLWithPort(baseUrl + "/getChanges"),
                HttpMethod.POST, entity, ErrorResponse.class);

        //assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse errRes = response.getBody();
        assert errRes != null;
        assertEquals("InvalidInputException", errRes.getMessage());
        assertEquals("Input param is incorrect, please try again", errRes.getDetail());
    }

    @Test
    void testGetChanges_whenAnyCoinQuantityIsNegative() {
        //User input
        List<CoinBox> coinBoxes = getCoins(0, 0, 0, -1, 0);
        Coins userCoins = new Coins(coinBoxes);

        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Coins> entity = new HttpEntity<>(userCoins, headers);
        //Rest Api
        ResponseEntity<ErrorResponse> response = restTemplate.exchange(createURLWithPort(baseUrl + "/getChanges"),
                HttpMethod.POST, entity, ErrorResponse.class);
        //asserts
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse errRes = response.getBody();
        assert errRes != null;
        assertEquals("InvalidInputException", errRes.getMessage());
        assertEquals("Input param is incorrect, please try again", errRes.getDetail());
    }

    @Test
    void testGetChanges_whenNotEnoughChangeInInventory() {
        //Set Inventory coins for testing
        List<CoinBox> updateCoins = getCoins(0, 0, 0, 1, 2);
        updateCoins.forEach(x -> coinsRepository.put(x.getCoin(), x.getQuantity()));

        //User input
        List<CoinBox> coinBoxes = getCoins(0, 2, 0, 1, 0);
        Coins userCoins = new Coins(coinBoxes);

        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Coins> entity = new HttpEntity<>(userCoins, headers);
        //Rest Api
        ResponseEntity<ErrorResponse> response = restTemplate.exchange(createURLWithPort(baseUrl + "/getChanges"),
                HttpMethod.POST, entity, ErrorResponse.class);

        //asserts
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        ErrorResponse errRes = response.getBody();
        assert errRes != null;
        assertEquals("NotEnoughChangeException", errRes.getMessage());
        assertEquals("NotEnoughChange in inventory for the given amount, Please try later", errRes.getDetail());
    }

    @Test
    void testGetChanges_whenResultMatches() {
        //Set Inventory coins for testing
        List<CoinBox> updateCoins = getCoins(1, 2, 3, 5, 6);
        updateCoins.forEach(x -> coinsRepository.put(x.getCoin(), x.getQuantity()));

        //User Input
        List<CoinBox> coinBoxes = getCoins(0, 0, 1, 2, 0);
        Coins userCoins = new Coins(coinBoxes);

        //Expected result
        List<CoinBox> exceptedCoinBoxes = getCoins(0, 0, 1, 1, 2);

        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Coins> entity = new HttpEntity<>(userCoins, headers);
        //Rest api
        ResponseEntity<Coins> response = restTemplate.exchange(createURLWithPort(baseUrl + "/getChanges"),
                HttpMethod.POST, entity, Coins.class);
        //assert
        Coins changeCoins = response.getBody();
        assert changeCoins != null;
        List<CoinBox> actualCoinBoxes = changeCoins.getCoinBuckets();

        //assert Success response
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Optional<Integer> actualTotal = actualCoinBoxes
                .stream().map(e -> e.getCoin().getValue() * e.getQuantity()).reduce(Integer::sum);
        Optional<Integer> expectedTotal = exceptedCoinBoxes
                .stream().map(e -> e.getCoin().getValue() * e.getQuantity()).reduce(Integer::sum);
        //assert totalAmt are same
        assertEquals(expectedTotal, actualTotal);
        //assert CoinsBuckets are same
        assertThat(exceptedCoinBoxes, Matchers.containsInAnyOrder(actualCoinBoxes.toArray()));
    }

    @Test
    void testAcceptChanges() {
        //Set Inventory coins for testing
        List<CoinBox> updateCoins = getCoins(1, 2, 3, 5, 6);
        updateCoins.forEach(x -> coinsRepository.put(x.getCoin(), x.getQuantity()));

        //Set userMap with entered coins for testing
        List<CoinBox> userCoinBoxes = getCoins(0, 0, 1, 2, 0);
        coinsTrackerService.userMapCache = userCoinBoxes.stream()
                .collect(Collectors.toMap(CoinBox::getCoin, CoinBox::getQuantity));

        //User accepted coins as input
        List<CoinBox> coinBoxes = getCoins(0, 0, 1, 2, 0);
        Coins coins = new Coins(coinBoxes);

        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Coins> entity = new HttpEntity<>(coins, headers);
        //Rest api
        ResponseEntity<String> response = restTemplate.exchange(createURLWithPort(baseUrl + "/acceptChanges"),
                HttpMethod.PUT, entity, String.class);
        //assert Success response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Change Successfully Dispensed", response.getBody());

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

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

}