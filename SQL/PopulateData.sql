-- Shane Gaymon -- 

use PizzasRUs;

-- toppings --

INSERT INTO topping 
	VALUES 
		(1, 'Pepperoni', 1.25, 0.2, 100, 2, 2.75, 3.5, 4.5),
		(2, 'Sausage', 1.25, 0.15, 100, 2.5, 3, 3.5, 4.25),
		(3, 'Ham', 1.5, 0.15, 78, 2, 2.5, 3.25, 4),
		(4, 'Chicken', 1.75, 0.25, 56, 1.5, 2, 2.25, 3),
		(5, 'Green Pepper', 0.5, 0.02, 79, 1, 1.5, 2, 2.5),
		(6, 'Onion', 0.5, 0.02, 85, 1, 1.5, 2, 2.75),
		(7, 'Roma Tomato', 0.75, 0.03, 86, 2, 3, 3.5, 4.5),
		(8, 'Mushrooms', 0.75, 0.1, 52, 1.5, 2, 2.5, 3),
		(9, 'Black Olives', 0.6, 0.1, 39, 0.75, 1, 1.5, 2),
		(10, 'Pineapple', 1, 0.25, 15, 1, 1.25, 1.75, 2),
		(11, 'Jalapenos', 0.5, 0.05, 64, 0.5, 0.75, 1.25, 1.75),
		(12, 'Banana Peppers', 0.5, 0.05, 36, 0.6, 1, 1.3, 1.75),
		(13, 'Regular Cheese', 1.5, 0.12, 250, 2, 3.5, 5, 7),
		(14, 'Four Cheese Blend', 2, 0.15, 150, 2, 3.5, 5, 7),
		(15, 'Feta Cheese', 2, 0.18, 75, 1.75, 3, 4, 5.5),
		(16, 'Goat Cheese', 2, 0.2, 54, 1.6, 2.75, 4, 5.5),
		(17, 'Bacon', 1.5, 0.25, 89, 1, 1.5, 2, 3);

-- base_price -- 
INSERT INTO base_price 
	VALUES 
		('small', 'Thin', 3, 0.5),
		('small', 'Original', 3, 0.75),
		('small', 'Pan', 3.5, 1),
		('small', 'Gluten-Free', 4, 2),
		('medium', 'Thin', 5, 1),
		('medium', 'Original', 5, 1.5),
		('medium', 'Pan', 6, 2.25),
		('medium', 'Gluten-Free', 6.25, 3),
		('large', 'Thin', 8, 1.25),
		('large', 'Original', 8, 2),
		('large', 'Pan', 9, 3),
		('large', 'Gluten-Free', 9.5, 4),
		('x-large', 'Thin', 10, 2),
		('x-large', 'Original', 10, 3),
		('x-large', 'Pan', 11.5, 4.5),
		('x-large', 'Gluten-Free', 12.5, 6);

-- discount --
INSERT INTO discount 
	VALUES 
		('Employee', 15, 1),
		('Lunch Special Medium', 1.00, 0),
		('Lunch Special Large', 2.00, 0),
		('Specialty Pizza', 1.50, 0),
		('Gameday Special', 20, 1);


-- order #1 -- 

INSERT INTO orders (OrderCost, OrderPrice, OrderTime, OrderType, OrderIsComplete, OrderCustomerID)
	VALUES (0, 0, '2023-03-05 12:03:00', 'DINEIN', true, null);
    
INSERT INTO dine_in_order VALUES ((SELECT MAX(OrderID) FROM orders), 14);

INSERT INTO pizza (PizzaIsCompleted, PizzaSize, PizzaCrust, PizzaOrderID, PizzaCost, PizzaPrice, PizzaDate)
	VALUES (1, 'large', 'Thin', (SELECT MAX(OrderID) FROM orders), 3.68, 13.50, '2023-03-05');
    
INSERT INTO pizza_toppings 
	VALUES 
		((SELECT MAX(PizzaID) FROM pizza), (SELECT ToppingID FROM topping WHERE ToppingName = 'Regular Cheese'), 1),
		((SELECT MAX(PizzaID) FROM pizza), (SELECT ToppingID FROM topping WHERE ToppingName = 'Pepperoni'), 0),    
		((SELECT MAX(PizzaID) FROM pizza), (SELECT ToppingID FROM topping WHERE ToppingName = 'Sausage'), 0);
    
