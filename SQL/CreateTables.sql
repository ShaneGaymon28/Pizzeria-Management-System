-- Shane Gaymon -- 

CREATE SCHEMA IF NOT EXISTS PizzasRUs;
USE PizzasRUs;

CREATE TABLE IF NOT EXISTS topping (
	ToppingID INTEGER NOT NULL PRIMARY KEY UNIQUE AUTO_INCREMENT,
    ToppingName VARCHAR(255) NOT NULL UNIQUE,
    ToppingPrice FLOAT NOT NULL,
    ToppingCost FLOAT NOT NULL,
    ToppingCurrentInventory INT NOT NULL,
    ToppingSmall FLOAT NOT NULL,
    ToppingMedium FLOAT NOT NULL,
    ToppingLarge FLOAT NOT NULL,
    ToppingXLarge FLOAT NOT NULL
);

CREATE TABLE IF NOT EXISTS base_price (
	BasePizzaSize VARCHAR(20) NOT NULL,
    BaseCrustType VARCHAR(20) NOT NULL,
    BasePrice FLOAT NOT NULL, 
    BaseCost FLOAT NOT NULL,
    PRIMARY KEY(BasePizzaSize, BaseCrustType)
);

CREATE TABLE IF NOT EXISTS customer (
	CustomerID INT NOT NULL UNIQUE AUTO_INCREMENT PRIMARY KEY,
    CustomerFirstName VARCHAR(255) NOT NULL,
    CustomerLastName VARCHAR(255) NOT NULL,
    CustomerPhone VARCHAR(15) NOT NULL
);

CREATE TABLE IF NOT EXISTS orders (
	OrderID INT NOT NULL UNIQUE AUTO_INCREMENT PRIMARY KEY,
    OrderCost FLOAT NOT NULL,
    OrderPrice FLOAT NOT NULL,
    OrderTime DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    OrderType VARCHAR(20) NOT NULL,
    OrderIsComplete BOOl NOT NULL,
    OrderCustomerID INT,
    FOREIGN KEY (OrderCustomerID) REFERENCES customer(CustomerID)
);

CREATE TABLE IF NOT EXISTS pizza (
	PizzaID INT NOT NULL UNIQUE AUTO_INCREMENT PRIMARY KEY,
    PizzaIsCompleted BOOL NOT NULL,
    PizzaSize VARCHAR(255) NOT NULL,
    PizzaCrust VARCHAR(255) NOT NULL,
    PizzaOrderID INT NOT NULL,
    PizzaCost FLOAT NOT NULL,
    PizzaPrice FLOAT NOT NULL,
    PizzaDate DATE NOT NULL,
    FOREIGN KEY (PizzaSize, PizzaCrust) REFERENCES base_price(BasePizzaSize, BaseCrustType),
    FOREIGN KEY (PizzaOrderID) REFERENCES orders(OrderID)
);

CREATE TABLE IF NOT EXISTS discount (
	DiscountID VARCHAR(255) NOT NULL UNIQUE PRIMARY KEY,
    DiscountAmount FLOAT NOT NULL,
    DiscountIsPercentage BOOL NOT NULL
);


CREATE TABLE IF NOT EXISTS discount_pizza (
	DiscountPizzaID INT NOT NULL,
    DiscountPizzaDiscountID VARCHAR(255) NOT NULL,
    PRIMARY KEY(DiscountPizzaID, DiscountPizzaDiscountID),
    FOREIGN KEY(DiscountPizzaID) REFERENCES pizza(PizzaID),
    FOREIGN Key(DiscountPizzaDiscountID) REFERENCES discount(DiscountID)
);

CREATE TABLE IF NOT EXISTS discount_order (
	DiscountOrderID INT NOT NULL,
    DiscountOrderDiscountID VARCHAR(255) NOT NULL,
    PRIMARY KEY(DiscountOrderID, DiscountOrderDiscountID),
    FOREIGN KEY(DiscountOrderID) REFERENCES orders(OrderID),
    FOREIGN Key(DiscountOrderDiscountID) REFERENCES discount(DiscountID)
);

CREATE TABLE IF NOT EXISTS pizza_toppings (
	PizzaID INT NOT NULL, 
    ToppingsID INT NOT NULL,
    IsExtraToppings BOOL NOT NULL,
    PRIMARY KEY(PizzaID, ToppingsID),
    FOREIGN KEY(PizzaID) REFERENCES pizza(PizzaID),
    FOREIGN KEY(ToppingsID) REFERENCES topping(ToppingID)
);

CREATE TABLE IF NOT EXISTS pick_up_order (
	PickUpOrderID INT NOT NULL PRIMARY KEY,
    PickUpCustomerPhone VARCHAR(15) NOT NULL,
    FOREIGN KEY(PickUpOrderID) REFERENCES orders(OrderID)
);

CREATE TABLE IF NOT EXISTS delivery_order (
	DeliveryOrderID INT NOT NULL PRIMARY KEY,
    DeliveryAddress VARCHAR(255) NOT NULL,
    FOREIGN KEY(DeliveryOrderID) REFERENCES orders(OrderID)
);

CREATE TABLE IF NOT EXISTS dine_in_order (
	DineInOrderID INT NOT NULL PRIMARY KEY,
    TableNum INT NOT NULL,
    FOREIGN KEY(DineInOrderID) REFERENCES orders(OrderID)
);


