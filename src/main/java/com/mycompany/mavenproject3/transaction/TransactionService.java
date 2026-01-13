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

    public static int getIndexById(int id) {
        int low = 0, high = transactionList.size() - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (transactionList.get(mid).getId() == id) {
                return mid;
            } else if (transactionList.get(mid).getId() < id) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return -1;
    }

    public static Transaction getTransactionByIndex(int index) {
        return transactionList.get(index);
    }

    public static Transaction getTransactionById(int id) {
        var index = getIndexById(id);
        if (index != -1) {
            return getTransactionByIndex(index);
        }
        return null;
    }

    public static Transaction addTransaction(Transaction transaction) {
        transactionList.add(transaction);
        fireDataChangeListener("add");
        return transaction;
    }

    public static Transaction updateTransaction(Transaction updatedTransaction) {
        return updateTransactionById(updatedTransaction.getId(), updatedTransaction);
    }

    public static Transaction updateTransactionById(int id, Transaction transaction) {
        var index = getIndexById(id);
        if (index != -1) {
            transactionList.set(index, transaction);
            fireDataChangeListener("update");
            return transaction;
        }
        return null;
    }

    public static boolean deleteTransactionByIndex(int index) {
        transactionList.remove(index);
        fireDataChangeListener("delete");
        return true;
    }

    public static boolean deleteTransactionById(int id) {
        var index = getIndexById(id);
        if (index != -1) {
            return deleteTransactionByIndex(index);
        }
        return false;
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