INSERT INTO discount_pizza 
	VALUES ((SELECT MAX(PizzaID) FROM pizza), (SELECT DiscountID FROM discount WHERE DiscountID = 'Lunch Special Large'));
    
UPDATE orders
	SET OrderPrice = (SELECT SUM(PizzaPrice) FROM pizza WHERE PizzaOrderID = (SELECT MAX(PizzaOrderID) FROM pizza)),
		OrderCost = (SELECT SUM(PizzaCost) FROM pizza WHERE PizzaOrderID = (SELECT MAX(PizzaOrderID) FROM pizza))
	WHERE OrderID = (SELECT MAX(PizzaOrderID) FROM pizza);


-- order #2 --
INSERT INTO orders (OrderCost, OrderPrice, OrderTime, OrderType, OrderIsComplete, OrderCustomerID)
	VALUES (0, 0, '2023-03-03 12:05:00', 'DINEIN', true, null);
    
INSERT INTO dine_in_order VALUES ((SELECT MAX(OrderID) FROM orders), 4);

INSERT INTO pizza (PizzaIsCompleted, PizzaSize, PizzaCrust, PizzaOrderID, PizzaCost, PizzaPrice, PizzaDate)
	VALUES (1, 'medium', 'Pan', (SELECT MAX(OrderID) FROM orders), 3.23, 10.60, '2023-03-03');
    
INSERT INTO pizza_toppings 
	VALUES 
		((SELECT MAX(PizzaID) FROM pizza), (SELECT ToppingID FROM topping WHERE ToppingName = 'Feta Cheese'), 0),
		((SELECT MAX(PizzaID) FROM pizza), (SELECT ToppingID FROM topping WHERE ToppingName = 'Black Olives'), 0),    
		((SELECT MAX(PizzaID) FROM pizza), (SELECT ToppingID FROM topping WHERE ToppingName = 'Roma Tomato'), 0),
        ((SELECT MAX(PizzaID) FROM pizza), (SELECT ToppingID FROM topping WHERE ToppingName = 'Mushrooms'), 0),
        ((SELECT MAX(PizzaID) FROM pizza), (SELECT ToppingID FROM topping WHERE ToppingName = 'Banana Peppers'), 0);
    
INSERT INTO discount_pizza 
	VALUES 
		((SELECT MAX(PizzaID) FROM pizza), (SELECT DiscountID FROM discount WHERE DiscountID = 'Lunch Special Medium')),
        ((SELECT MAX(PizzaID) FROM pizza), (SELECT DiscountID FROM discount WHERE DiscountID = 'Specialty Pizza'));
    
INSERT INTO pizza (PizzaIsCompleted, PizzaSize, PizzaCrust, PizzaOrderID, PizzaCost, PizzaPrice, PizzaDate)
	VALUES (1, 'small', 'Original', (SELECT MAX(OrderID) FROM orders), 1.40, 6.75, '2023-03-03');
    
INSERT INTO pizza_toppings 
	VALUES 
		((SELECT MAX(PizzaID) FROM pizza), (SELECT ToppingID FROM topping WHERE ToppingName = 'Regular Cheese'), 0),
        ((SELECT MAX(PizzaID) FROM pizza), (SELECT ToppingID FROM topping WHERE ToppingName = 'Chicken'), 0),
        ((SELECT MAX(PizzaID) FROM pizza), (SELECT ToppingID FROM topping WHERE ToppingName = 'Banana Peppers'), 0);
        
UPDATE orders
	SET OrderPrice = (SELECT SUM(PizzaPrice) FROM pizza WHERE PizzaOrderID = (SELECT MAX(PizzaOrderID) FROM pizza)),
		OrderCost = (SELECT SUM(PizzaCost) FROM pizza WHERE PizzaOrderID = (SELECT MAX(PizzaOrderID) FROM pizza))
	WHERE OrderID = (SELECT MAX(PizzaOrderID) FROM pizza);


