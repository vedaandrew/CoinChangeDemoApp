package com.va.vendingmachine.coins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CoinChangeDemoApp {

    private static final Logger logger = LoggerFactory.getLogger(CoinChangeDemoApp.class);

    public static void main(String[] arg) {
        SpringApplication.run(CoinChangeDemoApp.class, arg);
        logger.info("############## Vending Machine - Coin change #############");
    }

}
