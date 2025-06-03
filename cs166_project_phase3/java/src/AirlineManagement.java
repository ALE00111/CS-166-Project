/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;
import java.io.IOException;
import java.util.Scanner;


/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class AirlineManagement {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   // static Scanner scanner = new Scanner(System.in);

   /**
    * Creates a new instance of AirlineManagement
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public AirlineManagement(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end AirlineManagement

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
		 if(outputHeader){
			// for(int i = 1; i <= numCol; i++){
			// System.out.print(rsmd.getColumnName(i) + "\t");
			// }
			// System.out.println();

         for (int i = 1; i <= numCol; i++) {
            System.out.printf("%-20s", rsmd.getColumnName(i));
         }
         System.out.println();
			outputHeader = false;
		 }
         // for (int i=1; i<=numCol; ++i)
         //    System.out.print (rs.getString (i) + "\t");
         // System.out.println ();

         for (int i=1; i<=numCol; ++i) {
            System.out.printf("%-20s", rs.getString (i));
         }
         System.out.println();
         ++rowCount;
      }//end while
      stmt.close();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
        List<String> record = new ArrayList<String>();
		for (int i=1; i<=numCol; ++i)
			record.add(rs.getString (i));
        result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       while (rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();

	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            AirlineManagement.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      AirlineManagement esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the AirlineManagement object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new AirlineManagement (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              if(authorisedUser.equals("1")) {
               while(usermenu) {
                  System.out.println("\nAIRLINE MANAGERS");
                  System.out.println("---------");

                  //**the following functionalities should only be able to be used by Management**
                  System.out.println("1. View All Flights");
                  System.out.println("2. View Weekly Schedule of a Flight");
                  System.out.println("3. View Flight Seats");
                  System.out.println("4. View Flight Status");
                  System.out.println("5. View Flights of the day");  
                  System.out.println("6. View Passenger List Status");
                  System.out.println("7. View Passenger Information");
                  System.out.println("8. View Plane Information");
                  System.out.println("9. View All Repairs of a Technician");
                  System.out.println("10. View List of Repairs on a Plane");
                  System.out.println("11. View Flight Statistics");
                  //System.out.println("5. View Full Order ID History");
                  //  System.out.println(".........................");
                  //  System.out.println(".........................");
                  System.out.println("20. Log out");
                  switch (readChoice()){
                   case 1: ViewFlights(esql); break;
                   case 2: FlightWeeklySchedule(esql); break;
                   case 3: FlightSeats(esql); break;
                   case 4: FlightStatus(esql); break;
                   case 5: FlightsToday(esql); break;
                   case 6: PassengersList(esql); break;
                   case 7: PassengerInfo(esql); break;
                   case 8: PlaneInfo(esql); break;
                   case 9: TechnicianInfo(esql); break;
                   case 10: PlaneRepairRange(esql); break;
                   case 11: FlightStats(esql); break;
                   case 20: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
               }
              }
              else if (authorisedUser.equals("2")) {
                  while(usermenu) {
                     System.out.println("\nCUSTOMER");
                     System.out.println("---------");
                     //**the following functionalities should only be able to be used by customers**
                     //  System.out.println("10. Search Flights");
                     //  System.out.println(".........................");
                     //  System.out.println(".........................");
                     System.out.println("20. Log out");
                  switch(readChoice()) {
                     case 20: usermenu = false; break;
                     default : System.out.println("Unrecognized choice!"); break;
                  }
                  }
              }
              else if (authorisedUser.equals("3")) {
                  while(usermenu) {
                     System.out.println("\nPILOTS");
                     System.out.println("---------");
                     System.out.println("1. Maintenace Request");
                     System.out.println("20. Log out");

                  switch(readChoice()) {
                     //**the following functionalities should ony be able to be used by Pilots**
                     case 1: MaintenaceRequest(esql); break; 
                     case 20: usermenu = false; break;
                     default : System.out.println("Unrecognized choice!"); break;
                  }
                  }
              }
              else if(authorisedUser.equals("4")) {
                   while(usermenu) {
                     System.out.println("\nTECHNICIAN");
                     System.out.println("---------");
                     //**the following functionalities should only be able to be used by customers**
                     //  System.out.println("10. Search Flights");
                     //  System.out.println(".........................");
                     //  System.out.println(".........................");
                     System.out.println("20. Log out");
                  switch(readChoice()) {
                     case 20: usermenu = false; break;
                     default : System.out.println("Unrecognized choice!"); break;
                  }
                  }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user
    **/
   public static void CreateUser(AirlineManagement esql){
      //Temporary print for creating user
      System.out.println("USER CREATED\n");
   }//end CreateUser


   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(AirlineManagement esql){
      //Check for user credentials 
      //TEMPORARY LOGIN
      System.out.println("STATE YOUR ROLE: \n");
      System.out.println("1. AIRLINE MANAGEMENT\n");
      System.out.println("2. CUSTOMER \n");
      System.out.println("3. PILOT \n");
      System.out.println("4. TECHNICIAN \n");

      // Enter data using BufferReader
      String choice = "";
      try {
         // Printing the read line
         choice = in.readLine();
         //System.out.println(choice);
      }
      catch (IOException e) {
         System.out.println(e.getMessage());
      }
      return choice;
   }//end

// Rest of the functions definition go in here
   
   //View ALL FLIGHTS
   public static void ViewFlights(AirlineManagement esql) {
      try {
         System.out.println("\tALL FLIGHTS\t\n");
         String query = "SELECT * FROM Flight";
         int rowCount = esql.executeQuery(query);
         System.out.println ("total row(s): " + rowCount);
         esql.executeQueryAndPrintResult(query);

      } catch (Exception e) {
         System.err.println (e.getMessage());
      }
   }

   //View Flights weekly schedule based on flight number
   public static void FlightWeeklySchedule(AirlineManagement esql) {
      try{
         System.out.print("Enter a flight number for all flights schedule for the week: ");
         String query = "SELECT * FROM Schedule WHERE flightNumber = ";
         String flightNum = in.readLine();
         query += "'"+ flightNum + "'";

         if (esql.executeQueryAndReturnResult(query).size() == 0) {
            System.out.println("FLIGHT: " + flightNum + " NOT AVAIALBE THIS WEEK\n");
            return;
         }

         System.out.println("\tALL FLIGHTS FOR "+ flightNum + "\t\n");
         int rowCount = esql.executeQuery(query);
         System.out.println ("total row(s): " + rowCount);
         esql.executeQueryAndPrintResult(query);
      }  catch(Exception e){
         System.err.println (e.getMessage());
      }
   }


   //View Number of Seats on a specific flight given flight number and date
   public static void FlightSeats(AirlineManagement esql) {
      try {
         System.out.print("Enter a flight number: ");
         String flightNum = in.readLine();
         System.out.print("Enter a flight date(M/D/YY): ");
         String flightDate = in.readLine();
         String query = "SELECT SeatsTotal, SeatsSold FROM FlightInstance WHERE FlightNumber = ";
         query += "'"+ flightNum + "' AND FlightDate = '" + flightDate + "'";

         System.out.println("\n");
         if (esql.executeQueryAndReturnResult(query).size() == 0) {
            System.out.println("FLIGHT: " + flightNum + " NOT AVAIALBE ON "+ flightDate + "\n");
            return;
         }

         //executeQueryAndReturnResult returns a variable of type List<List<String>> which is a 2D array
         //result1.get(0).get(0) is broken down where result1.get(0) is returning the first row 
         // and result1.get(0).get(0) is returning the first column

         List<List<String>> result = esql.executeQueryAndReturnResult(query);
         int SeatsTotal = Integer.parseInt(result.get(0).get(0));
         int SeatsSold = Integer.parseInt(result.get(0).get(1));
         int SeatsOpen = SeatsTotal - SeatsSold; 

         System.out.println("FLIGHT: \t" + flightNum + "\n");
         System.out.println("ON DATE: \t" + flightDate + "\n");
         System.out.println("\tNUMBER OF SEATS OPEN:\t" + SeatsOpen + "\n");
         System.out.println("\tNUMBER OF SEATS SOLD:\t" + SeatsSold + "\n");
      } catch (Exception e) {
         System.err.println (e.getMessage());
      }
   }

   //View the flight status given a flight number and date
   public static void FlightStatus(AirlineManagement esql) {
      try {
         System.out.print("Enter a flight number: ");
         String flightNum = in.readLine();
         System.out.print("Enter a flight date(M/D/YY): ");
         String flightDate = in.readLine();
         String query = "SELECT DepartedOnTime, ArrivedOnTime FROM FlightInstance WHERE FlightNumber = ";
         query += "'"+ flightNum + "' AND FlightDate = '" + flightDate + "'";

         System.out.println("\n");
         if (esql.executeQueryAndReturnResult(query).size() == 0) {
            System.out.println("FLIGHT: " + flightNum + " NOT AVAIALBE ON "+ flightDate + "\n");
            return;
         }

         //executeQueryAndReturnResult returns a variable of type List<List<String>> which is a 2D array
         //result1.get(0).get(0) is broken down where result1.get(0) is returning the first row 
         // and result1.get(0).get(0) is returning the first column

         List<List<String>> result = esql.executeQueryAndReturnResult(query);
         String DepartureStatus = result.get(0).get(0);
         String ArrivalStatus = result.get(0).get(1);

         System.out.println("FLIGHT: \t" + flightNum + "\n");
         System.out.println("ON DATE: \t" + flightDate + "\n");
         
         if(DepartureStatus.equals("f")) {
            System.out.println("DEPARTURE STATUS: \t NOT ON TIME\n");
         }
         else {
            System.out.println("DEPARTURE STATUS: \t ON TIME\n");
         }

         if(ArrivalStatus.equals("f")) {
            System.out.println("ARRIVAL STATUS: \t NOT ON TIME\n");
         }
         else {
            System.out.println("ARRIVAL STATUS: \t ON TIME\n");
         }
      } catch (Exception e) {
         System.err.println (e.getMessage());
      }
   }

   //View all flights for a given day
   public static void FlightsToday(AirlineManagement esql) {
      try {
         System.out.print("Enter a flight date(M/D/YY): ");
         String flightDate = in.readLine();
         String query = "SELECT * FROM FlightInstance WHERE FlightDate = '"+ flightDate + "'";

         if (esql.executeQueryAndReturnResult(query).size() == 0) {
            System.out.println("INVALID DATE\n");
            return;
         }

         System.out.println("\n FLIGHTS SCHEDULED ON: " + flightDate + "\n");
         int rowCount = esql.executeQuery(query);
         System.out.println ("total row(s): " + rowCount);
         esql.executeQueryAndPrintResult(query);
      } catch (Exception e) {
         System.err.println (e.getMessage());
      }
   }

   //View list of all passengers who reserved, are on the waiting list, and who actually flew on the flight
   public static void PassengersList(AirlineManagement esql) {
      try {
         System.out.print("Enter a flight number: ");
         String flightNum = in.readLine();
         System.out.print("Enter a flight date(M/D/YY): ");
         String flightDate = in.readLine();
         String query = "SELECT f.FlightInstanceID, r.Status  FROM FlightInstance f, Reservation r WHERE f.FlightNumber = ";
         query += "'"+ flightNum + "' AND f.FlightDate = '" + flightDate + "' AND f.FlightInstanceID = r.FlightInstanceID";

         if (esql.executeQueryAndReturnResult(query).size() == 0) {
            System.out.println("FLIGHT: " + flightNum + " NOT AVAIALBE ON "+ flightDate + "\n");
            return;
         }

         System.out.println("\n ALL PASSENGERS STATUS OF FLIGHT: " + flightNum + " ON " + "flightDate\n");
         int rowCount = esql.executeQuery(query);
         System.out.println ("total row(s): " + rowCount);
         esql.executeQueryAndPrintResult(query);
      } catch (Exception e) {
         System.err.println (e.getMessage());
      }
   }

   //Given a reservation number, retrieve information about the travelers under that number
   public static void PassengerInfo(AirlineManagement esql) {
      try {
         System.out.print("Enter Reservation ID: ");
         String reservationID = in.readLine();
         String query = "SELECT * FROM Customer c WHERE c.CustomerID = (SELECT r.CustomerId FROM Reservation r WHERE r.ReservationID = '" + reservationID + "')";

         if (esql.executeQueryAndReturnResult(query).size() == 0) {
            System.out.println("Reservation: " + reservationID + " DOES NOT EXIST\n");
            return;
         }
         
         System.out.println("\n INFORMATION OF CUSTOMER WITH RESERVATION: \t" + reservationID + "\n");
         int rowCount = esql.executeQuery(query);
         System.out.println ("total row(s): " + rowCount);
         esql.executeQueryAndPrintResult(query);
      } catch (Exception e) {
         System.err.println (e.getMessage());
      }
   }


   //Given a plane number, get its make, model, age, and last repair date
   public static void PlaneInfo(AirlineManagement esql) {
      try {
         System.out.print("Enter Plane ID: ");
         String planeID= in.readLine();
         String query = "SELECT * FROM Plane WHERE PlaneID = '" + planeID + "'";

         if (esql.executeQueryAndReturnResult(query).size() == 0) {
            System.out.println("PLANE: " + planeID + " DOES NOT EXIST\n");
            return;
         }

         System.out.println("\nINFORMATION OF PLANE: \t" + planeID + "\n");
         int rowCount = esql.executeQuery(query);
         System.out.println ("total row(s): " + rowCount);
         esql.executeQueryAndPrintResult(query);
      } catch (Exception e) {
         System.err.println (e.getMessage());
      }
   }
  
   //Given a maintenance technician ID, list all repairs made by that person
   public static void TechnicianInfo(AirlineManagement esql) {
       try {
         System.out.print("Enter Technician ID: ");
         String technicianID = in.readLine();
         String query = "SELECT * FROM Repair WHERE TechnicianID = '" + technicianID + "'";

         if (esql.executeQueryAndReturnResult(query).size() == 0) {
            System.out.println("TECHNICIAN: " + technicianID + " DOES NOT EXIST\n");
            return;
         }

         System.out.println("\nREPAIRS MADE BY: \t" + technicianID + "\n");
         int rowCount = esql.executeQuery(query);
         System.out.println ("total row(s): " + rowCount);
         esql.executeQueryAndPrintResult(query);
      } catch (Exception e) {
         System.err.println (e.getMessage());
      }
   }

   //Given a plane ID and a date range, list all the dates and the codes for repairs performed
   public static void PlaneRepairRange(AirlineManagement esql) {
      try {
         System.out.print("Enter Plane ID: ");
         String planeID= in.readLine();
         System.out.print("\nEnter a Date Range (YYYY-MM-DD): \n\n");
         System.out.print("Start Date: ");
         String startDate = in.readLine();
         System.out.print("\nEnd Date: ");
         String endDate = in.readLine();
         String query = "SELECT PlaneID, RepairDate, RepairCode FROM Repair WHERE RepairDate >= '" + startDate +"' AND RepairDate <= '" + endDate + "' AND PlaneID = '" + planeID + "'"; 

         if (esql.executeQueryAndReturnResult(query).size() == 0) {
            System.out.println("\nNO PLANEID: " + planeID + " HAS REPAIRS BETWEEN: " + startDate + " to " + startDate + "\n");
            return;
         }

         System.out.println("\nREPAIRS ON PLANE: \t" + planeID + " FROM: " + startDate + " TO: " + endDate + "\n");
         int rowCount = esql.executeQuery(query);
         System.out.println ("total row(s): " + rowCount);
         esql.executeQueryAndPrintResult(query);
      } catch (Exception e) {
         System.err.println (e.getMessage());
      }
   }

   //Given a flight and a range of date (start date, end date), show the statistics of the flight:
   //number of days the flight departed and arrived, number of sold and unsold tickets
   //NOT COMPLETE RETURN LATER
   public static void FlightStats(AirlineManagement esql) {
      try {
         // System.out.print("Enter Flight Number: ");
         // String flightNum = in.readLine();
         // System.out.print("\nEnter a Date Range (M-D-YY): \n\n");
         // System.out.print("Start Date: ");
         // String startDate = in.readLine();
         // System.out.print("\nEnd Date: ");
         // String endDate = in.readLine();
      

         // String query = "SELECT FlightNum, FlightDate, SeatsTotal, SeatsSold FROM FlightInstance WHERE FlightNumber = ";
         // query += "'"+ flightNum + "' AND FlightDate >= '" + startDate + "' AND FlightDate <= '" + endDate + "'";


         // List<List<String>> result = esql.executeQueryAndReturnResult(query);

         // while() {
         //    int SeatsTotal = Integer.parseInt(result.get(0).get(2));
         //    int SeatsSold = Integer.parseInt(result.get(0).get(3));
         //    int SeatsOpen = SeatsTotal - SeatsSold;

         // }


         // System.out.println("FLIGHT: \t" + flightNum + "\n");
         // System.out.println("ON DATE: \t" + flightDate + "\n");
         // System.out.println("\tNUMBER OF SEATS OPEN:\t" + SeatsOpen + "\n");
         // System.out.println("\tNUMBER OF SEATS SOLD:\t" + SeatsSold + "\n");



      } catch (Exception e) {
         System.err.println (e.getMessage());
      }
   }

   public static void MaintenaceRequest(AirlineManagement esql) {
      try {
         System.out.print("Enter Pilot ID: ");
         String pilotID = in.readLine();
         System.out.print("\nEnter Plane ID: ");
         String planeID = in.readLine();
         System.out.print("\nEnter the Repair Code: ");
         String repairCode = in.readLine();
         System.out.print("\nEnter Date of Request(YYYY-MM-DD): ");
         String requestDate = in.readLine();

         List<List<String>> result = esql.executeQueryAndReturnResult("SELECT MAX(RequestID) FROM MaintenanceRequest");
         int requestID =  Integer.parseInt(result.get(0).get(0)) + 1;

         String query = "INSERT INTO MaintenanceRequest(RequestID, PlaneID, RepairCode, RequestDate, PilotID) VALUES ("+requestID+", '"+ planeID +"', '"+ repairCode +"', '"+ requestDate +"', '"+ pilotID +"')";
         System.out.println("\n\t" +query + "\t\n");
         System.out.println("\nMAKING REQUEST FOR:" + pilotID + " ON PLANE: " + planeID + "\n");
         esql.executeUpdate(query);

         String query2 = "SELECT * FROM MaintenanceRequest";
         esql.executeQueryAndPrintResult(query2);
      } catch (Exception e) {
         System.err.println (e.getMessage());
      }
   }


}//end AirlineManagement

