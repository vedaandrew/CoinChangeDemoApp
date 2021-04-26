package com.va.vendingmachine.coins.repository;

import java.util.Map;

public interface IInventoryRepository<T, T1> {

    void add(T t, T1 t1);

    default void put(T t, T1 t1) {
        getInventory().put(t, t1);
    }

    int getQuantity(T t);

    Map<T, T1> getInventory();

    void remove(T t, T1 t1);

    default void clear() {
        getInventory().clear();
    }

}