-- order #3 -- 
INSERT INTO customer (CustomerFirstName, CustomerLastName, CustomerPhone)
	VALUES ('Ellis', 'Beck', '864-254-5861');

INSERT INTO orders (OrderCost, OrderPrice, OrderTime, OrderType, OrderIsComplete, OrderCustomerID)
	VALUES (0, 0, '2023-03-03 21:30:00', 'PICKUP', true, (SELECT MAX(CustomerID) FROM customer));
    
INSERT INTO pick_up_order VALUES ((SELECT MAX(OrderID) FROM orders), '864-254-5861');
    
INSERT INTO pizza (PizzaIsCompleted, PizzaSize, PizzaCrust, PizzaOrderID, PizzaCost, PizzaPrice, PizzaDate)
	VALUES (1, 'large', 'Original', (SELECT MAX(OrderID) FROM orders), 3.30, 10.75, '2023-03-03');
    
INSERT INTO pizza_toppings
	VALUES 
		((SELECT MAX(PizzaID) FROM pizza), (SELECT ToppingID FROM topping WHERE ToppingName = 'Regular Cheese'), 0),
        ((SELECT MAX(PizzaID) FROM pizza), (SELECT ToppingID FROM topping WHERE ToppingName = 'Pepperoni'), 0);
        
INSERT INTO pizza (PizzaIsCompleted, PizzaSize, PizzaCrust, PizzaOrderID, PizzaCost, PizzaPrice, PizzaDate)
	VALUES (1, 'large', 'Original', (SELECT MAX(OrderID) FROM orders), 3.30, 10.75, '2023-03-03');
    
INSERT INTO pizza_toppings
	VALUES 
		((SELECT MAX(PizzaID) FROM pizza), (SELECT ToppingID FROM topping WHERE ToppingName = 'Regular Cheese'), 0),
        ((SELECT MAX(PizzaID) FROM pizza), (SELECT ToppingID FROM topping WHERE ToppingName = 'Pepperoni'), 0);
        
INSERT INTO pizza (PizzaIsCompleted, PizzaSize, PizzaCrust, PizzaOrderID, PizzaCost, PizzaPrice, PizzaDate)
	VALUES (1, 'large', 'Original', (SELECT MAX(OrderID) FROM orders), 3.30, 10.75, '2023-03-03');
    
INSERT INTO pizza_toppings
	VALUES 
		((SELECT MAX(PizzaID) FROM pizza), (SELECT ToppingID FROM topping WHERE ToppingName = 'Regular Cheese'), 0),
        ((SELECT MAX(PizzaID) FROM pizza), (SELECT ToppingID FROM topping WHERE ToppingName = 'Pepperoni'), 0);
        
INSERT INTO pizza (PizzaIsCompleted, PizzaSize, PizzaCrust, PizzaOrderID, PizzaCost, PizzaPrice, PizzaDate)
	VALUES (1, 'large', 'Original', (SELECT MAX(OrderID) FROM orders), 3.30, 10.75, '2023-03-03');
    
INSERT INTO pizza_toppings
	VALUES 
		((SELECT MAX(PizzaID) FROM pizza), (SELECT ToppingID FROM topping WHERE ToppingName = 'Regular Cheese'), 0),
        ((SELECT MAX(PizzaID) FROM pizza), (SELECT ToppingID FROM topping WHERE ToppingName = 'Pepperoni'), 0);
        
INSERT INTO pizza (PizzaIsCompleted, PizzaSize, PizzaCrust, PizzaOrderID, PizzaCost, PizzaPrice, PizzaDate)
	VALUES (1, 'large', 'Original', (SELECT MAX(OrderID) FROM orders), 3.30, 10.75, '2023-03-03');
    
INSERT INTO pizza_toppings
	VALUES 
		((SELECT MAX(PizzaID) FROM pizza), (SELECT ToppingID FROM topping WHERE ToppingName = 'Regular Cheese'), 0),
        ((SELECT MAX(PizzaID) FROM pizza), (SELECT ToppingID FROM topping WHERE ToppingName = 'Pepperoni'), 0);
        
