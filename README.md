# DBConnector
Some usefull java util classes for database programming.
 How to Connect To DataBase
 
 			ConnectDatabase db = new ConnectDatabase("com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/testDB","root","root");
			Connection conn = db.getConnection();
			
			SQLExecutor exe = new SQLExecutor(conn);
			
			Compare compWith = new Compare("name", ComparisonType.IsEqual).setPropertyValue("sohana", DataType.STRING);
			
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
			
			//Insert examples
			SQLInsertQuery iQuery2 = (SQLInsertQuery) new SQLQuery.Builder(QueryType.Insert)
													.into("Passenger")
													.values(new Property("name","James"), new Property("age", 28, DataType.INT), new Property("sex","male"))
													.build();
			int autoId = exe.executeInsert(true, iQuery2);
			System.out.println("Created ID " + autoId);
			
			//Update Examples
			Compare updateWhere = new Compare("id", ComparisonType.IsEqual).setPropertyValue(6, DataType.INT);
			SQLUpdateQuery upQuery = (SQLUpdateQuery) new SQLQuery.Builder(QueryType.Update)
											.set(new Property("name","James Ive"), new Property("age", 29, DataType.INT))
											.from("Passenger")
											.whereParams(Logic.AND, updateWhere).build();
			
			int isUpdated = exe.executeUpdate(upQuery);
			System.out.println("Updated Successfull " + (isUpdated == 1 ? "YES" : "NO"));
			
			exe.close();
			
		
