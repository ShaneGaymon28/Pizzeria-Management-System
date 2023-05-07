package cpsc4620;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;
import init.DBIniter;

/*
 * This file is where the front end magic happens.
 * 
 * You will have to write the functionality of each of these menu options' respective functions.
 * 
 * This file should need to access your DB at all, it should make calls to the DBNinja that will do all the connections.
 * 
 * You can add and remove functions as you see necessary. But you MUST have all 8 menu functions (9 including exit)
 * 
 * Simply removing menu functions because you don't know how to implement it will result in a major error penalty (akin to your program crashing)
 * 
 * Speaking of crashing. Your program shouldn't do it. Use exceptions, or if statements, or whatever it is you need to do to keep your program from breaking.
 * 
 * 
 */

public class Menu {
	public static void main(String[] args) throws SQLException, IOException {
		System.out.println("Welcome to Taylor's Pizzeria!");
		
		int menu_option = 0;

		// present a menu of options and take their selection
		
		PrintMenu();
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		DBIniter.init();
		String option = reader.readLine();
		menu_option = Integer.parseInt(option);

		while (menu_option != 9) {
			switch (menu_option) {
			case 1:// enter order
				EnterOrder();
				break;
			case 2:// view customers
				viewCustomers();
				break;
			case 3:// enter customer
				EnterCustomer();
				break;
			case 4:// view order
				// open/closed/date
				ViewOrders();
				break;
			case 5:// mark order as complete
				MarkOrderAsComplete();
				break;
			case 6:// view inventory levels
				ViewInventoryLevels();
				break;
			case 7:// add to inventory
				AddInventory();
				break;
			case 8:// view reports
				PrintReports();
				break;
			}
			PrintMenu();
			option = reader.readLine();
			menu_option = Integer.parseInt(option);
		}

	}

	public static void PrintMenu() {
		System.out.println("\n\nPlease enter a menu option:");
		System.out.println("1. Enter a new order");
		System.out.println("2. View Customers ");
		System.out.println("3. Enter a new Customer ");
		System.out.println("4. View orders");
		System.out.println("5. Mark an order as completed");
		System.out.println("6. View Inventory Levels");
		System.out.println("7. Add Inventory");
		System.out.println("8. View Reports");
		System.out.println("9. Exit\n\n");
		System.out.println("Enter your option: ");
	}

	// allow for a new order to be placed
	public static void EnterOrder() throws SQLException, IOException 
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

		ArrayList<Customer> customers = DBNinja.getCustomerList();
		ArrayList<Discount> discounts = DBNinja.getDiscountList();
		String existingCust = "";
		String option = "";

		int custID = 0;
		Order curOrder = new Order(0, 0, "", "", 0, 0, 0);

		System.out.println("Is the order for an existing customer? (y/n)");
		existingCust = reader.readLine();
		if (existingCust.equalsIgnoreCase("y")){
			System.out.println("Here's a list of existing customers: ");
			for (Customer c : customers){
				System.out.println(c.toString());
			}

			System.out.println("Enter the ID of the customer to use for this order: ");
			existingCust = reader.readLine();
			custID = Integer.parseInt(existingCust);
			curOrder.setCustID(custID);
		}
		else {
			EnterCustomer();
			// get max id
			custID = DBNinja.getMaxCustomerID();
			curOrder.setCustID(custID);
		}

		System.out.println("Is this order for (Enter the number):");
		System.out.println("\t1. Dine-in");
		System.out.println("\t2. Pick-up");
		System.out.println("\t3. Delivery");
		option = reader.readLine();

		int table = 0;
		String address = "";
		switch (Integer.parseInt(option)){
			case 1:
				curOrder.setOrderType(DBNinja.dine_in);
				System.out.println("Enter your table number:");
				table = Integer.parseInt(reader.readLine());
				break;
			case 2:
				curOrder.setOrderType(DBNinja.pickup);
				break;
			case 3:
				curOrder.setOrderType(DBNinja.delivery);
				System.out.println("Enter the delivery address:");
				address = reader.readLine();

				break;
			default:
				System.out.println("Unknown input for order type... returning to menu");
				break;
		}

		DBNinja.addOrder(curOrder);
		int orderID = DBNinja.getMaxOrderID();
		curOrder.setOrderID(orderID);

		// create pizzas
		while (true){
			curOrder.addPizza(buildPizza(orderID));
			System.out.println("Would you like to add another pizza? (y/n)");
			String moreZa = reader.readLine();

			if (moreZa.equalsIgnoreCase("n")){
				break;
			}
		}

