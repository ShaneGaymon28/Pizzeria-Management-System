# DBMS-Clemson
This project was done at Clemson University as part of CPSC-4620 (Database Management Systems).

## Description

This was a 3 part project to design and create a database system to be used by the fictional company, Pizzas-R-Us that will track the day to day operations of the company.

### Part 1 - Database Design
  The first part of this project consisted of creating an enhanced ER diagram to base the database on.

### Part 2 - Database Implementation
  The second part required me to use the ERD from part 1 to create the SQL code to:
  
    1. CreateTables.sql - contains the necessary create statements to build the tables for the database
    
    2. PopulateData.sql - contains the necessary code to populate data in the database
    
    3. ViewTables.sql - contains a "SELECT * FROM ..." for each table to easily view all tables 
    
    4. DropTables.sql - contains code to drop each table in the database
    
    5. CreateViews.sql - contains code to create 3 views: ToppingPopularity, ProfitByPizza, and ProfitByOrderType

### Part 3 - Adding the database to a Java application
  The third part of this project involved creating a Java application that will interact with the database.
  
  The following features were added:
  
    1. Add a new order to the database
    2. View Customers
    3. Enter a new customer
    4. View Orders
    5. Mark an order as completed
    6. View Inventory Levels
    7. Add Inventory
    8. View Reports

## Technology
  - MySQL (https://www.mysql.com/downloads/)
  - Java
  - JDBC (https://dev.mysql.com/downloads/connector/j/)

