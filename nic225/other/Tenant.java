import java.sql.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.LocalDate;

public class Tenant {

    public static void TenantInterface(Connection conn, Scanner scanner){
        boolean valid = true;
        int choice = 0;
        int personID = 0; 
        int tenantID = 0;
        
        //Initial Log In
        do{
            System.out.println("You have selected Tenant Login.\nHere is a list of our current Tenants:");
            System.out.println("\nID\tName");
            System.out.println("----------------------------");
            
            try{
                PreparedStatement getTenants = conn.prepareStatement("select * from tenant natural join person order by tenantid");
                ResultSet tenants;
                tenants = getTenants.executeQuery();
                valid = tenants.next();
                while(valid){
                    System.out.printf("%d\t%s, %s\n", tenants.getInt("tenantid"), tenants.getString("lastname"), tenants.getString("firstname"));
                    valid = tenants.next();
                }

                System.out.print("\nWhich Tenant would you like to select (enter id, 0 to exit): ");
                choice = getInt(scanner);

                if(choice == 0){
                    return;
                }
            
                PreparedStatement findTenant = conn.prepareStatement("select * from Person inner join Tenant on Person.PersonID = Tenant.PersonID where Tenant.tenantID = ?");
                ResultSet tenant;
                findTenant.setInt(1, choice);
                tenant = findTenant.executeQuery();
                valid = tenant.next(); 
                if (valid) {
                    personID = tenant.getInt("PersonID");
                    tenantID = tenant.getInt("TenantID"); 
                    System.out.printf("\n\nWelcome %s, %s!\n", tenant.getString("LastName"), tenant.getString("FirstName")); 
                } else {
                    System.out.println("\nNot in our database.\nPlease try again.\n");
                    valid = false;
                }
            }catch (SQLException e) {
                e.printStackTrace();
                valid = false;
            }
        }while(!valid);

        //Tenant Menu
        do{
            valid = false;
            System.out.println("\nTENANT MENU:");
            //want to add, add dependant, add pet, set a move out date
            System.out.println("1 -- View Profile\n2 -- Update Profile\n3 -- View Lease\n4 -- Check Payment Status\n5 -- Make Payment\n6 -- Edit Dependants\n7 -- Edit Pets\n8 -- Exit\n");
            int c = 0;
            if(scanner.hasNextInt()){
                c = scanner.nextInt();
                scanner.nextLine();
                switch(c){
                    case 8: 
                        System.out.println("\nReturning to Main Menu.");
                        return;
                    case 1:
                        viewProfile(conn, scanner, personID);
                    break;
                    case 2:
                        updateProfile(conn, scanner, personID);
                    break;
                    case 3:
                        viewLease(conn, scanner, tenantID);
                    break;
                    case 4:
                        checkPaymentStatus(conn, scanner, tenantID);
                    break;
                    case 5: 
                        makePayment(conn, scanner, tenantID);
                    break;
                    case 6:
                        editDependants(conn, scanner, tenantID);
                    break;
                    case 7: 
                        editPets(conn, scanner, tenantID); 
                    default:
                        System.out.println("\nPlease Enter 1-8.\n");
                }
            }else{
                valid = false;
                scanner.nextLine();
            }
        }while(!valid);
    } 

    public static void viewProfile(Connection conn, Scanner scanner, int personID){
        try{
                PreparedStatement findTenant = conn.prepareStatement("select * from Person inner join Tenant on Person.PersonID = Tenant.PersonID where person.personID = ?");
                ResultSet tenant;
                findTenant.setInt(1, personID);
                tenant = findTenant.executeQuery();
                boolean valid = tenant.next(); 
                if (valid) {
                    System.out.println("\nThis is your current profile:");
                    System.out.println("First Name: \t" + tenant.getString("FirstName"));
                    System.out.println("Last Name: \t" + tenant.getString("LastName"));
                    System.out.println("Phone Number: \t" + tenant.getString("PhoneNumber"));
                    System.out.println("Email: \t\t" + tenant.getString("Email"));
                    System.out.println("Date Of Birth: \t" + tenant.getDate("DateOfBirth"));  
                } else {
                    System.out.println("There was an error in retrieving your profile.");
                }
            }catch (SQLException e) {
                e.printStackTrace();
            }
    }

