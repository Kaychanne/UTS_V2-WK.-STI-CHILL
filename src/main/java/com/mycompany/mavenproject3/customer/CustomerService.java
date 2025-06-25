package com.mycompany.mavenproject3.customer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mycompany.mavenproject3.event.DataChangeEvent;
import com.mycompany.mavenproject3.event.DataChangeListener;

public class CustomerService {
    private static final List<Customer> customerList = new ArrayList<>();
    private static final List<DataChangeListener> listeners = new ArrayList<>();
    private static final Map<Integer, Customer> customerMap = new HashMap<>();
    private static int currentId = 0;

    public static int getCurrentId() {
        return currentId;
    }

    public static int getNextId() {
        return ++currentId;
    }

    public static void init() {
        System.out.println("CustomerService.init()" + customerList.size());
        if (customerList.isEmpty()) {
            customerList.add(new Customer(getNextId(), "C001", "Cash"));
        }
    }

    public static List<Customer> getAllCustomers() {
        return customerList;
    }

    public static Customer getCustomerByIndex(int index) {
        return customerList.get(index);
    }

    public static Customer getCustomerById(int id) {
        int index = getCustomerIndexById(id);
        if (index != -1) {
            return customerList.get(index);
        }
        return null;
    }

    public static void addCustomer(Customer customer) {
        customerList.add(customer);
        fireDataChangeListener("add");
    }

    public static void updateCustomer(Customer updatedCustomer) {
        int index = getCustomerIndexById(updatedCustomer.getId());
        if (index != -1) {
            customerList.set(index, updatedCustomer);
            fireDataChangeListener("update");
        }
    }

    public static void deleteCustomerByIndex(int index) {
        customerList.remove(index);
        fireDataChangeListener("delete");
    }

    public static DataChangeListener addDataChangeListener(DataChangeListener listener) {
        listeners.add(listener);
        return listener;
    }

    public static void removeDataChangeListener(DataChangeListener listener) {
        listeners.remove(listener);
    }

    private static int getCustomerIndexById(int id) {
        int low = 0, high = customerList.size() - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (customerList.get(mid).getId() == id) {
                return mid;
            } else if (customerList.get(mid).getId() < id) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return -1;
    }

    private static void fireDataChangeListener(String operation) {
        DataChangeEvent event = new DataChangeEvent(operation);
        for (DataChangeListener listener : listeners) {
            listener.onDataChanged(event);
        }
    }

    public static List<Customer> getAll() {
        return new ArrayList<>(customerMap.values());
    }

    public static Customer get(int id) {
        return customerMap.get(id);
    }

    public static Customer add(Customer customer) {
        if (customer.getId() == 0) {
            customer.setId(getNextId());
        } else {
            if (customer.getId() > currentId) {
                currentId = customer.getId();
            }
        }
        customerList.add(customer);
        customerMap.put(customer.getId(), customer);
        fireDataChangeListener("add");
        return customer;
    }

    public static Customer update(int id, Customer customer) {
        customer.setId(id);
        int index = getCustomerIndexById(id);
        if (index != -1) {
            customerList.set(index, customer);
        }
        customerMap.put(id, customer);
        fireDataChangeListener("update");
        return customer;
    }

    public static boolean delete(int id) {
        int index = getCustomerIndexById(id);
        if (index != -1) {
            customerList.remove(index);
            customerMap.remove(id);
            fireDataChangeListener("delete");
            return true;
        }
        return false;
    }
}