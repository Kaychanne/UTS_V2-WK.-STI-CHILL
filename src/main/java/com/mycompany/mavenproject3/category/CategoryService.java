package com.mycompany.mavenproject3.category;

import java.util.ArrayList;
import java.util.List;

import com.mycompany.mavenproject3.event.DataChangeEvent;
import com.mycompany.mavenproject3.event.DataChangeListener;

public class CategoryService {
    private static final List<Category> categoryList = new ArrayList<>();
    private static final List<DataChangeListener> listeners = new ArrayList<>();
    private static int currentId = 0;

    public static int getCurrentId() {
        return currentId;
    }

    public static int getNextId() {
        return ++currentId;
    }

    public static void init() {
        if (categoryList.isEmpty()) {
            categoryList.add(new Category(getNextId(), "Coffee"));
            categoryList.add(new Category(getNextId(), "Dairy"));
            categoryList.add(new Category(getNextId(), "Juice"));
            categoryList.add(new Category(getNextId(), "Soda"));
            categoryList.add(new Category(getNextId(), "Tea"));
        }
    }

    public static List<Category> getAllCategories() {
        return categoryList;
    }

    public static Category getCategoryByIndex(int index) {
        return categoryList.get(index);
    }

    public static Category getCategoryById(int id) {
        int index = getCategoryIndexById(id);
        if (index != -1) {
            return categoryList.get(index);
        }
        return null;
    }

    public static void addCategory(Category category) {
        categoryList.add(category);
        fireDataChangeListener("add");
    }

    public static void updateCategory(Category updatedCategory) {
        int index = getCategoryIndexById(updatedCategory.getId());
        if (index != -1) {
            categoryList.set(index, updatedCategory);
            fireDataChangeListener("update");
        }
    }

    public static void deleteCategoryByIndex(int index) {
        categoryList.remove(index);
        fireDataChangeListener("delete");
    }

    public static DataChangeListener addDataChangeListener(DataChangeListener listener) {
        listeners.add(listener);
        return listener;
    }

    public static void removeDataChangeListener(DataChangeListener listener) {
        listeners.remove(listener);
    }

    private static int getCategoryIndexById(int id) {
        int low = 0, high = categoryList.size() - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (categoryList.get(mid).getId() == id) {
                return mid;
            } else if (categoryList.get(mid).getId() < id) {
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
}
