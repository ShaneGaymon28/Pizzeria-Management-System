package cpsc4620;

import com.mysql.cj.x.protobuf.MysqlxPrepare;

import java.io.IOException;
import java.sql.*;
import java.sql.Date;
import java.util.*;

/*
 * This file is where most of your code changes will occur You will write the code to retrieve
 * information from the database, or save information to the database
 * 
 * The class has several hard coded static variables used for the connection, you will need to
 * change those to your connection information
 * 
 * This class also has static string variables for pickup, delivery and dine-in. If your database
 * stores the strings differently (i.e "pick-up" vs "pickup") changing these static variables will
 * ensure that the comparison is checking for the right string in other places in the program. You
 * will also need to use these strings if you store this as boolean fields or an integer.
 * 
 * 
 */

/**
 * A utility class to help add and retrieve information from the database
 */

public final class DBNinja {
	private static Connection conn;

	// Change these variables to however you record dine-in, pick-up and delivery, and sizes and crusts
	public final static String pickup = "PICKUP";
	public final static String delivery = "DELIVERY";
	public final static String dine_in = "DINEIN";

	public final static String size_s = "small";
	public final static String size_m = "medium";
	public final static String size_l = "large";
	public final static String size_xl = "x-Large";

	public final static String crust_thin = "Thin";
	public final static String crust_orig = "Original";
	public final static String crust_pan = "Pan";
	public final static String crust_gf = "Gluten-Free";



	
	private static boolean connect_to_db() throws SQLException, IOException {

		try {
			conn = DBConnector.make_connection();
			return true;
		} catch (SQLException e) {
			return false;
		} catch (IOException e) {
			return false;
		}

	}

	
	public static void addOrder(Order o) throws SQLException, IOException 
	{
		connect_to_db();
		Double cost = o.getBusPrice();
		Double price = o.getCustPrice();

		String sql =
				"INSERT INTO orders (OrderCost, OrderPrice, OrderType, OrderIsComplete, OrderCustomerID)" +
				"VALUES (?, ?, ?, ?, ?)";

		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setFloat(1, cost.floatValue());
			ps.setFloat(2, price.floatValue());
			ps.setString(3, o.getOrderType());
			ps.setBoolean(4, false);
			ps.setInt(5, o.getCustID());
			ps.executeUpdate();

			conn.close();
		} catch (Exception e){
			System.out.println(e);
		}
	}

	public static void updateOrder(Order o) throws SQLException, IOException
	{
		connect_to_db();

		Double cost = o.getBusPrice();
		Double price = o.getCustPrice();
		boolean comp;

		if (o.getIsComplete() == 1){
			comp = true;
		}
		else {
			comp = false;
		}

		String sql =
				"UPDATE orders " +
				"SET OrderCost = ?, OrderPrice = ?, OrderTime = ?, OrderType = ?, OrderIsComplete = ?, OrderCustomerID = ? " +
				"WHERE OrderID = ?";

		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setFloat(1, cost.floatValue());
			ps.setFloat(2, price.floatValue());
			ps.setString(3, o.getDate());
			ps.setString(4, o.getOrderType());
			ps.setBoolean(5, comp);
			ps.setInt(6, o.getCustID());
			ps.setInt(7, o.getOrderID());
			ps.executeUpdate();
		} catch (Exception e){
			System.out.println(e);
		}

		conn.close();
	}

	public static void addSubOrder(Order o) throws SQLException, IOException
	{
		connect_to_db();
		if (o instanceof DineinOrder){
			DineinOrder dineinOrder = (DineinOrder) o;
			String sql = "INSERT INTO dine_in_order VALUES(?, ?)";
			try {
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setInt(1, dineinOrder.getOrderID());
				ps.setInt(2, dineinOrder.getTableNum());
				ps.executeUpdate();
			} catch (Exception e){
				System.out.println(e);
			}
		}
		else if (o instanceof PickupOrder){
			PickupOrder pickupOrder = (PickupOrder) o;
			String sql = "INSERT INTO pick_up_order VALUES(?, ?)";
			try {
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setInt(1, pickupOrder.getOrderID());
				ps.setString(2, getCustomerPhone(pickupOrder.getCustID()));
				ps.executeUpdate();
			} catch (Exception e){
				System.out.println(e);
			}
		}
		else if (o instanceof DeliveryOrder){
			DeliveryOrder deliveryOrder = (DeliveryOrder) o;
			String sql = "INSERT INTO pick_up_order VALUES(?, ?)";
			try {
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setInt(1, deliveryOrder.getOrderID());
				ps.setString(2, deliveryOrder.getAddress());
				ps.executeUpdate();
			} catch (Exception e){
				System.out.println(e);
			}
		}

		conn.close();
	}
	
	public static void addPizza(Pizza p) throws SQLException, IOException
	{
		connect_to_db();
		Double cost = p.getBusPrice();
		Double price = p.getCustPrice();


		String sql =
				"INSERT INTO pizza " +
					"(PizzaIsCompleted, PizzaSize, PizzaCrust, PizzaOrderID, PizzaCost, PizzaPrice, PizzaDate) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?)";


		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setBoolean(1, false);
			ps.setString(2, p.getSize());
			ps.setString(3, p.getCrustType());
			ps.setInt(4, p.getOrderID());
			ps.setFloat(5, cost.floatValue());
			ps.setFloat(6, price.floatValue());
			ps.setDate(7, Date.valueOf(p.getPizzaDate()));
			ps.executeUpdate();

		} catch (Exception e){
			System.out.println(e);
		}

		conn.close();
	}

	public static void updatePizza(Pizza p) throws SQLException, IOException
	{
		connect_to_db();

		Double cost = p.getBusPrice();
		Double price = p.getCustPrice();

		String sql =
				"UPDATE pizza " +
						"SET PizzaCost = ?, PizzaPrice = ?" +
						"WHERE PizzaID = ?";

		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setFloat(1, cost.floatValue());
			ps.setFloat(2, price.floatValue());
			ps.setInt(3, p.getPizzaID());
			ps.executeUpdate();

		} catch (Exception e){
			System.out.println(e);
		}

		conn.close();
	}
	
	public static int getMaxPizzaID() throws SQLException, IOException
	{
		connect_to_db();
		int pizzaID = 0;

		String sql = "SELECT MAX(PizzaID) as p FROM pizza";

		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet res = ps.executeQuery();
			if (res.next()) {
				pizzaID = res.getInt("p");
			}

		} catch (Exception e){
			System.out.println(e);
		}

		conn.close();
		return pizzaID;
	}

	public static int getMaxCustomerID() throws SQLException, IOException
	{
		connect_to_db();
		int custID = 0;

		String sql = "SELECT MAX(CustomerID) as c FROM customer";

		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet resultSet = ps.executeQuery();
			if (resultSet.next()){
				custID = resultSet.getInt("c");
			}


		} catch (Exception e){
			System.out.println(e);
		}

		conn.close();
		return custID;
	}

	public static int getMaxOrderID() throws SQLException, IOException
	{
		connect_to_db();
		int orderID = 0;

		String sql = "SELECT MAX(OrderID) as o FROM orders";

		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet resultSet = ps.executeQuery();
			if (resultSet.next()){
				orderID = resultSet.getInt("o");
			}

			conn.close();
		} catch (Exception e){
			System.out.println(e);
		}

		return orderID;
	}
	
	public static void useTopping(Pizza p, Topping t, boolean isDoubled) throws SQLException, IOException //this function will update toppings inventory in SQL and add entities to the Pizzatops table. Pass in the p pizza that is using t topping
	{
		connect_to_db();

		String pSize = p.getSize();
		int m = 1;
		if (isDoubled){
			m = 2;
		}

		if(pSize.equals(size_s)){
			int inv = t.getCurINVT() - (int) (t.getPerAMT() * m);
			t.setCurINVT(inv);
		}
		else if(pSize.equals(size_m)){
			int inv = t.getCurINVT() - (int) (t.getMedAMT() * m);
			t.setCurINVT(inv);
		}
		else if(pSize.equals(size_l)){
			int inv = t.getCurINVT() - (int) (t.getLgAMT() * m);
			t.setCurINVT(inv);
		}
		else {
			int inv = t.getCurINVT() - (int) (t.getXLAMT() * m);
			t.setCurINVT(inv);
		}

		String sql = "INSERT INTO pizza_toppings VALUES(?, ?, ?)";
		String top = "UPDATE topping SET ToppingCurrentInventory = ? WHERE ToppingID = ?";
		try {
			PreparedStatement ps = conn.prepareStatement(top);
			ps.setInt(1, t.getCurINVT());
			ps.setInt(2, t.getTopID());
			ps.executeUpdate();

			ps = conn.prepareStatement(sql);
			ps.setInt(1, p.getPizzaID());
			ps.setInt(2, t.getTopID());
			ps.setBoolean(3, isDoubled);
			ps.executeUpdate();

		} catch (Exception e){
			System.out.println(e);
		}


		conn.close();
	}
	
	// done
	public static void usePizzaDiscount(Pizza p, Discount d) throws SQLException, IOException
	{
		connect_to_db();
		
		String sql =
				"INSERT INTO discount_pizza VALUES (?, ?)";

		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, p.getPizzaID());
			ps.setString(2, d.getDiscountName());
			ps.executeUpdate();

			conn.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	// done
	public static void useOrderDiscount(Order o, Discount d) throws SQLException, IOException
	{
		connect_to_db();

		String sql =
				"INSERT INTO discount_order VALUES (?, ?)";

		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, o.getOrderID());
			ps.setString(2, d.getDiscountName());
			ps.executeUpdate();

			conn.close();
		} catch (Exception e) {
			System.out.println(e);
		}

	}
	
	// done
	public static void addCustomer(Customer c) throws SQLException, IOException {
		connect_to_db();
				
		String sql =
				"INSERT INTO customer(CustomerFirstName, CustomerLastName, CustomerPhone) " +
				"VALUES (?, ?, ?)";

		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, c.getFName());
			ps.setString(2, c.getLName());
			ps.setString(3, c.getPhone());
			ps.executeUpdate();


			conn.close();
		} catch (Exception e){
			System.out.println(e);
		}

	}


	
	public static void CompleteOrder(Order o) throws SQLException, IOException {
		connect_to_db();

		String sql =
				"UPDATE orders " +
				"SET OrderIsComplete = true " +
				"WHERE OrderID = ?";

		String updatePizzas = "UPDATE pizza SET PizzaIsCompleted = true WHERE PizzaOrderID = ?";

		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, o.getOrderID());
			ps.executeUpdate();

			ps = conn.prepareStatement(updatePizzas);
			ps.setInt(1, o.getOrderID());
			ps.executeUpdate();

		} catch (Exception e){
			System.out.println(e);
		}
		conn.close();
	}


	
	// done
	public static void AddToInventory(Topping t, double toAdd) throws SQLException, IOException {
		connect_to_db();

		Double add = new Double(toAdd);

		String sql =
				"UPDATE topping " +
				"SET ToppingCurrentInventory = ToppingCurrentInventory + ? " +
				"WHERE ToppingID = ?";

		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setFloat(1, add.floatValue());
			ps.setInt(2, t.getTopID());
			ps.executeUpdate();


			conn.close();
		} catch (Exception e){
			System.out.println(e);
		}
	}

	// done
	public static Topping getTopping(int id) throws SQLException, IOException {
		connect_to_db();

		Topping t;

		String sql = "SELECT * FROM topping WHERE ToppingID = ?";

		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, id);
			ResultSet resultSet = ps.executeQuery();

			if(resultSet.next()){
				t = new Topping(
						resultSet.getInt("ToppingID"),
						resultSet.getString("ToppingName"),
						resultSet.getFloat("ToppingSmall"),
						resultSet.getFloat("ToppingMedium"),
						resultSet.getFloat("ToppingLarge"),
						resultSet.getFloat("ToppingXLarge"),
						resultSet.getFloat("ToppingPrice"),
						resultSet.getFloat("ToppingCost"),
						0,
						resultSet.getInt("ToppingCurrentInventory")
				);

				conn.close();
				return t;
			}

		} catch (Exception e){
			System.out.println(e);
		}

		return null;
	}

	// done
	public static void printInventory() throws SQLException, IOException {
		connect_to_db();

		String sql = "SELECT * FROM topping ORDER BY ToppingName";
		Statement stmt = conn.createStatement();
		ResultSet resultSet = stmt.executeQuery(sql);

		System.out.printf("%-5s%-10s%20s\n", "ID", "Name", "Current Inventory");
		while (resultSet.next()){
			System.out.printf("%-5d%-10s%20d\n", resultSet.getInt("ToppingID"), resultSet.getString("ToppingName"),
					resultSet.getInt("ToppingCurrentInventory"));
		}

		conn.close();
	}
	
	// done
	public static ArrayList<Topping> getInventory() throws SQLException, IOException {
		connect_to_db();

		ArrayList<Topping> toppings = new ArrayList<>();

		String sql = "SELECT * FROM topping ORDER BY ToppingName";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet resultSet = ps.executeQuery();

			while (resultSet.next()) {
				Topping top = new Topping(
						resultSet.getInt("ToppingID"),
						resultSet.getString("ToppingName"),
						resultSet.getFloat("ToppingSmall"),
						resultSet.getFloat("ToppingMedium"),
						resultSet.getFloat("ToppingLarge"),
						resultSet.getFloat("ToppingXLarge"),
						resultSet.getFloat("ToppingPrice"),
						resultSet.getFloat("ToppingCost"),
						0,
						resultSet.getInt("ToppingCurrentInventory"));

				toppings.add(top);
			}

			conn.close();
		} catch (Exception e){
			System.out.println(e);
		}
		return toppings;
	}


	public static ArrayList<Order> getCurrentOrders() throws SQLException, IOException {
		connect_to_db();

		ArrayList<Order> orders = new ArrayList<>();

		String sql = "SELECT * FROM orders ORDER BY OrderTime DESC";

		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet resultSet = ps.executeQuery();

			while (resultSet.next()) {

				if (resultSet.getString(5).equals(pickup)) {
					PickupOrder pu = new PickupOrder(resultSet.getInt("OrderID"), resultSet.getInt("OrderCustomerID"),
							resultSet.getString("OrderTime"), resultSet.getFloat("OrderPrice"), resultSet.getFloat("OrderCost"),
							1, resultSet.getInt("OrderIsComplete"));//resultSet.getBoolean("OrderIsComplete") ? 1 : 0);
					orders.add(pu);
				} else if (resultSet.getString(5).equals(delivery)) {
					DeliveryOrder d = new DeliveryOrder(resultSet.getInt("OrderID"), resultSet.getInt("OrderCustomerID"),
							resultSet.getString("OrderTime"), resultSet.getFloat("OrderPrice"), resultSet.getFloat("OrderCost"),
							resultSet.getInt("OrderIsComplete"), "");//resultSet.getBoolean(6) ? 1 : 0, "");
					d.setAddress(getDeliveryAddress(d.getOrderID()));
					orders.add(d);

				} else {
					DineinOrder di = new DineinOrder(resultSet.getInt("OrderID"), resultSet.getInt("OrderCustomerID"),
							resultSet.getString("OrderTime"), resultSet.getFloat("OrderPrice"), resultSet.getFloat("OrderCost"),
							resultSet.getInt("OrderIsComplete"), 0);//resultSet.getBoolean(6) ? 1: 0, 0);
					di.setTableNum(getDineInTableNum(di.getOrderID()));
					orders.add(di);
				}

			}


		} catch (Exception e){
			System.out.println(e);
		}

		conn.close();
		return orders;
	}

	// filter existing orders list to find orders from a certain date
	public static ArrayList<Order> filterOrdersByDate(String date, ArrayList<Order> orders){
		ArrayList<Order> filtered = new ArrayList<>();
		int day = getDay(date);
		int month = getMonth(date);
		int year = getYear(date);


		for (Order o : orders){
			String cur = o.getDate();
			if (getYear(cur) >= year && getMonth(cur) >= month && getDay(cur) >= day){
				filtered.add(o);
			}
		}

		return filtered;
	}

	
	/*
	 * The next 3 private functions help get the individual components of a SQL datetime object. 
	 * You're welcome to keep them or remove them.
	 */
	private static int getYear(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(0,4));
	}
	private static int getMonth(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(5, 7));
	}
	private static int getDay(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(8, 10));
	}


	
	public static double getBaseCustPrice(String size, String crust) throws SQLException, IOException {
		connect_to_db();
		double bp = 0.0;
		
		
		String sql =
				"SELECT BasePrice " +
				"FROM base_price " +
				"WHERE BasePizzaSize = ? AND BaseCrustType = ?";

		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, size);
			ps.setString(2, crust);
			ResultSet resultSet = ps.executeQuery();

			if (resultSet.next()) {
				bp = resultSet.getFloat("BasePrice");
				return bp;
			}


			conn.close();
		} catch (Exception e){
			System.out.println(e);
		}
		return bp;
	}

	// done
	public static String getCustomerPhone(int CustID) throws SQLException, IOException
	{
		connect_to_db();
		String ret = "";
		String sql = "SELECT CustomerPhone FROM customer WHERE CustomerID = ?";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, CustID);
			ResultSet resultSet = ps.executeQuery();
			if (resultSet.next()){
				ret = resultSet.getString("CustomerPhone");
			}
		} catch (Exception e){
			System.out.println(e);
		}

		conn.close();
		return ret;
	}

	// done
	public static String getDeliveryAddress(int OrderID) throws SQLException, IOException
	{
		connect_to_db();
		String ret = "";
		String sql = "SELECT DeliveryAddress FROM delivery_order WHERE DeliveryOrderID = ?";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, OrderID);
			ResultSet resultSet = ps.executeQuery();
			if (resultSet.next()){
				ret = resultSet.getString("DeliveryAddress");
			}
		} catch (Exception e){
			System.out.println(e);
		}

		conn.close();
		return ret;
	}

	// get customer table num
	// done
	public static int getDineInTableNum(int OrderID) throws SQLException, IOException
	{
		connect_to_db();
		int ret = 0;
		String sql = "SELECT TableNum FROM dine_in_order WHERE DineInOrderID = ?";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, OrderID);
			ResultSet resultSet = ps.executeQuery();
			if (resultSet.next()){
				ret = resultSet.getInt("TableNum");
			}
		} catch (Exception e){
			System.out.println(e);
		}

		conn.close();
		return ret;
	}

	// done
	public static String getCustomerName(int CustID) throws SQLException, IOException
	{
		/*
		 *This is a helper function I used to fetch the name of a customer
		 *based on a customer ID. It actually gets called in the Order class
		 *so I'll keep the implementation here. You're welcome to change
		 *how the order print statements work so that you don't need this function.
		 */
		connect_to_db();
		String ret = "";
		String query = "Select CustomerFirstName, CustomerLastName From customer WHERE CustomerID=" + CustID + ";";
		Statement stmt = conn.createStatement();
		ResultSet rset = stmt.executeQuery(query);
		
		while(rset.next())
		{
			ret = rset.getString(1) + " " + rset.getString(2);
		}
		conn.close();
		return ret;
	}

	// done
	public static double getBaseBusPrice(String size, String crust) throws SQLException, IOException {
		connect_to_db();
		double bp = 0.0;
		// add code to get the base cost (for the business) for that size and crust pizza Depending on how
		// you store size and crust in your database, you may have to do a conversion

		String sql =
				"SELECT BaseCost " +
				"FROM base_price " +
				"WHERE BasePizzaSize = ? AND BaseCrustType = ?";

		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, size);
			ps.setString(2, crust);
			ResultSet resultSet = ps.executeQuery();

			if (resultSet.next()) {
				bp = resultSet.getFloat("BaseCost");
				return bp;
			}


			conn.close();
		} catch (Exception e){
			System.out.println(e);
		}
		return bp;
	}

	// done
	public static ArrayList<Discount> getDiscountList() throws SQLException, IOException {
		ArrayList<Discount> discs = new ArrayList<Discount>();
		connect_to_db();
		//returns a list of all the discounts.
		
		
		String sql = "SELECT * FROM discount";

		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet resultSet = ps.executeQuery();

			int index = 1;
			while (resultSet.next()) {
				Discount d = new Discount(
						index++,
						resultSet.getString("DiscountID"),
						resultSet.getFloat("DiscountAmount"),
						resultSet.getBoolean("DiscountIsPercentage")
				);

				discs.add(d);
			}

			conn.close();
		} catch (Exception e){
			System.out.println(e);
		}
		return discs;
	}

	// done
	public static ArrayList<Customer> getCustomerList() throws SQLException, IOException {
		ArrayList<Customer> custs = new ArrayList<Customer>();
		connect_to_db();
		/*
		 * return an arrayList of all the customers. These customers should
		 *print in alphabetical order, so account for that as you see fit.
		*/

		String sql = "SELECT * FROM customer ORDER BY CustomerFirstName, CustomerLastName";

		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet resultSet = ps.executeQuery();

			while (resultSet.next()) {
				Customer c = new Customer(
						resultSet.getInt("CustomerID"),
						resultSet.getString("CustomerFirstName"),
						resultSet.getString("CustomerLastName"),
						resultSet.getString("CustomerPhone")
				);

				custs.add(c);
			}



		} catch (Exception e){
			System.out.println(e);
		}

		conn.close();
		return custs;
	}
	
	public static void printToppingPopReport() throws SQLException, IOException
	{
		connect_to_db();

		String sql = "SELECT * FROM ToppingPopularity";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet resultSet = ps.executeQuery();

			System.out.printf("%-20s%30s\n", "Topping", "ToppingCount");
			while (resultSet.next()) {
				System.out.printf("%-20s%20s\n", resultSet.getString("Topping"), resultSet.getInt("ToppingCount"));
			}

			conn.close();
		} catch (Exception e){
			System.out.println(e);
		}
	}
	
	public static void printProfitByPizzaReport() throws SQLException, IOException
	{
		connect_to_db();

		String sql = "SELECT * FROM ProfitByPizza";

		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet resultSet = ps.executeQuery();

			System.out.printf("%-10s%15s%15s%20s\n", "Pizza Size", "Pizza Crust", "Profit", "LastOrderDate");
			while (resultSet.next()) {
				System.out.printf("%-10s%15s%15s%20s\n", resultSet.getString("PizzaSize"), resultSet.getString("PizzaCrust"),
						resultSet.getFloat("Profit"), resultSet.getString("LastOrderDate"));
			}

			conn.close();
		} catch (Exception e){
			System.out.println(e);
		}
	}
	
	public static void printProfitByOrderType() throws SQLException, IOException
	{
		connect_to_db();


		String sql = "SELECT * FROM ProfitByOrderType";

		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet resultSet = ps.executeQuery();

			System.out.printf("%-10s%15s%20s%15s%10s\n", "Order Type", "Order Month", "TotalOrderPrice", "TotalOrderCost",
					"Profit");
			while (resultSet.next()) {
				System.out.printf("%-10s%15s%20s%10s%15s\n", resultSet.getString("CustomerType"), resultSet.getString("OrderMonth"),
						resultSet.getFloat("TotalOrderPrice"), resultSet.getFloat("TotalOrderCost"), resultSet.getFloat("Profit"));
			}

		} catch (Exception e){
			System.out.println(e);
		}

		conn.close();
	}
	
	

}