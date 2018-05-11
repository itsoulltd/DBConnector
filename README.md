# DBConnector
Some usefull java util classes for database programming.
 How to Connect To DataBase
 
 			ConnectDatabase db = new ConnectDatabase("com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/testDB","root","towhid@123");
			Connection conn = db.getConnection();
			
			SQLExecutor exe = new SQLExecutor(conn);
			
			String query = SQLBuilder.createSelectQuery("Passenger"); //"Select * From Passenger"
			ResultSet set = exe.executeSelect(query);
			
			List<Map<String,Object>> x = exe.convertToKeyValuePaire(set);
			exe.displayCollection(x);
			
			Map<Object,Map<String,Object>> x2 = exe.convertToIndexedKeyValuePaire(set, "id");
			exe.displayCollection(x2);
			
			String[] projectionParams = {"id","name"};
			Map<String, Parameter> whereClause = new HashMap<String, SQLBuilder.Parameter>();
			whereClause.put("name", new Parameter("name", "sohana", DataType.ParamDataTypeString));
			ResultSet set2 = exe.executeSelect("Passenger", projectionParams, null, whereClause);
			List<Map<String,Object>> x4 = exe.convertToKeyValuePaire(set2);
			exe.displayCollection(x4);
			
			exe.close();
			
		