INSERT INTO pizza (PizzaIsCompleted, PizzaSize, PizzaCrust, PizzaOrderID, PizzaCost, PizzaPrice, PizzaDate)
	VALUES (1, 'large', 'Original', (SELECT MAX(OrderID) FROM orders), 3.30, 10.75, '2023-03-03');
    
INSERT INTO pizza_toppings
	VALUES 
		((SELECT MAX(PizzaID) FROM pizza), (SELECT ToppingID FROM topping WHERE ToppingName = 'Regular Cheese'), 0),
        ((SELECT MAX(PizzaID) FROM pizza), (SELECT ToppingID FROM topping WHERE ToppingName = 'Pepperoni'), 0);
        

        
UPDATE orders
	SET OrderPrice = (SELECT SUM(PizzaPrice) FROM pizza WHERE PizzaOrderID = (SELECT MAX(PizzaOrderID) FROM pizza)),
		OrderCost = (SELECT SUM(PizzaCost) FROM pizza WHERE PizzaOrderID = (SELECT MAX(PizzaOrderID) FROM pizza))
	WHERE OrderID = (SELECT MAX(PizzaOrderID) FROM pizza);
    

    
    
-- order #4 -- 

INSERT INTO orders (OrderCost, OrderPrice, OrderTime, OrderType, OrderIsComplete, OrderCustomerID)
	VALUES (0, 0, '2023-03-05 17:11:00', 'DELIVERY', true, (SELECT CustomerID FROM customer WHERE CustomerFirstName = 'Ellis' AND CustomerLastName = 'Beck'));
    
INSERT INTO delivery_order VALUES ((SELECT MAX(OrderID) FROM orders), '115 Party Blvd, Anderson SC, 29621');

INSERT INTO pizza (PizzaIsCompleted, PizzaSize, PizzaCrust, PizzaOrderID, PizzaCost, PizzaPrice, PizzaDate)
	VALUES (1, 'x-large', 'Original', (SELECT MAX(OrderID) FROM orders), 5.59, 14.50, '2023-03-05');
    
INSERT INTO pizza_toppings
	VALUES 
		((SELECT MAX(PizzaID) FROM pizza), (SELECT ToppingID FROM topping WHERE ToppingName = 'Four Cheese Blend'), 0),
        ((SELECT MAX(PizzaID) FROM pizza), (SELECT ToppingID FROM topping WHERE ToppingName = 'Pepperoni'), 0),
        ((SELECT MAX(PizzaID) FROM pizza), (SELECT ToppingID FROM topping WHERE ToppingName = 'Sausage'), 0);
      
INSERT INTO pizza (PizzaIsCompleted, PizzaSize, PizzaCrust, PizzaOrderID, PizzaCost, PizzaPrice, PizzaDate)
	VALUES (1, 'x-large', 'Original', (SELECT MAX(OrderID) FROM orders), 5.59, 17.00, '2023-03-05');
    
INSERT INTO pizza_toppings
	VALUES 
		((SELECT MAX(PizzaID) FROM pizza), (SELECT ToppingID FROM topping WHERE ToppingName = 'Four Cheese Blend'), 0),
        ((SELECT MAX(PizzaID) FROM pizza), (SELECT ToppingID FROM topping WHERE ToppingName = 'Ham'), 1),
        ((SELECT MAX(PizzaID) FROM pizza), (SELECT ToppingID FROM topping WHERE ToppingName = 'Pineapple'), 1);
        
INSERT INTO discount_pizza 
	VALUES 
		((SELECT MAX(PizzaID) FROM pizza), (SELECT DiscountID FROM discount WHERE DiscountID = 'Specialty Pizza'));
        
INSERT INTO pizza (PizzaIsCompleted, PizzaSize, PizzaCrust, PizzaOrderID, PizzaCost, PizzaPrice, PizzaDate)
	VALUES (1, 'x-large', 'Original', (SELECT MAX(OrderID) FROM orders), 5.68, 14.00, '2023-03-05');
    
INSERT INTO pizza_toppings
	VALUES 
		((SELECT MAX(PizzaID) FROM pizza), (SELECT ToppingID FROM topping WHERE ToppingName = 'Four Cheese Blend'), 0),
        ((SELECT MAX(PizzaID) FROM pizza), (SELECT ToppingID FROM topping WHERE ToppingName = 'Jalapenos'), 0),
        ((SELECT MAX(PizzaID) FROM pizza), (SELECT ToppingID FROM topping WHERE ToppingName = 'Bacon'), 0);
        