    public static void updateProfile(Connection conn, Scanner scanner, int personID){
        boolean valid;
        do{
            valid = true;
            System.out.println("\nUPDATE PROFILE (select which field you would like to change):");
            System.out.println("1 -- Firstname\n2 -- Lastname\n3 -- Phone Number\n4 -- Email\n5 -- Exit\n");
            int choice = 0;
            if(scanner.hasNextInt()){
                choice = scanner.nextInt();
                scanner.nextLine();
                try{
                    switch(choice){
                        case 5: 
                            System.out.println("\nReturning to Tenant Menu.");
                            return;
                        case 1:
                            PreparedStatement updatefn = conn.prepareStatement("update Person set firstname = ? where PersonID = ?");
                            System.out.print("Enter Firstname: ");
                            String fn = scanner.nextLine();
                            updatefn.setString(1, fn);
                            updatefn.setInt(2,personID);
                            updatefn.executeQuery();
                            System.out.printf("Updated Firstname Successfully to %s!\n", fn);
                        break;
                        case 2:
                            PreparedStatement updateln = conn.prepareStatement("update Person set lastname = ? where PersonID = ?");
                            System.out.print("Enter Lastmname: ");
                            String ln = scanner.nextLine();
                            updateln.setString(1, ln);
                            updateln.setInt(2,personID);
                            updateln.executeQuery();
                            System.out.printf("Updated Lastname Successfully to %s!\n", ln);
                        break;
                        case 3: 
                            PreparedStatement updatepn = conn.prepareStatement("update Person set phonenumber = ? where PersonID = ?");
                            System.out.print("Enter Phone Number: ");
                            String pn = getValidPhoneNumber(scanner);
                            updatepn.setString(1, pn);
                            updatepn.setInt(2,personID);
                            updatepn.executeQuery();
                            System.out.printf("Updated Phone Number Successfully to %s!\n", pn);
                        break;
                        case 4:
                            PreparedStatement updatee = conn.prepareStatement("update Person set email = ? where PersonID = ?");
                            System.out.print("Enter Email: ");
                            String e = getValidEmail(scanner);
                            updatee.setString(1, e);
                            updatee.setInt(2,personID);
                            updatee.executeQuery();
                            System.out.printf("Updated Email Successfully to %s!\n", e);   
                        break;
                        default:
                            System.out.println("\nPlease Enter 1, 2, 3, 4 or 5.\n");
                    }
                }catch (SQLException e) {
                    e.printStackTrace();
                    valid = false;
                }
            }else{
                    valid = false;
                    scanner.nextLine();
                }
        }while(!valid);
    }

    /*

        Reference: 
        https://howtodoinjava.com/java/regex/java-regex-validate-email-address/

        I used to source to help me understand regex and the pattern and matcher classes.

    */
    public static String getValidEmail(Scanner scanner) {
        boolean valid = true; 
        do{
            String email = scanner.nextLine();
            //Regex was taken directly from the website.
            String regex = "^[a-zA-Z0-9_!#$%&amp;'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&amp;'*+/=?`{|}~^-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(email);
            if(matcher.matches()){
                return email;
            }else{
                System.out.print("\nPlease enter a valid email: ");
                valid = false;
            }
        }while(!valid);   
        return "Error";
    }

    /*

        Reference: 
        https://howtodoinjava.com/java-regular-expression-tutorials/

        I used their regular expression tutorial to apply this to phone numbers. 

    */
    public static String getValidPhoneNumber(Scanner scanner) {
        boolean valid = true; 
        do{
            String phoneNumber = scanner.nextLine();
            String regex = "^\\d{3}-\\d{3}-\\d{4}$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(phoneNumber);
            if(matcher.matches()){
                return phoneNumber;
            }else{
                System.out.print("\nPlease enter a valid Phone Number (xxx-xxx-xxxx): ");
                valid = false;
            }
        }while(!valid);  
        return "Error"; 
    }

