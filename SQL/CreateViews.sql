-- Shane Gaymon -- 

use PizzasRUs;

DROP VIEW IF EXISTS ToppingPopularity;

CREATE VIEW ToppingPopularity AS
	SELECT 
		topping.ToppingName AS Topping, 
        COUNT(pizza_toppings.ToppingsID) + (SELECT COUNT(IsExtraToppings) FROM pizza_toppings WHERE IsExtraToppings = 1 AND ToppingsID = topping.ToppingID) AS ToppingCount
    FROM topping, pizza_toppings
    WHERE topping.ToppingID = pizza_toppings.ToppingsID
    GROUP BY pizza_toppings.ToppingsID
    ORDER BY ToppingCount DESC, Topping ASC;
    
SELECT * FROM ToppingPopularity;

DROP VIEW IF EXISTS ProfitByPizza;

CREATE VIEW ProfitByPizza AS 
	SELECT 
		Profit.PizzaSize AS PizzaSize,
        Profit.PizzaCrust AS PizzaCrust,
        CAST(TypeProfit AS DECIMAL(6,2)) AS Profit,
        LastOrderDate
	FROM
		(SELECT 
			PizzaSize, 
			PizzaCrust, 
            ROUND(SUM(PizzaPrice-PizzaCost), 2) AS TypeProfit,
            DATE_FORMAT(MAX(PizzaDate), '%M-%e-%Y') AS LastOrderDate
            FROM pizza
            WHERE PizzaDate BETWEEN '2023-01-01' AND '2023-12-31'
            GROUP BY PizzaSize, PizzaCrust)
            AS Profit
		GROUP BY PizzaSize, PizzaCrust
        ORDER BY TypeProfit DESC;
        
SELECT * FROM ProfitByPizza;

DROP VIEW IF EXISTS ProfitByOrderType;

CREATE VIEW ProfitByOrderType AS
SELECT 
	OrderType AS CustomerType,
    DATE_FORMAT(OrderTime, '%Y-%M') AS OrderMonth,
    ROUND(SUM(OrderPrice), 2) AS TotalOrderPrice,
    ROUND(SUM(OrderCost), 2) AS TotalOrderCost,
    ROUND(SUM(OrderPrice - OrderCost), 2) AS Profit
FROM
	orders
GROUP BY
	MONTH(OrderMonth),
    OrderType
UNION ALL
SELECT 
	NULL,
    'Grand Total', 
    ROUND(SUM(OrderPrice), 2) AS TotalOrderPrice,
    ROUND(SUM(OrderCost), 2) AS TotalOrderCost,
    ROUND(SUM(OrderPrice - OrderCost), 2) AS Profit
FROM 
	orders;

    
SELECT * FROM ProfitByOrderType;
	
    