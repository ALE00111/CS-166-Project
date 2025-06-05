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
import java.time.*;
import java.time.format.DateTimeFormatter;

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
        try {
            // constructs the connection URL
            String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
            System.out.println("Connection URL: " + url + "\n");

            // obtain a physical connection
            this._connection = DriverManager.getConnection(url, user, passwd);
            System.out.println("Done");
        } catch (Exception e) {
            System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
            System.out.println("Make sure you started postgres on this machine");
            System.exit(-1);
        }//end catch
    }//end AirlineManagement

    /**
     * Method to execute an update SQL statement. Update SQL instructions
     * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
     *
     * @param sql the input SQL string
     * @throws java.sql.SQLException when update failed
     */
    public void executeUpdate(String sql) throws SQLException {
        // creates a statement object
        Statement stmt = this._connection.createStatement();

        // issues the update instruction
        stmt.executeUpdate(sql);

        // close the instruction
        stmt.close();
    }//end executeUpdate

    /**
     * Method to execute an input query SQL instruction (i.e. SELECT). This
     * method issues the query to the DBMS and outputs the results to standard
     * out.
     *
     * @param query the input query string
     * @return the number of rows returned
     * @throws java.sql.SQLException when failed to execute the query
     */
    public int executeQueryAndPrintResult(String query) throws SQLException {
        // creates a statement object
        Statement stmt = this._connection.createStatement();

        // issues the query instruction
        ResultSet rs = stmt.executeQuery(query);

        /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
         */
        ResultSetMetaData rsmd = rs.getMetaData();
        int numCol = rsmd.getColumnCount();
        int rowCount = 0;

        // iterates through the result set and output them to standard out.
        boolean outputHeader = true;
        while (rs.next()) {
            if (outputHeader) {
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

            for (int i = 1; i <= numCol; ++i) {
                System.out.printf("%-20s", rs.getString(i));
            }
            System.out.println();
            ++rowCount;
        }//end while
        stmt.close();
        return rowCount;
    }//end executeQuery

    /**
     * Method to execute an input query SQL instruction (i.e. SELECT). This
     * method issues the query to the DBMS and returns the results as a list of
     * records. Each record in turn is a list of attribute values
     *
     * @param query the input query string
     * @return the query result as a list of records
     * @throws java.sql.SQLException when failed to execute the query
     */
    public List<List<String>> executeQueryAndReturnResult(String query) throws SQLException {
        // creates a statement object
        Statement stmt = this._connection.createStatement();

        // issues the query instruction
        ResultSet rs = stmt.executeQuery(query);

        /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
         */
        ResultSetMetaData rsmd = rs.getMetaData();
        int numCol = rsmd.getColumnCount();
        int rowCount = 0;

        // iterates through the result set and saves the data returned by the query.
        boolean outputHeader = false;
        List<List<String>> result = new ArrayList<List<String>>();
        while (rs.next()) {
            List<String> record = new ArrayList<String>();
            for (int i = 1; i <= numCol; ++i) {
                record.add(rs.getString(i));
            }
            result.add(record);
        }//end while
        stmt.close();
        return result;
    }//end executeQueryAndReturnResult

    /**
     * Method to execute an input query SQL instruction (i.e. SELECT). This
     * method issues the query to the DBMS and returns the number of results
     *
     * @param query the input query string
     * @return the number of rows returned
     * @throws java.sql.SQLException when failed to execute the query
     */
    public int executeQuery(String query) throws SQLException {
        // creates a statement object
        Statement stmt = this._connection.createStatement();

        // issues the query instruction
        ResultSet rs = stmt.executeQuery(query);

        int rowCount = 0;

        // iterates through the result set and count nuber of results.
        while (rs.next()) {
            rowCount++;
        }//end while
        stmt.close();
        return rowCount;
    }

    /**
     * Method to fetch the last value from sequence. This method issues the
     * query to the DBMS and returns the current value of sequence used for
     * autogenerated keys
     *
     * @param sequence name of the DB sequence
     * @return current value of a sequence
     * @throws java.sql.SQLException when failed to execute the query
     */
    public int getCurrSeqVal(String sequence) throws SQLException {
        Statement stmt = this._connection.createStatement();

        ResultSet rs = stmt.executeQuery(String.format("Select currval('%s')", sequence));
        if (rs.next()) {
            return rs.getInt(1);
        }
        return -1;
    }

    /**
     * Method to close the physical connection if it is open.
     */
    public void cleanup() {
        try {
            if (this._connection != null) {
                this._connection.close();
            }//end if
        } catch (SQLException e) {
            // ignored.
        }//end try
    }//end cleanup

    /**
     * The main execution method
     *
     * @param args the command line arguments this inclues the <mysql|pgsql>
     * <login file>
     */
    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println(
                    "Usage: "
                    + "java [-classpath <classpath>] "
                    + AirlineManagement.class.getName()
                    + " <dbname> <port> <user>");
            return;
        }//end if

        Greeting();
        AirlineManagement esql = null;
        try {
            // use postgres JDBC driver.
            Class.forName("org.postgresql.Driver").newInstance();
            // instantiate the AirlineManagement object and creates a physical
            // connection.
            String dbname = args[0];
            String dbport = args[1];
            String user = args[2];
            esql = new AirlineManagement(dbname, dbport, user, "");

            boolean keepon = true;
            while (keepon) {
                // These are sample SQL statements
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. Create user");
                System.out.println("2. Log in");
                System.out.println("9. < EXIT");
                String authorisedUser = null;
                switch (readChoice()) {
                    case 1:
                        CreateUser(esql);
                        break;
                    case 2:
                        authorisedUser = LogIn(esql);
                        break;
                    case 9:
                        keepon = false;
                        break;
                    default:
                        System.out.println("Unrecognized choice!");
                        break;
                }//end switch
                if (authorisedUser != null) {
                    boolean usermenu = true;
                    if (authorisedUser.equals("1")) {
                        while (usermenu) {
                            System.out.println("\nAIRLINE MANAGEMENT");
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
                            switch (readChoice()) {
                                case 1:
                                    ViewFlights(esql);
                                    break;
                                case 2:
                                    FlightWeeklySchedule(esql);
                                    break;
                                case 3:
                                    FlightSeats(esql);
                                    break;
                                case 4:
                                    FlightStatus(esql);
                                    break;
                                case 5:
                                    FlightsToday(esql);
                                    break;
                                case 6:
                                    PassengersList(esql);
                                    break;
                                case 7:
                                    PassengerInfo(esql);
                                    break;
                                case 8:
                                    PlaneInfo(esql);
                                    break;
                                case 9:
                                    TechnicianInfo(esql);
                                    break;
                                case 10:
                                    PlaneRepairRange(esql);
                                    break;
                                case 11:
                                    FlightStats(esql);
                                    break;
                                case 20:
                                    usermenu = false;
                                    break;
                                default:
                                    System.out.println("Unrecognized choice!");
                                    break;
                            }
                        }
                    } else if (authorisedUser.equals("2")) {
                        while (usermenu) {
                            System.out.println("\nCUSTOMER");
                            System.out.println("---------");
                            //**the following functionalities should only be able to be used by customers**
                            System.out.println("1. Find all flights on a given date");
                            System.out.println("2. Find ticket price");
                            System.out.println("3. Find airplane type");
                            System.out.println("4. Make a reservation");
                            System.out.println("5. Cancel a reservation");
                            System.out.println("6. View current reservations");
                            System.out.println("20. Log out");

                            switch (readChoice()) {
                                case 1:
                                    ViewDateFlights(esql);
                                    break;
                                case 2:
                                    FindTicketPrice(esql);
                                    break;
                                case 3:
                                    FindAirplaneType(esql);
                                    break;
                                case 4:
                                    MakeReservation(esql);
                                    break;
                                case 5:
                                    CancelReservation(esql);
                                    break;
                                case 6:
                                    ViewCurrentReservations(esql);
                                    break;
                                case 20:
                                    usermenu = false;
                                    break;
                                default:
                                    System.out.println("Unrecognized choice!");
                                    break;
                            }
                        }
                    } else if (authorisedUser.equals("3")) {
                        while (usermenu) {
                            System.out.println("\nPILOTS");
                            System.out.println("---------");
                            System.out.println("1. Maintenace Request");
                            System.out.println("20. Log out");

                            switch (readChoice()) {
                                //**the following functionalities should ony be able to be used by Pilots**
                                case 1:
                                    MaintenaceRequest(esql);
                                    break;
                                case 20:
                                    usermenu = false;
                                    break;
                                default:
                                    System.out.println("Unrecognized choice!");
                                    break;
                            }
                        }
                    } else if (authorisedUser.equals("4")) {
                        while (usermenu) {
                            System.out.println("\nTECHNICIAN");
                            System.out.println("---------");
                            //**the following functionalities should only be able to be used by technicians**
                            System.out.println("1. Find all repairs on a plane");
                            System.out.println("2. Find all pilot requests");
                            System.out.println("20. Log out");
                            switch (readChoice()) {
                                case 1:
                                    FindAllRepairs(esql);
                                    break;
                                case 2:
                                    FindAllPilotRequests(esql);
                                    break;
                                case 20:
                                    usermenu = false;
                                    break;
                                default:
                                    System.out.println("Unrecognized choice!");
                                    break;
                            }
                        }
                    }
                }
            }//end while
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            // make sure to cleanup the created table and close the connection.
            try {
                if (esql != null) {
                    System.out.print("Disconnecting from database...");
                    esql.cleanup();
                    System.out.println("Done\n\nBye !");
                }//end if
            } catch (Exception e) {
                // ignored.
            }//end try
        }//end try
    }//end main

    public static void Greeting() {
        System.out.println(
                "\n\n*******************************************************\n"
                + "              User Interface      	               \n"
                + "*******************************************************\n");
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
            } catch (Exception e) {
                System.out.println("Your input is invalid!");
                continue;
            }//end try
        } while (true);
        return input;
    }//end readChoice

    /*
    * Creates a new user
    **/
    public static void CreateUser(AirlineManagement esql) {
        try {
            System.out.println("\nCREATE A NEW USER: ");
            System.out.println("---------");

            int userID;
            String username;
            String password;
            String usertype;

            System.out.print("\nCREATE A USERNAME: ");
            username = in.readLine();
            while (esql.executeQuery("SELECT * FROM Users WHERE UserName = '" + username + "'") > 0) {
                System.out.println("ANOTHER USER IS ALREADY USING THIS USERNAME. PLEASE CHOOSE ANTOHER \n");
                System.out.print("\nCREATE A USERNAME: ");
                username = in.readLine();
            }

            System.out.print("\nCREATE A PASSWORD: ");
            password = in.readLine();

            System.out.print("\nINPUT A VALUE BASED ON WHAT TYPE OF USER YOU ARE: \n");
            System.out.println("1. AIRLINE MANAGEMENT\n");
            System.out.println("2. CUSTOMER \n");
            System.out.println("3. PILOT \n");
            System.out.println("4. TECHNICIAN \n");
            usertype = in.readLine();

            userID = esql.executeQuery("SELECT * FROM Users") + 1;
            String query = "INSERT INTO Users (UserID, UserName, Password, UserType) VALUES (" + userID + ", '" + username + "', '" + password + "', " + usertype + ")";
            esql.executeUpdate(query);
            System.out.println("\nUSER CREATED\n");

            esql.executeQueryAndPrintResult("SELECT * FROM USERS");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }//end CreateUser


    /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
    public static String LogIn(AirlineManagement esql) {
        //Check for user credentials 
        // Enter data using BufferReader
        try {
            while (true) {
                String username;
                String password;
                System.out.print("\nENTER USERNAME: ");
                username = in.readLine();
                System.out.print("\nENTER PASSWORD: ");
                password = in.readLine();

                String query = "SELECT UserType FROM Users WHERE UserName = '" + username + "' AND Password = '" + password + "'";
                List<List<String>> result = esql.executeQueryAndReturnResult(query);

                if (result.size() == 0) {
                    System.out.println("\nUSERNAME OR PASSWORD INCORRECT. TRY AGAIN\n");
                } else {
                    System.out.println("\nUSER HAS LOGGED IN\n");
                    return result.get(0).get(0);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
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
         System.out.print("\nEnter a flight number for all flights schedule for the week: ");
         String query = "SELECT * FROM Schedule WHERE flightNumber = ";
         String flightNum = in.readLine();
         while(esql.executeQuery("SELECT * FROM Flight WHERE FlightNumber = '" + flightNum + "'") < 1) {
            System.out.println("FLIGHT DOES NOT EXIST. PLEASE GIVE A VALID FLIGHT NUMBER\n");
            System.out.print("ENTER FLIGHT NUMBER: ");
            flightNum = in.readLine();
         }
         query += "'"+ flightNum + "'";

         if (esql.executeQuery(query) < 1) {
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
         while(esql.executeQuery("SELECT * FROM Flight WHERE FlightNumber = '" + flightNum + "'") < 1) {
            System.out.println("FLIGHT DOES NOT EXIST. PLEASE GIVE A VALID FLIGHT NUMBER\n");
            System.out.print("ENTER FLIGHT NUMBER: ");
            flightNum = in.readLine();
         }

         System.out.print("Enter a flight date(M/D/YY): ");
         String flightDate = in.readLine();

         
         String query = "SELECT SeatsTotal, SeatsSold, (SeatsTotal - SeatsSold) as SeatsOpen FROM FlightInstance WHERE FlightNumber = ";
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
         int seatsSold = Integer.parseInt(result.get(0).get(1));
         int seatsOpen = Integer.parseInt(result.get(0).get(2));

         System.out.println("FLIGHT: \t" + flightNum + "\n");
         System.out.println("ON DATE: \t" + flightDate + "\n");
         System.out.println("\tNUMBER OF SEATS OPEN:\t" + seatsOpen + "\n");
         System.out.println("\tNUMBER OF SEATS SOLD:\t" + seatsSold + "\n");
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
            System.out.println("TECHNICIAN: " + technicianID + " DOES NTO HAVE REPAIRS\n");
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
   //number of days the flight departed and arrived (how many flight instances happened for this flight), number of sold and unsold tickets
   public static void FlightStats(AirlineManagement esql) {
      try {
         System.out.print("Enter Flight Number: ");
         String flightNum = in.readLine();
         while(esql.executeQuery("SELECT * FROM Flight WHERE FlightNumber = '" + flightNum + "'") < 1) {
            System.out.println("FLIGHT DOES NOT EXIST. PLEASE GIVE A VALID FLIGHT NUMBER \n");
            System.out.print("Enter Flight Number: ");
            flightNum = in.readLine();
         }

         System.out.print("\nEnter a Date Range (M/D/YY): \n\n");
         System.out.print("Start Date: ");
         String startDate = in.readLine();
         System.out.print("\nEnd Date: ");
         String endDate = in.readLine();

         System.out.println("\nFLIGHT: " + flightNum + "\n");
         System.out.println("ON DATE RANGE FROM: " + startDate + " TO: "+ startDate +"\n");
      

         String query = "SELECT FlightNumber, FlightDate, SeatsTotal, SeatsSold, (SeatsTotal - SeatsSold) as SeatsOpen FROM FlightInstance WHERE FlightNumber = ";
         query += "'"+ flightNum + "' AND FlightDate >= '" + startDate + "' AND FlightDate <= '" + endDate + "'";

         List<List<String>> result = esql.executeQueryAndReturnResult(query);
         esql.executeQueryAndPrintResult(query);
         int totalUnsold = 0;
         int totalSold = 0;
         int totalFlights = 0;

         for(int i = 0; i < result.size(); ++i) {
            int seatsSold = Integer.parseInt(result.get(i).get(3));
            int seatsOpen = Integer.parseInt(result.get(i).get(4));
            totalSold += seatsSold;
            totalUnsold += seatsOpen;
            totalFlights += 1;
         }

         System.out.println("\n\tTOTAL FLIGHTS: " + totalFlights + "\n");
         System.out.println("\tTOTAL NUMBER OF UNSOLD TICKETS:\t" + totalUnsold + "\n");
         System.out.println("\tTOTAL NUMBER OF SOLD TICKETS:\t" + totalSold + "\n");

      } catch (Exception e) {
         System.err.println (e.getMessage());
      }
   }

   public static void MaintenaceRequest(AirlineManagement esql) {
      try {
         System.out.print("Enter Pilot ID: ");
         String pilotID = in.readLine();
         while(esql.executeQuery("SELECT * FROM Pilot WHERE pilotID = '" + pilotID + "'") < 1) {
            System.out.println("PILOT DOES NOT EXIST. PLEASE GIVE A VALID PILOT ID\n");
            System.out.print("Enter Pilot ID: ");
            pilotID = in.readLine();
         }

         System.out.print("\nEnter Plane ID: ");
         String planeID = in.readLine();
         while (esql.executeQuery("SELECT * FROM Plane WHERE planeID = '" + planeID + "'") < 1) {
            System.out.println("PLANE DOES NOT EXIST. PLEASE GIVE A VALID PLANE ID\n");
            System.out.print("\nEnter Plane ID: ");
            planeID = in.readLine();
         }

         System.out.print("\nEnter the Repair Code: ");
         String repairCode = in.readLine();

         System.out.print("\nEnter Date of Request(YYYY-MM-DD): ");
         String requestDate = in.readLine();



         int requestID =  esql.executeQuery("SELECT * FROM MaintenanceRequest") + 1;
         String query = "INSERT INTO MaintenanceRequest(RequestID, PlaneID, RepairCode, RequestDate, PilotID) VALUES ("+requestID+", '"+ planeID +"', '"+ repairCode +"', '"+ requestDate +"', '"+ pilotID +"')";
         System.out.println("\nMAKING REQUEST FOR:" + pilotID + " ON PLANE: " + planeID + "\n");
         esql.executeUpdate(query);

         String query2 = "SELECT * FROM MaintenanceRequest";
         esql.executeQueryAndPrintResult(query2);
      } catch (Exception e) {
         System.err.println (e.getMessage());
      }
   }


// ================================
// 2. Customer Management
// ================================
    // 
    public static void ViewDateFlights(AirlineManagement esql) {
        try {
            System.out.print("1. FLights on a given date === \n");
            System.out.print("Enter departure city: ");
            String departureCity = in.readLine();
            System.out.print("Enter arrival city: ");
            String arrivalCity = in.readLine();

            // Convert user date into machine date
            System.out.print("Enter date (M/D/YY): ");
            String departureDate = in.readLine();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yy");
            LocalDate machineDate = LocalDate.parse(departureDate, formatter);

            // Get DoW from machine date
            DayOfWeek dow = DayOfWeek.from(machineDate);
            String weekday = dow.toString();
            weekday = weekday.charAt(0) + weekday.substring(1).toLowerCase();

            // Using (F1##)(s), return the stats of the date chosen
            String query = "SELECT F.FlightNumber, S.DepartureTime, S.ArrivalTime, I.NumOfStops FROM Flight AS F, Schedule AS S, FlightInstance as I WHERE F.DepartureCity = '"
                    + departureCity + "' AND F.ArrivalCity = '" + arrivalCity
                    + "' AND I.FlightDate = '" + departureDate
                    + "' AND F.FlightNumber = I.FlightNumber AND F.FlightNumber = S.FlightNumber AND S.DayOfWeek = '"
                    + weekday + "'";

            int rowCount = esql.executeQuery(query);
            System.out.println("total row(s): " + rowCount);
            esql.executeQueryAndPrintResult(query);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void FindTicketPrice(AirlineManagement esql) {
        try {
            System.out.print("2. Ticket Price ===\n");
            System.out.print("Enter flight number (F1##): ");
            String flightNumber = in.readLine();
            System.out.print("Enter departure date (M/D/YY): ");
            String departureDate = in.readLine();

            String query = "SELECT TicketCost FROM FlightInstance WHERE FlightNumber = ";
            query += "'" + flightNumber + "' AND FlightDate = '" + departureDate + "'";

            int rowCount = esql.executeQuery(query);
            System.out.println("total row(s): " + rowCount);
            esql.executeQueryAndPrintResult(query);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void FindAirplaneType(AirlineManagement esql) {
        try {
            System.out.print("3. Airplane Type === \n");
            System.out.print("Enter flight number (F1##): ");
            String flightNumber = in.readLine();
            String query = "SELECT P.Make, P.MODEL FROM Plane as P, Flight as F WHERE P.PlaneID = F.PlaneID AND F.FlightNumber = ";
            query += "'" + flightNumber + "'";
            int rowCount = esql.executeQuery(query);
            System.out.println("total row(s): " + rowCount);
            esql.executeQueryAndPrintResult(query);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void MakeReservation(AirlineManagement esql) {
        try {
            System.out.print("Enter Customer ID: ");
            String customerID = in.readLine();
            System.out.print("\nEnter Flight Instance ID: ");
            String FlightInstanceID = in.readLine();

            // Select Total Seats and Sold Seats, see whether to reserve or not
            String seatQuery = "SELECT SeatsTotal, SeatsSold FROM FlightInstance WHERE FlightInstanceID = '"
                    + FlightInstanceID + "'";
            List<List<String>> result = esql.executeQueryAndReturnResult(seatQuery);
            int SeatsTotal = Integer.parseInt(result.get(0).get(0));
            int SeatsSold = Integer.parseInt(result.get(0).get(1));
            String Status = "reserved";
            if (SeatsTotal <= SeatsSold) {
                Status = "waitlist";
            }

            // Get biggest reservation, extract and create next RID
            String maxReservation = "SELECT MAX(ReservationID) FROM Reservation";
            List<List<String>> result2 = esql.executeQueryAndReturnResult(maxReservation);
            String maxReservationID = result2.get(0).get(0);
            int reservationNumber = Integer.parseInt(maxReservationID.substring(1)) + 1;
            String currentRID = String.format("R%04d", reservationNumber);

            String query = "INSERT INTO Reservation(ReservationID, CustomerID, FlightInstanceID, Status) VALUES ('"
                    + currentRID + "', '" + customerID + "', '" + FlightInstanceID + "', '" + Status + "')";
            esql.executeUpdate(query);
            System.out.print("Total Open Seats: " + (SeatsTotal - SeatsSold) + "\n");
            System.out.print("Status: " + Status);

            // Update number of seats available
            String updateSeatCount = "UPDATE FlightInstance SET SeatsSold = SeatsSold + 1 WHERE FlightInstanceID = '"
                    + FlightInstanceID + "'";
            esql.executeQuery(updateSeatCount);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void CancelReservation(AirlineManagement esql) {
        try {
            // Lmao no security
            System.out.print("Cancel Reservation === \n ");
            System.out.print("\nEnter ReservationID ID: ");
            String ReservationID = in.readLine();

            // Get FlightInstanceID to update seat count with later
            String flightQuery = "SELECT FlightInstanceID FROM Reservation WHERE ReservationID = '"
                    + ReservationID + "'";

            String query = "DELETE FROM Reservation WHERE ReservationID = '" + ReservationID + "'";
            esql.executeQuery(query);

            // Update number of seats available
            String updateSeatCount = "UPDATE FlightInstance SET SeatsSold = SeatsSold - 1 WHERE FlightInstanceID = '"
                    + flightQuery + "'";
            esql.executeQuery(updateSeatCount);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void ViewCurrentReservations(AirlineManagement esql) {
        try {
            System.out.print("Enter customer ID: ");
            String customerID = in.readLine();
            String query = "SELECT ReservationID, FlightInstanceID, Status FROM Reservation WHERE CustomerID = ";
            query += "'" + customerID + "'";
            int rowCount = esql.executeQuery(query);
            System.out.println("total row(s): " + rowCount);
            esql.executeQueryAndPrintResult(query);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

// ================================
// 4. Technician Management
// ================================
    public static void FindAllRepairs(AirlineManagement esql) {
        try {
            System.out.print("Enter Plane ID (PL###): ");
            String planeID = in.readLine();
            String query = "SELECT * FROM Repair WHERE PlaneID = ";
            query += "'" + planeID + "'";
            int rowCount = esql.executeQuery(query);
            System.out.println("total row(s): " + rowCount);
            esql.executeQueryAndPrintResult(query);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void FindAllPilotRequests(AirlineManagement esql) {
        try {
            System.out.println("Enter Plane ID (PL###): ");
            String planeID = in.readLine();
            String query = "SELECT * FROM MaintenanceRequest WHERE PlaneID = ";
            query += "'" + planeID + "'";
            int rowCount = esql.executeQuery(query);
            System.out.println("total row(s): " + rowCount);
            esql.executeQueryAndPrintResult(query);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

}//end AirlineManagement