INSERT INTO discount_order VALUES ((SELECT MAX(OrderID) FROM orders), (SELECT DiscountID FROM discount WHERE DiscountID = 'Gameday Special'));

UPDATE orders
	SET OrderPrice = (SELECT SUM(PizzaPrice) FROM pizza WHERE PizzaOrderID = (SELECT MAX(PizzaOrderID) FROM pizza)),
		OrderCost = (SELECT SUM(PizzaCost) FROM pizza WHERE PizzaOrderID = (SELECT MAX(PizzaOrderID) FROM pizza))
	WHERE OrderID = (SELECT MAX(PizzaOrderID) FROM pizza);
    
-- order #5 --
INSERT INTO customer (CustomerFirstName, CustomerLastName, CustomerPhone)
	VALUES ('Kurt', 'McKinney', '864-474-9953');
    
INSERT INTO orders (OrderCost, OrderPrice, OrderTime, OrderType, OrderIsComplete, OrderCustomerID)
	VALUES (0, 0, '2023-03-02 17:30:00', 'PICKUP', true, (SELECT MAX(CustomerID) FROM customer));
    
INSERT INTO pick_up_order VALUES ((SELECT MAX(OrderID) FROM orders), '864-254-5861');

-- error happens here for some reason --
INSERT INTO pizza (PizzaIsCompleted, PizzaSize, PizzaCrust, PizzaOrderID, PizzaCost, PizzaPrice, PizzaDate)
	VALUES (1, 'x-large', 'Gluten-Free', (SELECT MAX(OrderID) FROM orders), 7.85, 16.85, '2023-03-02');
    
INSERT INTO pizza_toppings
	VALUES 
		((SELECT MAX(PizzaID) FROM pizza), (SELECT ToppingID FROM topping WHERE ToppingName = 'Green Pepper'), 0),
        ((SELECT MAX(PizzaID) FROM pizza), (SELECT ToppingID FROM topping WHERE ToppingName = 'Onion'), 0),
        ((SELECT MAX(PizzaID) FROM pizza), (SELECT ToppingID FROM topping WHERE ToppingName = 'Roma Tomato'), 0),
        ((SELECT MAX(PizzaID) FROM pizza), (SELECT ToppingID FROM topping WHERE ToppingName = 'Mushrooms'), 0),
        ((SELECT MAX(PizzaID) FROM pizza), (SELECT ToppingID FROM topping WHERE ToppingName = 'Black Olives'), 0),
        ((SELECT MAX(PizzaID) FROM pizza), (SELECT ToppingID FROM topping WHERE ToppingName = 'Goat Cheese'), 0);
        
INSERT INTO discount_pizza 
	VALUES 
		((SELECT MAX(PizzaID) FROM pizza), (SELECT DiscountID FROM discount WHERE DiscountID = 'Specialty Pizza'));
        
UPDATE orders
	SET OrderPrice = (SELECT SUM(PizzaPrice) FROM pizza WHERE PizzaOrderID = (SELECT MAX(PizzaOrderID) FROM pizza)),
		OrderCost = (SELECT SUM(PizzaCost) FROM pizza WHERE PizzaOrderID = (SELECT MAX(PizzaOrderID) FROM pizza))
	WHERE OrderID = (SELECT MAX(PizzaOrderID) FROM pizza);
    
-- order #6 --
INSERT INTO customer (CustomerFirstName, CustomerLastName, CustomerPhone)
	VALUES ('Calvin', 'Sanders', '864-232-8944');

INSERT INTO orders (OrderCost, OrderPrice, OrderTime, OrderType, OrderIsComplete, OrderCustomerID)
	VALUES (0, 0, '2023-03-02 18:17:00', 'DELIVERY', true, (SELECT MAX(CustomerID) FROM customer));
    
INSERT INTO delivery_order VALUES ((SELECT MAX(OrderID) FROM orders), '6745 Wessex St Anderson SC 29621');
    