		// calculate prices of all pizzas in order
		double op = 0, oc = 0;
		for (Pizza p : curOrder.getPizzaList()){
			op += p.getCustPrice();
			oc += p.getBusPrice();
		}
		curOrder.setBusPrice(oc);
		curOrder.setCustPrice(op);

		// set date of order
		Date date = new Date(System.currentTimeMillis());
		Timestamp ts = new Timestamp(date.getTime());

		String time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(ts);
		curOrder.setDate(time);

		System.out.println(curOrder.toString());

		// discounts
		System.out.println("Do you want to add discounts to this order? (y/n)");
		String disc = reader.readLine();
		if (disc.equalsIgnoreCase("y")){
			System.out.println("Getting discount list...");
			while (true) {
				for (Discount d : discounts) {
					System.out.println(d.toString());
				}

				System.out.println("Which order discount do you want to add? (Enter the DiscountID or -1 to stop adding discounts):");
				int discID = Integer.parseInt(reader.readLine());

				if (discID == -1){
					break;
				}

				for (Discount d : discounts) {
					if (discID == d.getDiscountID()) {
						curOrder.addDiscount(d);
						DBNinja.useOrderDiscount(curOrder, d);
					}
				}

			}
		}

		// update the order and create order type
		DBNinja.updateOrder(curOrder);
		switch (curOrder.getOrderType()){
			case DBNinja.dine_in:
				DineinOrder dineinOrder = new DineinOrder(curOrder.getOrderID(), curOrder.getCustID(), curOrder.getDate(),
						curOrder.getCustPrice(), curOrder.getBusPrice(), curOrder.getIsComplete(), table);
				DBNinja.addSubOrder(dineinOrder);
				break;
			case DBNinja.pickup:
				PickupOrder pickupOrder = new PickupOrder(curOrder.getOrderID(), curOrder.getCustID(), curOrder.getDate(),
						curOrder.getCustPrice(), curOrder.getBusPrice(), 0, 0);
				DBNinja.addSubOrder(pickupOrder);
				break;
			case DBNinja.delivery:
				DeliveryOrder deliveryOrder = new DeliveryOrder(curOrder.getOrderID(), curOrder.getCustID(), curOrder.getDate(),
						curOrder.getCustPrice(), curOrder.getBusPrice(), 0, address);
				DBNinja.addSubOrder(deliveryOrder);
				break;
			default:
				System.out.println("Error getting order type");
				break;
		}
		
