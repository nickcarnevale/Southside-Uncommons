import java.sql.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.LocalDate;

public class FinancialManager {

    public static void FinancialManagerInterface(Connection conn, Scanner scanner){
        boolean valid = true;
        
        System.out.print("You have selected the Financial Manager Interface.\n");
        do{
            valid = false;
            System.out.println("\nFINACIAL MANAGER ACTIONS:");
            System.out.println("1 -- Display Active Leases\n2 -- Get Total Revenue\n3 -- Exit\n");
            int choice = 0;
            if(scanner.hasNextInt()){
                choice = scanner.nextInt();
                scanner.nextLine();
                switch(choice){
                    case 3: 
                        System.out.println("\nReturning to Main Menu.");
                        return;
                    case 1:
                        viewLeases(conn, scanner);
                    break;
                    case 2:
                        totalRevenue(conn, scanner);
                    break;
                    default:
                        System.out.println("\nPlease Enter 1-3.\n");
                }
            }else{
                valid = false;
                scanner.nextLine();
            }
        }while(!valid);
    } 

    public static void totalRevenue(Connection conn, Scanner scanner){

        try{

            PreparedStatement getRevenue = conn.prepareStatement("SELECT SUM(Amount) AS Revenue FROM Payment");
            ResultSet rev = getRevenue.executeQuery();
            rev.next();

            System.out.print("\n\nTOTAL REVENUE\n-------------------\n");
            System.out.printf("$%d.00\n\n", rev.getInt("Revenue"));

            return;
        }catch(SQLException e){
            e.printStackTrace();
        }

    }

    public static void viewLeases(Connection conn, Scanner scanner){
        boolean valid = true;
        try{

            while(true){
                System.out.println("\nActive Leases: \n");

                System.out.println("ID\tSTART\t\tEND\t\tADDRESS\t\t\tRENT\n-------------------------------------------------------------------------");
                PreparedStatement findLeases = conn.prepareStatement("select * from lease l join apartment a on a.apartmentid = l.apartmentid join property p on a.propertyid = p.propertyid order by leaseid");
                ResultSet leases = findLeases.executeQuery();
                valid = leases.next();
                while(valid){
                    int id = leases.getInt("leaseid");
                    Date start = leases.getDate("startdate");
                    Date end = leases.getDate("enddate");
                    String addy = leases.getString("streetaddress") + " #" + leases.getInt("apartmentnumber");
                    int rent = leases.getInt("monthlyrent");

                    PreparedStatement findChaAmenities = conn.prepareStatement("select * from chargeableamenity ca join chargeableamenitylease caa on ca.chargeableamenityid = caa.chargeableamenityid join lease l on l.leaseid = caa.leaseid where l.leaseid = ?");
                    findChaAmenities.setInt(1, id);
                    ResultSet amenities = findChaAmenities.executeQuery();
                    valid = amenities.next();
                    while(valid){
                        rent += amenities.getInt("monthlycost");
                        valid = amenities.next();
                    }

                    System.out.printf("%d\t%s\t%s\t%s\t$%d.00\n", id, start, end, addy, rent);
                    valid = leases.next();
                }

                return;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        



    }
}