INSERT INTO pizza (PizzaIsCompleted, PizzaSize, PizzaCrust, PizzaOrderID, PizzaCost, PizzaPrice, PizzaDate)
	VALUES (1, 'large', 'Thin', (SELECT MAX(OrderID) FROM orders), 3.20, 13.25, '2023-03-02');
    
INSERT INTO pizza_toppings
	VALUES 
		((SELECT MAX(PizzaID) FROM pizza), (SELECT ToppingID FROM topping WHERE ToppingName = 'Four Cheese Blend'), 1),
        ((SELECT MAX(PizzaID) FROM pizza), (SELECT ToppingID FROM topping WHERE ToppingName = 'Chicken'), 0),
        ((SELECT MAX(PizzaID) FROM pizza), (SELECT ToppingID FROM topping WHERE ToppingName = 'Green Pepper'), 0),
        ((SELECT MAX(PizzaID) FROM pizza), (SELECT ToppingID FROM topping WHERE ToppingName = 'Onion'), 0),
        ((SELECT MAX(PizzaID) FROM pizza), (SELECT ToppingID FROM topping WHERE ToppingName = 'Mushrooms'), 0);
        
UPDATE orders
	SET OrderPrice = (SELECT SUM(PizzaPrice) FROM pizza WHERE PizzaOrderID = (SELECT MAX(PizzaOrderID) FROM pizza)),
		OrderCost = (SELECT SUM(PizzaCost) FROM pizza WHERE PizzaOrderID = (SELECT MAX(PizzaOrderID) FROM pizza))
	WHERE OrderID = (SELECT MAX(PizzaOrderID) FROM pizza);
    
-- order #7 --
INSERT INTO customer (CustomerFirstName, CustomerLastName, CustomerPhone)
	VALUES ('Lance', 'Benton', '864-878-5679');

INSERT INTO orders (OrderCost, OrderPrice, OrderTime, OrderType, OrderIsComplete, OrderCustomerID)
	VALUES (0, 0, '2023-03-06 20:32:00', 'DELIVERY', true, (SELECT MAX(CustomerID) FROM customer));
    
INSERT INTO delivery_order VALUES ((SELECT MAX(OrderID) FROM orders), '8879 Suburban Home, Anderson, SC 29621');
    
INSERT INTO pizza (PizzaIsCompleted, PizzaSize, PizzaCrust, PizzaOrderID, PizzaCost, PizzaPrice, PizzaDate)
	VALUES (1, 'large', 'Thin', (SELECT MAX(OrderID) FROM orders), 3.75, 12.00, '2023-03-06');
    
INSERT INTO pizza_toppings
	VALUES 
		((SELECT MAX(PizzaID) FROM pizza), (SELECT ToppingID FROM topping WHERE ToppingName = 'Four Cheese Blend'), 1);
        
INSERT INTO pizza (PizzaIsCompleted, PizzaSize, PizzaCrust, PizzaOrderID, PizzaCost, PizzaPrice, PizzaDate)
	VALUES (1, 'large', 'Thin', (SELECT MAX(OrderID) FROM orders), 3.75, 12.00, '2023-03-06');
    
INSERT INTO pizza_toppings
	VALUES 
		((SELECT MAX(PizzaID) FROM pizza), (SELECT ToppingID FROM topping WHERE ToppingName = 'Regular Cheese'), 0),
        ((SELECT MAX(PizzaID) FROM pizza), (SELECT ToppingID FROM topping WHERE ToppingName = 'Pepperoni'), 1);
        
INSERT INTO discount_order VALUES ((SELECT MAX(OrderID) FROM orders), (SELECT DiscountID FROM discount WHERE DiscountID = 'Employee'));

UPDATE orders
	SET OrderPrice = (SELECT SUM(PizzaPrice) FROM pizza WHERE PizzaOrderID = (SELECT MAX(PizzaOrderID) FROM pizza)),
		OrderCost = (SELECT SUM(PizzaCost) FROM pizza WHERE PizzaOrderID = (SELECT MAX(PizzaOrderID) FROM pizza))
	WHERE OrderID = (SELECT MAX(PizzaOrderID) FROM pizza);

