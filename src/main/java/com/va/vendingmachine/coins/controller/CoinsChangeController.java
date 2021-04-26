package com.va.vendingmachine.coins.controller;

import com.va.vendingmachine.coins.exception.InvalidInputException;
import com.va.vendingmachine.coins.exception.NotEnoughChangeException;
import com.va.vendingmachine.coins.pojo.Coin;
import com.va.vendingmachine.coins.pojo.CoinBox;
import com.va.vendingmachine.coins.pojo.Coins;
import com.va.vendingmachine.coins.service.ICoinsChangeService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = {"/api/v1/coins"})
public class CoinsChangeController {

    private static final Logger logger = LoggerFactory.getLogger(CoinsChangeController.class);

    @Autowired
    private ICoinsChangeService<Coin, Integer> coinsChange;

    @ApiOperation(value = "Api to load coins to inventory")
    @PutMapping(path = "/loadCoins", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> loadCoins(@RequestBody Coins coins) throws Exception {
        logger.info("load coins to inventory");
        String status = coinsChange.loadCoinsToInventory(coins.getCoinBuckets());
        logger.info("Inventory Update: {}", status);
        if (status.equalsIgnoreCase("Failed")) {
            throw new Exception("Coins inventory is empty");
        }
        return new ResponseEntity<>("Inventory Successfully Updated", HttpStatus.OK);
    }

    @ApiOperation(value = "Api to show the summary of change for given coins")
    @PostMapping(path = "/getChanges", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Coins> getChanges(@RequestBody Coins coins) {
        logger.info("get changes for given coins");
        int noQyt = coins.getCoinBuckets().stream().map(e -> e.getCoin().getValue() * e.getQuantity())
                .reduce(Integer::sum).orElse(0);
        boolean isAnyNegQty = coins.getCoinBuckets().stream().anyMatch(x -> x.getQuantity() < 0);
        if (noQyt == 0 || isAnyNegQty) {
            throw new InvalidInputException("Input param is incorrect, please try again");
        }
        Map<Coin, Integer> coinsMap = coinsChange.getChangeCoins(coins.getCoinBuckets());
        List<CoinBox> outputCoinsList = coinsMap.entrySet().stream()
                .map(c -> new CoinBox(c.getKey(), c.getValue())).collect(Collectors.toList());
        Coins coinsChange = new Coins(outputCoinsList);
        return new ResponseEntity<>(coinsChange, HttpStatus.OK);
    }

    @ApiOperation(value = "Api to accept the summary of change shown to user and dispense the coins")
    @PutMapping(path = "/acceptChanges", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> acceptChange(@RequestBody Coins coins) {
        logger.info("accept the coins and dispense");
        coinsChange.acceptAndDispenseCoins(coins.getCoinBuckets());
        logger.info("Change Successfully Dispensed");
        return new ResponseEntity<>("Change Successfully Dispensed", HttpStatus.OK);
    }

    @ApiOperation(value = "Api to get the current coins status from inventory, display summary of coins")
    @RequestMapping(path = "/getInventory", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Coins> getInventory() {
        logger.info("get inventory status");
        Map<Coin, Integer> coinsMap = coinsChange.getInventoryStatus();
        if (ObjectUtils.isEmpty(coinsMap)) {
            throw new NotEnoughChangeException("Coins inventory is empty");
        }
        List<CoinBox> outputCoinsList = coinsMap.entrySet().stream()
                .map(c -> new CoinBox(c.getKey(), c.getValue())).collect(Collectors.toList());
        Coins coinsInventory = new Coins(outputCoinsList);
        return new ResponseEntity<>(coinsInventory, HttpStatus.OK);
    }

    @ApiOperation(value = "Api to clear inventory")
    @DeleteMapping(path = "/clearInventory")
    public ResponseEntity<String> clearInventory() {
        logger.info("clear inventory");
        coinsChange.clearCoinBox();
        logger.info("Inventory cleared");
        return new ResponseEntity<>("Inventory cleared", HttpStatus.OK);
    }

}
