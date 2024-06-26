import java.sql.*;
import java.util.*;

public class App {

    //Lehigh University Database URL
    static final String DB_URL = "jdbc:oracle:thin:@edgar1.cse.lehigh.edu:1521:cse241";

    public static void main(String[] args){

        //initialize connection and scanner
        Connection conn = null;
        Scanner scanner = new Scanner(System.in);
        
        do {
            try { 
                //get username and password
                System.out.print("\nEnter Oracle user id: ");
                String user = scanner.nextLine();
                System.out.print("Enter Oracle password: ");
                String pass = scanner.nextLine();

                //initalize the connection to the database
                conn = DriverManager.getConnection(DB_URL, user, pass);
                System.out.println("You have connected successfully.\n");
                
                //initialize variables
                boolean valid = true;

                System.out.println("\nWelcome to EastSide UnCommons!");
                do{
                    System.out.println("\nINTERFACE MENU:");
                    System.out.println("1 -- Tenant\n2 -- Property Manager\n3 -- Financial Manager\n4 -- Exit\n");
                    int choice = 0;
                    if(scanner.hasNextInt()){
                        choice = scanner.nextInt();
                        scanner.nextLine();
                        valid = false;
                        switch(choice){
                            case 4: 
                                System.out.println("\nThank You, Come Again!");
                                valid = true;
                            break;
                            case 1:
                                Tenant.TenantInterface(conn, scanner);
                            break;
                            case 2:
                                PropertyManager.PropertyManagerInterface(conn, scanner);
                            break;
                            case 3: 
                                FinancialManager.FinancialManagerInterface(conn, scanner);
                            break;
                            default:
                                System.out.println("\nPlease Enter 1, 2, 3 or 4.\n");
                        }
                    }else{
                        valid = false;
                        scanner.nextLine();
                    }
                }while(!valid);

                //close the connection
                conn.close();
                
                //terminate the program
                System.exit(0);
            } catch (SQLException e){
                e.printStackTrace();
                System.out.println("\n[Error]: Connection Error. Re-enter login data.\n");
            }
        } while(conn == null);
     
    }

}