		System.out.println("Finished adding order...Returning to menu...");
	}
	
	// get and print customer list from database
	public static void viewCustomers() throws SQLException, IOException
	{
		ArrayList<Customer> customers = DBNinja.getCustomerList();

		for (Customer c : customers){
			System.out.println(c.toString());
		}
	}
	

	// allows user to enter a new customer
	public static void EnterCustomer() throws SQLException, IOException 
	{

		String firstName, lastName, phone;
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Enter the customer's first name (then press <enter>): ");
		firstName = reader.readLine();

		System.out.println("Enter the customer's last name (then press <enter>): ");
		lastName = reader.readLine();

		System.out.println("Enter the customer's phone number in the format XXX-XXX-XXXX (then press <enter>): ");
		phone = reader.readLine();

		int nextCustID = DBNinja.getMaxCustomerID() + 1;
		Customer c = new Customer(nextCustID, firstName, lastName, phone);
		DBNinja.addCustomer(c);
		System.out.println("Customer: " + c.toString() + " was created successfully");


	}

	// View any orders that are not marked as completed
	public static void ViewOrders() throws SQLException, IOException 
	{
		String option;
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		ArrayList<Order> orders = DBNinja.getCurrentOrders();
		ArrayList<Order> filtered = new ArrayList<>();

		System.out.println("Would you like to:");
		System.out.println("\t1. Display all orders");
		System.out.println("\t2. Display orders since a specific date");
		option = reader.readLine();

		if (Integer.parseInt(option) == 1){
			// display all orders
			for (Order o : orders){
				System.out.println(o.toSimplePrint());
			}
		}
		else {
			// ask for date
			System.out.println("Which date would you like to restrict by? (YYYY-MM-DD)");
			String resDate = reader.readLine();
			// orders will be new --> old
			filtered = DBNinja.filterOrdersByDate(resDate, orders);
			for (Order o : filtered){
				System.out.println(o.toSimplePrint());
			}

		}

		System.out.println("Which order would you like to see in detail? (Enter the OrderID value):");
		option = reader.readLine();

		// get the order matching order ID
		for (Order o: orders){
			if (o.getOrderID() == Integer.parseInt(option)){
				System.out.println(o.toString());
				break;
			}
		}

	}

	
	// When an order is completed, we need to make sure it is marked as complete
	public static void MarkOrderAsComplete() throws SQLException, IOException 
	{

		ArrayList<Order> orders = DBNinja.getCurrentOrders();
		ArrayList<Order> incomplete = new ArrayList<>();
		String option;

		for (Order o : orders){
			if (o.getIsComplete() == 0){
				incomplete.add(o);
				System.out.println(o.toSimplePrint());
			}
		}

		System.out.println("Which order would you like to mark as complete? (Enter the OrderID):");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		option = reader.readLine();

		int orderID = Integer.parseInt(option);
		for (Order o : incomplete){
			if (o.getOrderID() == orderID){
				DBNinja.CompleteOrder(o);
				break;
			}
		}


	}

	// See the list of inventory and it's current level
	public static void ViewInventoryLevels() throws SQLException, IOException 
	{
		DBNinja.printInventory();
		return;
	}

	// Select an inventory item and add more to the inventory level to re-stock the
	// inventory
	public static void AddInventory() throws SQLException, IOException 
	{
		String top, amt;
		int toppingID = 0;
		double addAmt = 0;

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

		DBNinja.printInventory();

		System.out.println();
		System.out.println("Which topping would you like to add more of? (Enter the ID value)");
		top = reader.readLine();

		System.out.println("How much would you like to add?");
		amt = reader.readLine();

		toppingID = Integer.parseInt(top);
		addAmt = Double.parseDouble(amt);

		// get topping
		Topping t = DBNinja.getTopping(toppingID);

		DBNinja.AddToInventory(t, addAmt);
	}

	// A function that builds a pizza. Used in our add new order function
	public static Pizza buildPizza(int orderID) throws SQLException, IOException 
	{

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		Pizza p = null;
		String size = "", crust = "";
		ArrayList<Topping> tops = DBNinja.getInventory();

		int isize, icrust;

		System.out.println("Let's build a pizza!");

		System.out.println("What size is the pizza?\n\t1. Small\n\t2. Medium\n\t3. Large\n\t4. XLarge");
		System.out.println("Enter the corresponding number:");
		isize = Integer.parseInt(reader.readLine());

		System.out.println("What crust for this pizza?\n\t1. Thin\n\t2. Original\n\t3. Pan\n\t4. Gluten-Free");
		System.out.println("Enter the corresponding number:");
		icrust = Integer.parseInt(reader.readLine());

		switch (isize){
			case 1:
				size = DBNinja.size_s;
				break;
			case 2:
				size = DBNinja.size_m;
				break;
			case 3:
				size = DBNinja.size_l;
				break;
			case 4:
				size = DBNinja.size_xl;
				break;
		}

		switch (icrust){
			case 1:
				crust = DBNinja.crust_thin;
				break;
			case 2:
				crust = DBNinja.crust_orig;
				break;
			case 3:
				crust = DBNinja.crust_pan;
				break;
			case 4:
				crust = DBNinja.crust_gf;
				break;
		}

		//int pizzaID = DBNinja.getMaxPizzaID() + 1;
		Date date = new Date(System.currentTimeMillis());
		double price = DBNinja.getBaseCustPrice(size, crust);
		double cost = DBNinja.getBaseBusPrice(size, crust);

		// pizzaID is 0 because I'm not using it when inserting into the DB
		p = new Pizza(0, size, crust, orderID, "false", date.toString(), cost, price);
		DBNinja.addPizza(p);
		p.setPizzaID(DBNinja.getMaxPizzaID());

		// toppings
		int topChoice = 0;
		while (true){
			ViewInventoryLevels();
			System.out.println("Which topping do you want to add? (Enter the Topping ID or -1 to stop adding toppings)");
			topChoice = Integer.parseInt(reader.readLine());

			if (topChoice == -1){
				break;
			}

			System.out.println("Would you like to add extra of this topping? (y/n)");
			String extra = reader.readLine();

			for (Topping t : tops){
				if (topChoice == t.getTopID()) {
					if (extra.equalsIgnoreCase("y")) {
						DBNinja.useTopping(p, t, true);
						p.addToppings(t, true);
					}
					else {
						DBNinja.useTopping(p, t, false);
						p.addToppings(t, false);
					}
				}
			}
		}

		System.out.println("Would you like to add discounts to this Pizza? (y/n)");
		String disc = reader.readLine();

		if (disc.equalsIgnoreCase( "y")){
			ArrayList<Discount> discounts = DBNinja.getDiscountList();

			int discChoice = 0;
			while (true){
				System.out.println("Getting discount list...");
				for (Discount d : discounts){
					System.out.println(d.toString());
				}
				System.out.println("Which discount would you like to add? (Enter the DiscountID or -1 to stop adding discounts");
				discChoice = Integer.parseInt(reader.readLine());

				if (discChoice == -1){
					break;
				}

				for (Discount d : discounts){
					if (discChoice == d.getDiscountID()){
						DBNinja.usePizzaDiscount(p, d);
						p.addDiscounts(d);
					}
				}
			}
		}

		// update pizza price and cost
		DBNinja.updatePizza(p);
		System.out.println(p.toString());

		return p;
	}
	
	private static int getTopIndexFromList(int TopID, ArrayList<Topping> tops)
	{
		/*
		 * This is a helper function I used to get a topping index from a list of toppings
		 * It's very possible you never need to use a function like this
		 * 
		 */
		int ret = -1;
		
		
		
		return ret;
	}
	
	
	public static void PrintReports() throws SQLException, NumberFormatException, IOException
	{
		int selection = 0;
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Which report would you like to print? (Enter the corresponding number)");
		System.out.println("\t1. Topping Popularity");
		System.out.println("\t2. Profit by Pizza");
		System.out.println("\t3. Profit by Order Type");

		String report = reader.readLine();
		selection = Integer.parseInt(report);
		switch (selection){
			case 1:
				DBNinja.printToppingPopReport();
				break;
			case 2:
				DBNinja.printProfitByPizzaReport();
				break;
			case 3:
				DBNinja.printProfitByOrderType();
				break;
			default:
				System.out.println("Input error, returning to menu");
		}



	}

}


