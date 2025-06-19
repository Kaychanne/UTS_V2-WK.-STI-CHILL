package com.mycompany.mavenproject3.transaction;

import java.util.ArrayList;
import java.util.List;

import com.mycompany.mavenproject3.event.DataChangeEvent;
import com.mycompany.mavenproject3.event.DataChangeListener;

public class TransactionService {
    private static final List<Transaction> transactionList = new ArrayList<>();
    private static final List<DataChangeListener> listeners = new ArrayList<>();
    private static int currentId = 0;

    public static int getCurrentId() {
        return currentId;
    }

    public static int getNextId() {
        return ++currentId;
    }

    public static List<Transaction> getAllTransactions() {
        return transactionList;
    }

    public static Transaction getTransactionByIndex(int index) {
        return transactionList.get(index);
    }

    public static Transaction getTransactionById(int id) {
        int low = 0, high = transactionList.size() - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (transactionList.get(mid).getId() == id) {
                return transactionList.get(mid);
            } else if (transactionList.get(mid).getId() < id) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return null;
    }

    public static void addTransaction(Transaction transaction) {
        transactionList.add(transaction);
        fireDataChangeListener("add");
    }

    public static void updateTransaction(Transaction updatedTransaction) {
        for (int i = 0; i < transactionList.size(); i++) {
            Transaction current = transactionList.get(i);
            if (current.getCode().equals(updatedTransaction.getCode())) {
                transactionList.set(i, updatedTransaction);
                fireDataChangeListener("update");
                break;
            }
        }
    }

    public static void deleteTransactionByIndex(int index) {
        transactionList.remove(index);
        fireDataChangeListener("delete");
    }
    
    public static void deleteTransactionByCode(String code) {
        transactionList.removeIf(p -> p.getCode().equals(code));
        fireDataChangeListener("delete");
    }

    public static DataChangeListener addDataChangeListener(DataChangeListener listener) {
        listeners.add(listener);
        return listener;
    }

    public static void removeDataChangeListener(DataChangeListener listener) {
        listeners.remove(listener);
    }

    private static void fireDataChangeListener(String operation) {
        DataChangeEvent event = new DataChangeEvent(operation);
        for (DataChangeListener listener : listeners) {
            listener.onDataChanged(event);
        }
    }

}