    public static String getValidPhoneNumberForLogIn(Scanner scanner) {
        boolean valid = true; 
        do{
            String phoneNumber = scanner.nextLine();
            //this if statement is the only difference
            if(phoneNumber.equals("0")){
                return phoneNumber;
            }
            String regex = "^\\d{3}-\\d{3}-\\d{4}$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(phoneNumber);
            if(matcher.matches()){
                return phoneNumber;
            }else{
                System.out.print("\nPlease enter a valid Phone Number (xxx-xxx-xxxx): ");
                valid = false;
            }
        }while(!valid);  
        return "Error"; 
    }

    public static void viewLease(Connection conn, Scanner scanner, int tenantID){
        boolean valid = true;
        int totalRent = 0;
        try{
            System.out.println("\nYour Lease Agreement");
            System.out.println("--------------------\n");


            PreparedStatement findLease = conn.prepareStatement("select cal.LeaseID, cal.ChargeableAmenityID, ca.*, l.*, a.*, p.* from lease l left join ChargeableAmenityLease cal on cal.LeaseID = l.LeaseID left join ChargeableAmenity ca on cal.ChargeableAmenityID = ca.ChargeableAmenityID left join Apartment a on l.ApartmentID = a.ApartmentID left join property p on a.PropertyID = p.PropertyID where l.tenantId = ?");
            ResultSet lease;
            findLease.setInt(1, tenantID);
            lease = findLease.executeQuery();
            valid = lease.next();
            
            
            if(valid){
                System.out.printf("%s %s, %s, %s\n", lease.getString("streetaddress"), lease.getString("town"), lease.getString("state"), lease.getString("zipcode"));
                System.out.println("Apartment: " + lease.getInt("apartmentnumber"));
                System.out.printf("\nTerms:\n%s - %s\n", lease.getDate("startdate"), lease.getDate("enddate"));
                System.out.printf("Security Deposit: $%.2f\n", (float) lease.getInt("securitydeposit"));
                System.out.printf("Base Monthly Rent: $%.2f\n", (float) lease.getInt("monthlyrent")); 
                System.out.printf("\n%d Bedrooms and %.1f Bathrooms\n", lease.getInt("Bedrooms"), lease.getFloat("Bathrooms"));
                System.out.printf("Square Footage: %d\n", lease.getInt("apartmentsize"));
                totalRent += lease.getInt("monthlyrent");
            }
            

            System.out.println("\nPurchased Chargeable Amenities");
            System.out.println("------------------------------");
            while(valid){
                if(lease.getString("name") != null){
                    totalRent += lease.getInt("monthlycost");
                    System.out.printf("%s\t\t$%.2f per month\n",lease.getString("name"), (float) lease.getInt("monthlycost"));
                    System.out.printf("%s\n\n", lease.getString("description"));
                }else{ 
                    System.out.println("None on file.");
                }
                valid = lease.next();
            } 
            System.out.printf("\nTOTAL MONTHLY RENT: $%.2f\n", (float) totalRent);   
                
            
        }catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void checkPaymentStatus(Connection conn, Scanner scanner, int tenantID){
        boolean valid = true;
        int leaseID = 0;
        int monthlyRent = 0;
        

        try{

            Date lastPaymentDate = Date.valueOf("2023-08-15");
            Date comp = Date.valueOf("2023-08-15"); 
            LocalDate currentDate = LocalDate.now();
            LocalDate fifteenthDay = currentDate.withDayOfMonth(15);
            Date fifteenthOfCurrentMonth = Date.valueOf(fifteenthDay);

            PreparedStatement findStartDate = conn.prepareStatement("select * from lease where tenantid = ?");
            findStartDate.setInt(1, tenantID);
            ResultSet findsd = findStartDate.executeQuery();
            findsd.next();
            Date startDate = findsd.getDate("startdate");
            LocalDate get15th = startDate.toLocalDate();
            LocalDate localDate = get15th.withDayOfMonth(15);
            Date fifteenthOfStartMonth = Date.valueOf(localDate);



            PreparedStatement findPayments = conn.prepareStatement("select * from lease left join payment on payment.tenantid = lease.tenantid where lease.tenantid = ? order by COALESCE(payment.paymentdate, TO_DATE('1970-01-01', 'YYYY-MM-DD'))");            
            ResultSet payments;
            findPayments.setInt(1, tenantID);
            payments = findPayments.executeQuery();
            valid = payments.next();
            leaseID = payments.getInt("leaseID");
            monthlyRent = payments.getInt("monthlyrent");
            
            //get first payment date
            Date firstPayment = payments.getDate("startdate");
            LocalDate firstPaymentLocal = firstPayment.toLocalDate();
            LocalDate editted = firstPaymentLocal.withDayOfMonth(15);
            firstPayment = Date.valueOf(editted);


            System.out.println("\nSTATEMENTS PAID:\n\nDate\t\tAmount\t\tMethod");
            System.out.println("--------------------------------------");
            while(valid){
                if(payments.getInt("security") == 1){
                    System.out.printf("%s\t$%.2f\t%s\t%s\n", payments.getDate("PaymentDate"), (float) payments.getInt("Amount"), payments.getString("paymentmethod"), "Security Deposit");
                    valid = payments.next();
                    continue;
                }
                if(payments.getDate("PaymentDate") == null){
                    System.out.println("No payments have been made.\n");
                    break;
                }
                lastPaymentDate = payments.getDate("PaymentDate");
                if(payments.getInt("Amount") < 1000){
                    System.out.printf("%s\t$%.2f\t\t%s\n", lastPaymentDate, (float) payments.getInt("Amount"), payments.getString("paymentmethod"));
                }else{
                    System.out.printf("%s\t$%.2f\t%s\n", lastPaymentDate, (float) payments.getInt("Amount"), payments.getString("paymentmethod"));
                }
                valid = payments.next();
            } 

            //get monthly rent if there is chargeable amentities added on
            PreparedStatement findMonthlyRent = conn.prepareStatement("select * from ChargeableAmenityLease cal join ChargeableAmenity ca on cal.ChargeableAmenityID = ca.ChargeableAmenityID where cal.LeaseID = ?");
            ResultSet cAmenities;
            findMonthlyRent.setInt(1, leaseID);
            cAmenities = findMonthlyRent.executeQuery();
            valid = cAmenities.next();
            while(valid){
                monthlyRent += cAmenities.getInt("monthlycost");
                valid = cAmenities.next();
            }

            System.out.println("\nSTATEMENTS DUE:\n\nDate\t\tMonthlyRent");
            System.out.println("---------------------------");

            if(fifteenthOfStartMonth.compareTo(fifteenthOfCurrentMonth) >= 0){
                System.out.println("Nothing is outstanding. Your First Statement is Due:");
                System.out.printf("%s\t$%.2f\n",fifteenthOfStartMonth, (float) monthlyRent);
            }else{
                if(lastPaymentDate.compareTo(fifteenthOfCurrentMonth) >= 0){
                    System.out.println("None, your monthly statement has been paid.\n");
                }else{
                    int counter = 1;
                    while(lastPaymentDate.compareTo(fifteenthOfCurrentMonth) < 0){
                        System.out.printf("%s\t$%.2f\n",fifteenthOfCurrentMonth, (float) monthlyRent);
                        //calculate if they paid the previous month
                        LocalDate previousMonth15th = currentDate.minusMonths(counter).withDayOfMonth(15);
                        counter++;
                        fifteenthOfCurrentMonth = Date.valueOf(previousMonth15th);
                        
                        //if there was no payment at all - to avoid infinite loop
                        if(lastPaymentDate.compareTo(firstPayment) < 0 && fifteenthOfCurrentMonth.compareTo(firstPayment) == 0){
                            System.out.printf("%s\t$%.2f\n",fifteenthOfCurrentMonth, (float) monthlyRent);
                            break;
                        }
                    }
                    if(counter >= 4){
                        System.out.println("\n3 or more payments have been missed.\nWe will be reaching out to you shortly with an eviction \nnotice if outstanding payments persist.");
                    }
                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public static void makePayment(Connection conn, Scanner scanner, int tenantID){
        boolean valid = true;
        int leaseID = 0;
        int monthlyRent = 0;
        Date lastPaymentDate = Date.valueOf("2023-08-15");
        LocalDate currentDate = LocalDate.now();
        LocalDate fifteenthDay = currentDate.withDayOfMonth(15);
        Date fifteenthOfCurrentMonth = Date.valueOf(fifteenthDay);

        try{
            PreparedStatement findStartDate = conn.prepareStatement("select * from lease where tenantid = ?");
            findStartDate.setInt(1, tenantID);
            ResultSet findsd = findStartDate.executeQuery();
            findsd.next();
            Date startDate = findsd.getDate("startdate");
            LocalDate get15th = startDate.toLocalDate();
            LocalDate localDate = get15th.withDayOfMonth(15);
            Date fifteenthOfStartMonth = Date.valueOf(localDate);

            PreparedStatement findPayments = conn.prepareStatement("select * from lease left join payment on payment.tenantid = lease.tenantid where lease.tenantid = ? order by COALESCE(payment.paymentdate, TO_DATE('1970-01-01', 'YYYY-MM-DD'))");            
            ResultSet payments;
            findPayments.setInt(1, tenantID);
            payments = findPayments.executeQuery();
            valid = payments.next();
            leaseID = payments.getInt("leaseID");
            monthlyRent = payments.getInt("monthlyrent");
            
            //get first payment date
            Date firstPayment = payments.getDate("startdate");
            LocalDate firstPaymentLocal = firstPayment.toLocalDate();
            LocalDate editted = firstPaymentLocal.withDayOfMonth(15);
            firstPayment = Date.valueOf(editted);
            while(valid){
                if(payments.getInt("security") == 1){
                    valid = payments.next();
                    continue;
                }
                if(payments.getString("PaymentMethod") != null){
                    lastPaymentDate = payments.getDate("PaymentDate");
                    valid = payments.next();
                }else{
                    break;
                }
                
            } 

            //get monthly rent if there is chargeable amentities added on
            PreparedStatement findMonthlyRent = conn.prepareStatement("select * from ChargeableAmenityLease cal join ChargeableAmenity ca on cal.ChargeableAmenityID = ca.ChargeableAmenityID where cal.LeaseID = ?");
            ResultSet cAmenities;
            findMonthlyRent.setInt(1, leaseID);
            cAmenities = findMonthlyRent.executeQuery();
            valid = cAmenities.next();
            while(valid){
                monthlyRent += cAmenities.getInt("monthlycost");
                valid = cAmenities.next();
            }

            System.out.println("\nDISCLAIMER:\nStatements need to be paid in full. If more than one outstanding \nstatement exists, statements need to be paid one at a time.\nThank you, Management.\n");
            System.out.println("\nOutstanding Statements:\n\nDate\t\tMonthlyRent");
            System.out.println("---------------------------");

            if(fifteenthOfStartMonth.compareTo(fifteenthOfCurrentMonth) >= 0){
                System.out.println("Nothing is outstanding. Your First Statement is Due:");
                System.out.printf("%s\t$%.2f\n",fifteenthOfStartMonth, (float) monthlyRent);
                System.out.println("\nWe do not accept payment until the month due. Thank you.\n");

            }else{
                if(lastPaymentDate.compareTo(fifteenthOfCurrentMonth) >= 0){
                    System.out.println("No Payments need to be made at this time.\n");
                    return;
                }else{
                    int counter = 1;
                    while(lastPaymentDate.compareTo(fifteenthOfCurrentMonth) < 0){
                        System.out.printf("%s\t$%.2f\n",fifteenthOfCurrentMonth, (float) monthlyRent);
                        //calculate if they paid the previous month
                        LocalDate previousMonth15th = currentDate.minusMonths(counter).withDayOfMonth(15);
                        counter++;
                        fifteenthOfCurrentMonth = Date.valueOf(previousMonth15th);
                        
                        //if there was no payment at all - to avoid infinite loop
                        if(lastPaymentDate.compareTo(firstPayment) < 0 && fifteenthOfCurrentMonth.compareTo(firstPayment) == 0){
                            System.out.printf("%s\t$%.2f\n",fifteenthOfCurrentMonth, (float) monthlyRent);
                            //so the adjustment still works
                            previousMonth15th = previousMonth15th.minusMonths(1).withDayOfMonth(15);
                            fifteenthOfCurrentMonth = Date.valueOf(previousMonth15th);
                            break;
                        }
                        
                    }
            
                    System.out.println("\n\nWould you like to pay your longest outstanding statement?");
                    System.out.println("1 -- Yes\n2 -- No\n");
                    do{
                        int choice = 0;
                        if(scanner.hasNextInt()){
                            choice = scanner.nextInt();
                            scanner.nextLine();
                            valid = false;
                            switch(choice){
                                case 1: 
                                    int amount = 0;
                                    String method = "";
                                    String cvv = "";
                                    do{
                                        System.out.print("Please enter the amount: ");
                                        if(scanner.hasNextInt()){
                                            amount = scanner.nextInt();
                                            scanner.nextLine();
                                            if(amount == monthlyRent){
                                                valid = true;
                                            }else{
                                                System.out.println("Please pay the exact amount for one statement.\n");
                                                valid = false;
                                            }
                                        }else{
                                            System.out.println("Please enter an integer. (No Decimals)\n");
                                            valid = false;
                                            scanner.nextLine();
                                        }
                                    }while(!valid);

                                        
                                    method = getValidCreditCard(scanner);
                                    System.out.println("We will not store your CVV in the database, but we need it to execute payment.");
                                    cvv = getValidCVV(scanner);  

                                    System.out.println("Processing Payment...");

                                    PreparedStatement getPaymentID = conn.prepareStatement("SELECT * FROM Payment ORDER BY PAYMENTID DESC");
                                    PreparedStatement pay = conn.prepareStatement("INSERT INTO Payment (PAYMENTID, TENANTID, LEASEID, PAYMENTDATE, AMOUNT, PAYMENTMETHOD, SECURITY) VALUES (?, ?, ?, ?, ?, ?, ?)");
                                    ResultSet paymentID = getPaymentID.executeQuery();
                                    paymentID.next();
                                    int nextID = 1;
                                    nextID += paymentID.getInt("PaymentID");

                                    pay.setInt(1, nextID);
                                    pay.setInt(2, tenantID);
                                    pay.setInt(3, leaseID);
                                    LocalDate adjustment = fifteenthOfCurrentMonth.toLocalDate();
                                    adjustment = adjustment.plusMonths(1).withDayOfMonth(15);
                                    fifteenthOfCurrentMonth = Date.valueOf(adjustment);
                                    pay.setDate(4, fifteenthOfCurrentMonth);
                                    pay.setInt(5, amount);
                                    pay.setString(6, method);
                                    pay.setInt(7, 0);

                                    pay.executeQuery();

                                    System.out.println("Payment Successful!\n");
                                    return;   
                                case 2:
                                    System.out.println("\nOkay, Returning to Tenant Menu.");
                                    return;
                                default:
                                    System.out.println("\nPlease Enter 1 or 2.\n");
                            }
                        }else{
                            valid = false;
                            scanner.nextLine();
                            System.out.println("\nPlease Enter 1 or 2.\n");
                        }
                    }while(!valid);
                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static int getInt(Scanner scanner){
        boolean valid = false;
        do{
            valid = false;
            int choice = 0;
            if(scanner.hasNextInt()){
                choice = scanner.nextInt();
                scanner.nextLine();
                return choice;
            }else{
                System.out.println("Please enter an integer: ");
                valid = false;
                scanner.nextLine();
            }
        }while(!valid);
        return -1;
    }

    public static void editDependants(Connection conn, Scanner scanner, int tenantID){
        boolean valid = true;
        int last = 0;
        int choice = 0;
        try{
        do{
            last = 0;
            valid = false;
            System.out.print("\nMENU\n------\n1 -- Add Dependant \n2 -- Delete Dependant\n3 -- Exit\n\n:");
            int c = 0;
            if(scanner.hasNextInt()){
                c = scanner.nextInt();
                scanner.nextLine();
                switch(c){
                    case 3: 
                        System.out.println("\nReturning to Tenant Menu.");
                        return;
                    case 1:
                        System.out.print("Your Current Depedants:\n\nID\tName\n----------------------------\n");
                        PreparedStatement findDependants = conn.prepareStatement("select * from dependant d join tenant t on  d.tenantid = t.tenantid where d.tenantId = ?  order by dependantid");
                        ResultSet dependants;
                        findDependants.setInt(1, tenantID);
                        dependants = findDependants.executeQuery();
                        valid = dependants.next();
                        while(valid){
                            System.out.printf("%d\t%s\n", dependants.getInt("dependantid"), dependants.getString("name"));
                            valid = dependants.next();
                        }

                        PreparedStatement findID = conn.prepareStatement("select * from dependant order by dependantid desc");
                        ResultSet getid = findID.executeQuery();
                        getid.next();
                        last = getid.getInt("dependantid");
                        last++;


                        System.out.print("\nName of dependant you would like to add (0 to exit): ");
                        String name = scanner.nextLine(); 
                        if(name.equals("0")){
                            break;
                        }else{
                            PreparedStatement addDependant = conn.prepareStatement("insert into Dependant (dependantid, tenantid, name) values (?,?,?)");
                            addDependant.setInt(1, last);
                            addDependant.setInt(2, tenantID);
                            addDependant.setString(3, name);
                            addDependant.executeQuery();

                            System.out.println("Successfully added!");
                            return;
                        }
                    case 2:

                        System.out.print("Your Current Depedants:\n\nID\tName\n----------------------------\n");
                        PreparedStatement findDependants2 = conn.prepareStatement("select * from dependant d join tenant t on  d.tenantid = t.tenantid where d.tenantId = ?  order by dependantid");
                        ResultSet dependants2;
                        findDependants2.setInt(1, tenantID);
                        dependants2 = findDependants2.executeQuery();
                        valid = dependants2.next();
                        while(valid){
                            System.out.printf("%d\t%s\n", dependants2.getInt("dependantid"), dependants2.getString("name"));
                            valid = dependants2.next();
                        }
                        

                        System.out.print("\nID of tenant you would like to delete (0 to exit): ");
                        
                        choice = getInt(scanner);
                        if(choice == 0){
                            break;
                        }else{
                            PreparedStatement validate = conn.prepareStatement("select * from dependant d join tenant t on  d.tenantid = t.tenantid where d.dependantid = ?");
                            ResultSet v;
                            validate.setInt(1, choice);
                            v = validate.executeQuery();
                            if(v.next()){
                                PreparedStatement delete = conn.prepareStatement("delete from dependant where dependantid = ?");
                                delete.setInt(1, choice);
                                delete.executeQuery();
                                System.out.println("Successfully deleted!");
                                return;
                            }else{
                                System.out.println("No Dependant with that id.");
                                break;
                            }
                        }
                        
                        
                    default:
                        System.out.println("\nPlease Enter 1-3.\n");
                }
            }else{
                System.out.println("Please enter an integer.");
                valid = false;
                scanner.nextLine();
            }
        }while(!valid);   
        }catch (SQLException e) {
            e.printStackTrace();
        }     
    }

    public static void editPets(Connection conn, Scanner scanner, int tenantID){
        boolean valid = true;
        int last;
        int choice = 0;
        try{
        do{
            last = 0;
            valid = false;
            System.out.print("\nMENU\n------\n1 -- Add Pet \n2 -- Delete Pet\n3 -- Exit\n\n:");
            int c = 0;
            if(scanner.hasNextInt()){
                c = scanner.nextInt();
                scanner.nextLine();
                switch(c){
                    case 3: 
                        System.out.println("\nReturning to Tenant Menu.");
                        return;
                    case 1:
                        System.out.print("Your Current Pets:\n\nID\tType\tName\n----------------------------\n");
                        PreparedStatement findPets = conn.prepareStatement("select * from pet2 p join tenant t on  p.tenantid = t.tenantid where p.tenantId = ? order by petid");
                        ResultSet pets;
                        findPets.setInt(1, tenantID);
                        pets = findPets.executeQuery();
                        valid = pets.next();
                        while(valid){
                            System.out.printf("%d\t%s\t%s\n", pets.getInt("petid"), pets.getString("type"), pets.getString("name"));
                            valid = pets.next();
                        }
                        System.out.print("\nType of pet you would like to add (0 to exit): ");
                        String type = scanner.nextLine(); 
                        if(type.equals("0")){
                            break;
                        }else{
                            System.out.print("\nName of your pet: ");
                            String name = scanner.nextLine();

                            PreparedStatement addPet = conn.prepareStatement("INSERT INTO Pet2 (Name, Type, TenantID) VALUES (?, ?, ?)");
                            addPet.setString(1, name);
                            addPet.setString(2, type);
                            addPet.setInt(3, tenantID);

                            addPet.executeQuery();


                            System.out.println("Successfully added!");
                            return;
                        }
                    case 2:

                        System.out.print("Your Current Pets:\n\nID\tType\tName\n----------------------------\n");
                        PreparedStatement findPets2 = conn.prepareStatement("select * from pet2 p join tenant t on  p.tenantid = t.tenantid where p.tenantId = ? order by petid");
                        ResultSet pets2;
                        findPets2.setInt(1, tenantID);
                        pets2 = findPets2.executeQuery();
                        valid = pets2.next();
                        while(valid){
                            System.out.printf("%d\t%s\t%s\n", pets2.getInt("petid"), pets2.getString("type"), pets2.getString("name"));
                            valid = pets2.next();
                        }
                        last++;
                        
                        System.out.print("\nID of pet you would like to delete (0 to exit): ");
                        
                        choice = getInt(scanner);
                        if(choice == 0){
                            break;
                        }else{
                            PreparedStatement validate = conn.prepareStatement("select * from pet2 d join tenant t on d.tenantid = t.tenantid where d.petid = ?");
                            ResultSet v;
                            validate.setInt(1, choice);
                            v = validate.executeQuery();
                            if(v.next()){
                                PreparedStatement delete = conn.prepareStatement("delete from pet2 where petid = ?");
                                delete.setInt(1, choice);
                                delete.executeQuery();
                                System.out.println("Successfully deleted!");
                                return;
                            }else{
                                System.out.println("No Pet with that id.");
                                break;
                            }
                        }
                        
                        
                    default:
                        System.out.println("\nPlease Enter 1-3.\n");
                }
            }else{
                System.out.println("Please enter an integer.");
                valid = false;
                scanner.nextLine();
            }
        }while(!valid);   
        }catch (SQLException e) {
            e.printStackTrace();
        }     
    }

    public static String getValidCreditCard(Scanner scanner) {
        boolean valid;
        String creditCard;

        do {
            System.out.print("\nPlease enter credit/debit card number (xxxx-xxxx-xxxx-xxxx): ");
            creditCard = scanner.nextLine().trim();

            // Credit card regex pattern
            String creditCardRegex = "^(\\d{4}-){3}\\d{4}$";
            Pattern pattern = Pattern.compile(creditCardRegex);
            Matcher matcher = pattern.matcher(creditCard);

            valid = matcher.matches();

            if (!valid) {
                System.out.println("\nInvalid credit card number format. Please enter a valid format.");
            }

        } while (!valid);

        return creditCard;
    }

    public static String getValidCVV(Scanner scanner) {
        boolean valid;
        String cvv;

        do {
            System.out.print("\nPlease enter CVV (3 or 4 digits): ");
            cvv = scanner.nextLine().trim();

            // CVV regex pattern
            String cvvRegex = "^[0-9]{3,4}$";
            Pattern pattern = Pattern.compile(cvvRegex);
            Matcher matcher = pattern.matcher(cvv);

            valid = matcher.matches();

            if (!valid) {
                System.out.println("\nInvalid CVV format. Please enter a valid format.");
            }

        } while (!valid);

        return cvv;
    }

}