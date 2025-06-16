# Changelog

## @xellanix
- [`DataChangeEvent`]
    - Add capability to store event basic data
- [`DataChangeListener`]
    - Add capability to do some actions, when it receive a data changed command
- [`Form`]
    - Remove CLOSE_ON_EXIT for non-root window
- [`Mavenproject3`]
    - Use event listener to change the banner items
- [`MoneyFormat`]
    - Add capability to format a double value to IDR format string
- [`ProductForm`]
    - Fix delete button bug
    - Move cancel button position
    - Remove redundant codes
    - Use `loadProductData` instead of manually updating the `tableModel`
    - Use `ProductService` id counter
    - Fix inconsistent text on save/add button
    - Reload data if the service is updated
- [`ProductService`]
    - Add event listener capability
    - Add methods to quickly get or remove an item by its index
    - Add id counter
- [`SalesForm`]
    - Use event listener to update the product list
    - Fix no product data on load
    - Migrate to multi-product sales mechanism
    - Add category filter
---

