# DBConnector
Some usefull java util classes for database programming.

			
			ConnectDatabase db = new ConnectDatabase("com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/testDB","root","towhid@123");
			Connection conn = db.getConnection();
			
			SQLExecutor exe = new SQLExecutor();
			
			String query = SQLBuilder.createSelectQuery("Passenger"); //"Select * From Passenger"
			ResultSet set = exe.executeSelect(conn, query);
			
			List<Map<String,Object>> x = exe.convertToKeyValuePaire(set);
			exe.displayCollection(x);
			
			Map<Object,Map<String,Object>> x2 = exe.convertToIndexedKeyValuePaire(set, "id");
			exe.displayCollection(x2);
			
			Map<String,Object> x3 = exe.retrieveRow(set, 2, true);
			exe.displayCollection(x3);
			
			db.closeConnections(conn);
			
		