//Prompt - NO CODE SHOULD TAKE PLACE BELOW THIS LINE
//DO NOT EDIT ANYTHING BELOW HERE, I NEED IT FOR MY TESTING DIRECTORY. IF YOU EDIT SOMETHING BELOW, IT BREAKS MY TESTER WHICH MEANS YOU DO NOT GET GRADED (0)

/*
CPSC 4620 Project: Part 3 â€“ Java Application Due: Thursday 11/30 @ 11:59 pm 125 pts

For this part of the project you will complete an application that will interact with your database. Much of the code is already completed, you will just need to handle the functionality to retrieve information from your database and save information to the database.
Note, this program does little to no verification of the input that is entered in the interface or passed to the objects in constructors or setters. This means that any junk data you enter will not be caught and will propagate to your database, if it does not cause an exception. Be careful with the data you enter! In the real world, this program would be much more robust and much more complex.

Program Requirements:

Add a new order to the database: You must be able to add a new order with pizzas to the database. The user interface is there to handle all of the input, and it creates the Order object in the code. It then calls DBNinja.addOrder(order) to save the order to the database. You will need to complete addOrder. Remember addOrder will include adding the order as well as the pizzas and their toppings. Since you are adding a new order, the inventory level for any toppings used will need to be updated. You need to check to see if there is inventory available for each topping as it is added to the pizza. You can not let the inventory level go negative for this project. To complete this operation, DBNinja must also be able to return a list of the available toppings and the list of known customers, both of which must be ordered appropropriately.

View Customers: This option will display each customer and their associated information. The customer information must be ordered by last name, first name and phone number. The user interface exists for this, it just needs the functionality in DBNinja

Enter a new customer: The program must be able to add the information for a new customer in the database. Again, the user interface for this exists, and it creates the Customer object and passes it to DBNinja to be saved to the database. You need to write the code to add this customer to the database. You do need to edit the prompt for the user interface in Menu.java to specify the format for the phone number, to make sure it matches the format in your database.

View orders: The program must be able to display orders and be sorted by order date/time from most recent to oldest. The program should be able to display open orders, all the completed orders or just the completed order since a specific date (inclusive) The user interface exists for this, it just needs the functionality in DBNinja

Mark an order as completed: Once the kitchen has finished prepping an order, they need to be able to mark it as completed. When an order is marked as completed, all of the pizzas should be marked as completed in the database. Open orders should be sorted as described above for option #4. Again, the user interface exists for this, it just needs the functionality in DBNinja

View Inventory Levels: This option will display each topping and its current inventory level. The toppings should be sorted in alphabetical order. Again, the user interface exists for this, it just needs the functionality in DBNinja

Add Inventory: When the inventory level of an item runs low, the restaurant will restock that item. When they do so, they need to enter into the inventory how much of that item was added. They will select a topping and then say how many units were added. Note: this is not creating a new topping, just updating the inventory level. Make sure that the inventory list is sorted as described in option #6. Again, the user interface exists for this, it just needs the functionality in DBNinja

View Reports: The program must be able to run the 3 profitability reports using the views you created in Part 2. Again, the user interface exists for this, it just needs the functionality in DBNinja

Modify the package DBConnector to contain your database connection information, this is the same information you use to connect to the database via MySQL Workbench. You will use DBNinja.connect_to_db to open a connection to the database. Be aware of how many open database connections you make and make sure the database is properly closed!
Your code needs to be secure, so any time you are adding any sort of parameter to your query that is a String, you need to use PreparedStatements to prevent against SQL injections attacks. If your query does not involve any parameters, or if your queries parameters are not coming from a String variable, then you can use a regular Statement instead.

The Files: Start by downloading the starter code files from Canvas. You will see that the user interface and the java interfaces and classes that you need for the assignment are already completed. Review all these files to familiarize yourself with them. They contain comments with instructions for what to complete. You should not need to change the user interface except to change prompts to the user to specify data formats (i.e. dashes in phone number) so it matches your database. You also should not need to change the entity object code, unless you want to remove any ID fields that you did not add to your database.

You could also leave the ID fields in place and just ignore them. If you have any data types that donâ€™t match (i.e. string size options as integers instead of strings), make the conversion when you pull the information from the database or add it to the database. You need to handle data type differences at that time anyway, so it makes sense to do it then instead of making changes to all of the files to handle the different data type or format.

The Menu.java class contains the actual user interface. This code will present the user with a menu of options, gather the necessary inputs, create the objects, and call the necessary functions in DBNinja. Again, you will not need to make changes to this file except to change the prompt to tell me what format you expect the phone number in (with or without dashes).

There is also a static class called DBNinja. This will be the actual class that connects to the database. This is where most of the work will be done. You will need to complete the methods to accomplish the tasks specified.

Also in DBNinja, there are several public static strings for different crusts, sizes and order types. By defining these in one place and always using those strings we can ensure consistency in our data and in our comparisons. You donâ€™t want to have â€œSMALLâ€� â€œsmallâ€� â€œSmallâ€� and â€œPersonalâ€� in your database so it is important to stay consistent. These strings will help with that. You can change what these strings say in DBNinja to match your database, as all other code refers to these public static strings.

Start by changing the class attributes in DBConnector that contain the data to connect to the database. You will need to provide your database name, username and password. All of this is available is available in the Chapter 15 lecture materials. Once you have that done, you can begin to build the functions that will interact with the database.

The methods you need to complete are already defined in the DBNinja class and are called by Menu.java, they just need the code. Two functions are completed (getInventory and getTopping), although for a different database design, and are included to show an example of connecting and using a database. You will need to make changes to these methods to get them to work for your database.

Several extra functions are suggested in the DBNinja class. Their functionality will be needed in other methods. By separating them out you can keep your code modular and reduce repeated code. I recommend completing your code with these small individual methods and queries. There are also additional methods suggested in the comments, but without the method template that could be helpful for your program. HINT, make sure you test your SQL queries in MySQL Workbench BEFORE implementing them in codeâ€¦it will save you a lot of debugging time!

If the code in the DBNinja class is completed correctly, then the program should function as intended. Make sure to TEST, to ensure your code works! Remember that you will need to include the MySQL JDBC libraries when building this application. Otherwise you will NOT be able to connect to your database.

Compiling and running your code: The starter code that will compile and â€œrunâ€�, but it will not do anything useful without your additions. Because so much code is being provided, there is no excuse for submitting code that does not compile. Code that does not compile and run will receive a 0, even if the issue is minor and easy to correct.

Help: Use MS Teams to ask questions. Do not wait until the last day to ask questions or get started!

Submission You will submit your assignment on Canvas. Your submission must include: â€¢ Updated DB scripts from Part 2 (all 5 scripts, in a folder, even if some of them are unchanged). â€¢ All of the class code files along with a README file identifying which class files in the starter code you changed. Include the README even if it says â€œI have no special instructions to shareâ€�. â€¢ Zip the DB Scripts, the class files (i.e. the application), and the README file(s) into one compressed ZIP file. No other formats will be accepted. Do not submit the lib directory or an IntellJ or other IDE project, just the code.

Testing your submission Your project will be tested by replacing your DBconnector class with one that connects to a special test server. Then your final SQL files will be run to recreate your database and populate the tables with data. The Java application will then be built with the new DBconnector class and tested.

No late submissions will be accepted for this assignment.*/

