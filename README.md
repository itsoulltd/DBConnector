# DBConnector
Some usefull java util classes for database programming.

 			//How to Connect To DataBase and Make Select, Insert, Update and Delete
 
 			ConnectDatabase db = new ConnectDatabase("com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/testDB","root","root");
			Connection conn = db.getConnection();
			
			SQLExecutor exe = new SQLExecutor(conn);
			
			//Select Example
			Compare compWith = new Compare("name", ComparisonType.IsEqual).setPropertyValue("sohana", DataType.STRING);
			
			SQLSelectQuery qc = (SQLSelectQuery) new SQLQuery.Builder(QueryType.Select)
																.columns()
																.from("Passenger")
																.whereExpression(compWith)
																.build();
			ResultSet set = exe.executeSelect(qc);
			
			List<Map<String,Object>> x = exe.convertToKeyValuePaire(set);
			exe.displayCollection(x);
			
			Map<Object,Map<String,Object>> x2 = exe.convertToIndexedKeyValuePaire(set, "id");
			exe.displayCollection(x2);
			
			//Insert into
			SQLInsertQuery iQuery2 = (SQLInsertQuery) new SQLQuery.Builder(QueryType.Insert)
											.into("Passenger")
											.values(new Property("name","tanvir"), new Property("age", 28, DataType.INT), new Property("sex","male"))
											.build();
			int autoId = exe.executeInsert(true, iQuery2);
			System.out.println("Created ID " + autoId);
			
			//Update example
			Compare compareWith = new Compare("id", ComparisonType.IsEqual).setPropertyValue(autoId, DataType.INT);
			
			SQLUpdateQuery upQuery = (SQLUpdateQuery) new SQLQuery.Builder(QueryType.Update)
											.set(new Property("name","tanvir Islam"), new Property("age", 29, DataType.INT))
											.from("Passenger")
											.whereExpression(compareWith).build();
			
			int updateId = exe.executeUpdate(upQuery);
			System.out.println("Updated Successfull " + (updateId == 1 ? "YES" : "NO"));
			
			//Delete Example where id = autoId
			SQLDeleteQuery dquery = (SQLDeleteQuery) new SQLQuery.Builder(QueryType.Delete)
											.rowsFrom("Passenger")
											.whereExpression(compareWith)
											.build();
			updateId = exe.executeDelete(dquery);
			System.out.println("Delete Successfull " + (updateId == 1 ? "YES" : "NO"));
			
			exe.close();
			//DataBase Connection Example End
			
			//How To Use LogicExpression
			LogicExpression or = new OrExpression(new Compare("name", ComparisonType.IsEqual), new Compare("age", ComparisonType.IsGreaterOrEqual));
			LogicExpression and = new AndExpression(new Compare("name", ComparisonType.IsEqual), compareWith);
			LogicExpression combined = new AndExpression(and, or);
			LogicExpression not = new NotExpression(combined);
			System.out.println(not.express());
			Compare[] comparesItems = not.resolveCompares();
			for (Compare compare : comparesItems) {
				System.out.println(compare.toString());
			}
		
