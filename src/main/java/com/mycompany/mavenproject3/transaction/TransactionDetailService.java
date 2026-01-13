package com.mycompany.mavenproject3.transaction;

import java.util.ArrayList;
import java.util.List;

import com.mycompany.mavenproject3.event.DataChangeEvent;
import com.mycompany.mavenproject3.event.DataChangeListener;

public class TransactionDetailService {
    private List<TransactionDetail> transactionDetailList;
    private List<DataChangeListener> listeners;
    private int currentId = 0;

    public TransactionDetailService() {
        transactionDetailList = new ArrayList<>();
        listeners = new ArrayList<>();
    }

    public TransactionDetailService(List<TransactionDetail> details, List<DataChangeListener> listeners, int currentId) {
        this.transactionDetailList = details;
        this.listeners = listeners;
        this.currentId = currentId;
    }

    public int getCurrentId() {
        return currentId;
    }

    public int getNextId() {
        return ++currentId;
    }

    public List<TransactionDetail> getAllTransactionDetails() {
        return transactionDetailList;
    }

    public TransactionDetail getTransactionDetailByIndex(int index) {
        return transactionDetailList.get(index);
    }

    public TransactionDetail getTransactionDetailById(int id) {
        int low = 0, high = transactionDetailList.size() - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (transactionDetailList.get(mid).getId() == id) {
                return transactionDetailList.get(mid);
            } else if (transactionDetailList.get(mid).getId() < id) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return null;
    }

    public void addTransactionDetail(TransactionDetail transactionDetail) {
        transactionDetailList.add(transactionDetail);
        fireDataChangeListener("add");
    }

    public void updateTransactionDetail(TransactionDetail updatedTransactionDetail) {
        for (int i = 0; i < transactionDetailList.size(); i++) {
            TransactionDetail current = transactionDetailList.get(i);
            if (current.getId() == updatedTransactionDetail.getId()) {
                transactionDetailList.set(i, updatedTransactionDetail);
                fireDataChangeListener("update");
                break;
            }
        }
    }

    public void deleteTransactionDetailByIndex(int index) {
        transactionDetailList.remove(index);
        fireDataChangeListener("delete");
    }

    public DataChangeListener addDataChangeListener(DataChangeListener listener) {
        listeners.add(listener);
        return listener;
    }

    public void removeDataChangeListener(DataChangeListener listener) {
        listeners.remove(listener);
    }

    private void fireDataChangeListener(String operation) {
        DataChangeEvent event = new DataChangeEvent(operation);
        for (DataChangeListener listener : listeners) {
            listener.onDataChanged(event);
        }
    }
}
