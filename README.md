# DBConnector
Some usefull java util classes for database programming.
 How to Connect To DataBase
 
 			ConnectDatabase db = new ConnectDatabase("com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/testDB","root","root");
			Connection conn = db.getConnection();
			
			SQLExecutor exe = new SQLExecutor(conn);
			
			Compare compWith = new Compare("name", ComparisonType.IsEqual);
			compWith.setPropertyValue("sohana", DataType.STRING);
			
			SQLSelectQuery qc = (SQLSelectQuery) new SQLQuery.Builder(QueryType.Select)
																.columns()
																.from("Passenger")
																.whereParams(Logic.AND, compWith)
																.build();
			ResultSet set = exe.executeSelect(qc);
			
			List<Map<String,Object>> x = exe.convertToKeyValuePaire(set);
			exe.displayCollection(x);
			
			Map<Object,Map<String,Object>> x2 = exe.convertToIndexedKeyValuePaire(set, "id");
			exe.displayCollection(x2);
			
			exe.close();
			
		
