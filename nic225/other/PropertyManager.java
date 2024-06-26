import java.sql.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.LocalDate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class PropertyManager {

    public static void PropertyManagerInterface(Connection conn, Scanner scanner){
        boolean valid = true;
        
        System.out.print("You have selected the Property Manager Login.\n");
        //Tenant Menu
        do{
            valid = false;
            System.out.println("\nPROPERTY MANAGER ACTIONS:");
            System.out.println("1 -- Handle New Customer\n2 -- Handle Returning Customer\n3 -- Explore Properties\n4 -- Exit\n");
            int choice = 0;
            if(scanner.hasNextInt()){
                choice = scanner.nextInt();
                scanner.nextLine();
                switch(choice){
                    case 4: 
                        System.out.println("\nReturning to Main Menu.");
                        return;
                    case 1:
                        handleNewCustomer(conn, scanner);
                    break;
                    case 2:
                        handleExistingCustomer(conn, scanner);
                    break;
                    case 3:
                        exploreProperties(conn, scanner);
                    break;
                    default:
                        System.out.println("\nPlease Enter 1-4.\n");
                }
            }else{
                valid = false;
                scanner.nextLine();
            }
        }while(!valid);
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

    public static Date getPastDate(Scanner scanner) {
        boolean valid = true;
        String dateFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        sdf.setLenient(false);

        do {
            String dateString = scanner.nextLine();

            try {
                // Parse the input date
                java.util.Date utilDate = sdf.parse(dateString);

                // Check if the date is in the past
                if (utilDate.before(new java.util.Date())) {
                    // Convert to java.sql.Date
                    Date sqlDate = new Date(utilDate.getTime());
                    // If conversion successful, return the sqlDate
                    return sqlDate;
                } else {
                    System.out.print("Invalid date. Please enter a past date.\n");
                    valid = false;
                }
            } catch (ParseException e) {
                System.out.print("Invalid date format.\n");
                System.out.print("\nEnter a past date in the format " + dateFormat + ": ");
                valid = false;
            }
        } while (!valid);

        return null;
    }

    public static Date getFutureDate(Scanner scanner) {
        boolean valid = true;
        String dateFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        sdf.setLenient(false);

        do {
            String dateString = scanner.nextLine();

            try {
                // Parse the input date
                java.util.Date utilDate = sdf.parse(dateString);

                // Check if the date is in the future
                if (utilDate.after(new java.util.Date())) {
                    // Convert to java.sql.Date
                    Date sqlDate = new Date(utilDate.getTime());
                    // If conversion successful, return the sqlDate
                    return sqlDate;
                } else {
                    System.out.print("Invalid date. Please enter a future date.\n");
                    valid = false;
                }
            } catch (ParseException e) {
                System.out.print("Invalid date format.\n");
                System.out.print("\nEnter a future date in the format " + dateFormat + ": ");
                valid = false;
            }
        } while (!valid);

        return null;
    }

    public static void handleNewCustomer(Connection conn, Scanner scanner){
        boolean valid = true;
        String firstname = "";
        String lastname = "";
        String phoneNumber = "";
        String email = "";
        Date dob;
        int id = 0;
        
        try{

            System.out.println("Please enter your information.\n");
            System.out.print("Firstname: ");
            firstname = scanner.nextLine();
            System.out.print("Lastname: ");
            lastname = scanner.nextLine();
            System.out.print("Phone Number (xxx-xxx-xxxx): ");
            phoneNumber = getValidPhoneNumber(scanner);
            System.out.print("Email: ");
            email = getValidEmail(scanner);
            System.out.print("Date of Birth (yyyy-mm-dd): ");
            dob = getPastDate(scanner);

            PreparedStatement getNextId = conn.prepareStatement("select * from person order by personid desc");
            ResultSet _id = getNextId.executeQuery();
            _id.next();
            id = _id.getInt("personid");
            id++;

            PreparedStatement insertPerson = conn.prepareStatement("insert into person (PERSONID, FIRSTNAME, LASTNAME, PHONENUMBER, EMAIL, DATEOFBIRTH) VALUES (?, ?, ?, ?, ?, ?)");
            insertPerson.setInt(1, id);
            insertPerson.setString(2, firstname);
            insertPerson.setString(3, lastname);
            insertPerson.setString(4, phoneNumber);
            insertPerson.setString(5, email);
            insertPerson.setDate(6, dob);
            insertPerson.executeQuery();

            System.out.println("Successfully added to the database!");
            handleCustomer(conn, scanner, id);
            return;
        }catch (SQLException e){
                e.printStackTrace();
        }
    }

    public static void handleExistingCustomer(Connection conn, Scanner scanner){
        boolean valid = true;
        do{

        try{
            valid = false;
            int choice = 0;
            System.out.println("Existing Customers: ");
            System.out.println("ID\tNAME\n-------------------");
            PreparedStatement getPeople = conn.prepareStatement("select * from person left join tenant on person.personid = tenant.personid order by person.personid");
            ResultSet people;
            people = getPeople.executeQuery();
            valid = people.next();
            while(valid){
                if(people.getInt("tenantid")>0){
                    System.out.printf("%d\t%s, %s *\n", people.getInt("personid"), people.getString("lastname"), people.getString("firstname"));
                }else{
                    System.out.printf("%d\t%s, %s\n", people.getInt("personid"), people.getString("lastname"), people.getString("firstname"));
                }
                valid = people.next();
            }

            System.out.print("\nWhich Customer would you like to select (enter id, 0 to exit): ");
            choice = getInt(scanner);

            if(choice == 0){
                return;
            }

            PreparedStatement isTenant = conn.prepareStatement("select * from tenant natural join person where personid = ?");
            isTenant.setInt(1, choice);
            ResultSet tenant = isTenant.executeQuery();
            if(tenant.next()){
                System.out.println("\nExisting Tenant. Please use the Tenant Interface!");
                return;
            }

            PreparedStatement isPerson  = conn.prepareStatement("SELECT * FROM person LEFT JOIN tenant ON person.personid = tenant.personid WHERE tenant.personid IS NULL and person.personid = ?"); 
            ResultSet person;
            isPerson.setInt(1, choice);
            person = isPerson.executeQuery();
            if(person.next()){
                handleCustomer(conn, scanner, choice);
                return;
            }else{
                valid = false;
                System.out.println("Please enter an existing customers id. ");
            }
        }catch (SQLException e){
                e.printStackTrace();
        }
        }while(!valid);
    }

    public static void exploreProperties(Connection conn, Scanner scanner){
        boolean valid = true;
        int choice = 0;
        try{

            do{
                 
                System.out.println("List of Properties:\n");
                System.out.println("ID\tADDRESS\t\t\t\t\tTOTAL UNITS\n-----------------------------------------------------------");
                PreparedStatement getProperties = conn.prepareStatement("select * from property");
                ResultSet properties = getProperties.executeQuery();
                valid = properties.next();
                while(valid){
                    System.out.printf("%d\t%s, %s, %s, %s\t\t%d\n", properties.getInt("propertyid"), properties.getString("streetaddress"), properties.getString("town"), properties.getString("state"), properties.getString("zipcode"), properties.getInt("totalunits"));
                    valid = properties.next();
                }

                System.out.println("\nWhich property would you like to learn more about (0 to exit): ");

                choice = getInt(scanner);

                if(choice == 0){
                    return;
                }

                PreparedStatement isProperty = conn.prepareStatement("select * from Property where propertyid = ?");
                isProperty.setInt(1, choice);
                ResultSet property = isProperty.executeQuery();
                if(property.next()){
                    
                    System.out.printf("You have selected:\n%s, %s, %s, %s\n\n", property.getString("streetaddress"), property.getString("town"), property.getString("state"), property.getString("zipcode"));

                    System.out.println("The following common amenities are avaliable to the selected property:\n");
                    System.out.println("NAME\t\t\tDESCRIPTION\n--------------------------------------");
                    PreparedStatement findCommonAmenities = conn.prepareStatement("select * from commonAmenity c join commonamenityproperty cp on c.commonamenityid = cp.commonamenityid join property p on cp.propertyid = p.propertyid where p.propertyid = ?");
                    findCommonAmenities.setInt(1, choice);
                    ResultSet commonAmenities = findCommonAmenities.executeQuery();
                    valid = commonAmenities.next();
                    while(valid){
                        if(commonAmenities.getString("name").length() < 15){
                            System.out.printf("%s\t\t%s\n",commonAmenities.getString("name"), commonAmenities.getString("description"));

                        }else{
                            System.out.printf("%s\t%s\n",commonAmenities.getString("name"), commonAmenities.getString("description"));

                        }
                        valid = commonAmenities.next();
                    }

                    System.out.println("\nThe following apartments are avaliable to the selected property:\n");

                    System.out.println("APT_ID\tNUMBER\tBEDROOMS\tBATHROOMS\tSIZE\tRENT\n-----------------------------------------------------------------");
                    
                    PreparedStatement findApartments = conn.prepareStatement("select * from Property natural join apartment where propertyid = ?");
                    findApartments.setInt(1, choice);
                    ResultSet apartments = findApartments.executeQuery();
                    valid = apartments.next();
                    while(valid){
                        System.out.printf("%d\t%d\t%d\t\t%.1f\t\t%d\t$%d.00\n",apartments.getInt("apartmentid"),apartments.getInt("apartmentnumber"), apartments.getInt("bedrooms"), apartments.getFloat("bathrooms"), apartments.getInt("apartmentsize"),apartments.getInt("monthlyrent"));
                        valid = apartments.next();
                    }

                    System.out.println("\nWould you like to view the chargeable amentities related to a specific apartment?");
                    
                    do{
                        System.out.println("Enter the APT_ID to view (0 to go back): ");

                        int apt = getInt(scanner);

                        if(apt == 0){
                            break;
                        }

                        PreparedStatement checkApartment = conn.prepareStatement("select * from Property natural join apartment where propertyid = ? and apartmentid = ?");
                        checkApartment.setInt(1, choice);
                        checkApartment.setInt(2, apt);
                        ResultSet check = checkApartment.executeQuery();
                        if(check.next()){
                            System.out.println("\nThe following chargeable amenities are avaliable to the selected apartment:\n");
                            System.out.println("NAME\t\t\t\tMONTHLYPRICE\t\tDESCRIPTION\n------------------------------------------------------------------");
                            PreparedStatement findCAmenities = conn.prepareStatement("select * from chargeableamenity c join chargeableamenityapartment ca on c.chargeableamenityid = ca.chargeableamenityid join apartment a on ca.apartmentid = a.apartmentid where a.apartmentid = ?");
                            findCAmenities.setInt(1, apt);
                            ResultSet chAmenities = findCAmenities.executeQuery();
                            valid = chAmenities.next();
                            while(valid){
                                if(chAmenities.getString("name").length() < 21){
                                    System.out.printf("%s\t\t\t%d\t%s\n",chAmenities.getString("name"), chAmenities.getInt("monthlycost"), chAmenities.getString("description"));

                                }else{
                                    System.out.printf("%s\t\t%d\t%s\n",chAmenities.getString("name"), chAmenities.getInt("monthlycost"), chAmenities.getString("description"));
                                }
                                valid = chAmenities.next();
                            }

                            System.out.println("\nWould you like to view another chargeable amentity related to this property?");


                        }else{
                            System.out.println("Please enter an apt_id from this property.");
                            valid = false;
                        }

                    }while(!valid);

                }else{
                    valid = false;
                    System.out.println("Please enter an existing property id.");
                }


            }while(!valid);

        }catch (SQLException e){
                e.printStackTrace();
        }

    }

    public static void handleCustomer(Connection conn, Scanner scanner, int personID){
        boolean valid = true;
        do{
            valid = false;
            System.out.println("\nOptions:");
            System.out.println("1 -- Schedule a Visit\n2 -- Sign a Lease\n3 -- Exit\n");
            int choice = 0;
            if(scanner.hasNextInt()){
                choice = scanner.nextInt();
                scanner.nextLine();
                switch(choice){
                    case 3: 
                        System.out.println("\nReturning to Property Manager Interface.");
                        return;
                    case 1:
                        scheduleVisit(conn,scanner,personID);
                    break;
                    case 2:
                        signLease(conn, scanner, personID);
                        return;
                    default:
                        System.out.println("\nPlease Enter 1-3.\n");
                }
            }else{
                valid = false;
                scanner.nextLine();
            }
        }while(!valid);
    }

    public static void scheduleVisit(Connection conn, Scanner scanner, int personID){
        boolean valid = true;
        int choice = 0;
        try{

            System.out.println("\nScheduled Visits: ");
            System.out.println("Address\t\t\tDate\n----------------------------------");

            PreparedStatement findVisits = conn.prepareStatement("select * from Visit v join apartment a on v.apartmentid = a.apartmentid join property p on p.propertyid = a.propertyid where v.personid = ? order by visitdate asc");
            findVisits.setInt(1, personID);
            ResultSet visits = findVisits.executeQuery();
            valid = visits.next();
            while(valid){
                System.out.printf("%d, %s\t%s\n", visits.getInt("Apartmentnumber"), visits.getString("streetaddress"), visits.getDate("visitdate"));
                valid = visits.next();
            }

            System.out.println("\nWhere would you like to schedule a visit?");
            System.out.println("\n\nENTERING EXPLORE MODE (exit when finished).\n");
            exploreProperties(conn, scanner);
            valid = false;
            do{
                System.out.print("\n\nWhat APT_ID to visit?\n\n(0 to exit, -1 to print list of all apartments):");
                choice = getInt(scanner);

                if(choice == 0){
                    return;
                }
                
                if(choice == -1){
                    System.out.println("\n\nAPT_ID\tNUMBER\tBEDROOMS\tBATHROOMS\tSIZE\tRENT\t\tADDRESS\n-----------------------------------------------------------------------------------------");
                    PreparedStatement findAllApartments = conn.prepareStatement("select * from Property natural join apartment");
                    ResultSet apartments = findAllApartments.executeQuery();
                    valid = apartments.next();
                    
                    while(valid){
                        if(apartments.getInt("monthlyrent") < 1000){
                            System.out.printf("%d\t%d\t%d\t\t%.1f\t\t%d\t$%d.00\t\t%s\n",apartments.getInt("apartmentid"),apartments.getInt("apartmentnumber"), apartments.getInt("bedrooms"), apartments.getFloat("bathrooms"), apartments.getInt("apartmentsize"),apartments.getInt("monthlyrent"), apartments.getString("streetaddress"));

                        }else{
                            System.out.printf("%d\t%d\t%d\t\t%.1f\t\t%d\t$%d.00\t%s\n",apartments.getInt("apartmentid"),apartments.getInt("apartmentnumber"), apartments.getInt("bedrooms"), apartments.getFloat("bathrooms"), apartments.getInt("apartmentsize"),apartments.getInt("monthlyrent"), apartments.getString("streetaddress"));
                        }
                        valid = apartments.next();
                    }
                    continue;
                }

                //else check if it is a valid apartment
                PreparedStatement checkApartment = conn.prepareStatement("select * from Property natural join apartment where apartmentid = ?");
                checkApartment.setInt(1, choice);
                ResultSet apartment = checkApartment.executeQuery();
                if(apartment.next()){
                    //exists

                    System.out.println("\nYou have selected: ");
                    System.out.printf("APT %d, %s\n\n",apartment.getInt("apartmentnumber"), apartment.getString("streetaddress"));

                    System.out.print("When would you like to schedule your visit?\n(yyyy-MM-dd): ");
                    Date visit = getFutureDate(scanner);

                    PreparedStatement schedule = conn.prepareStatement("INSERT INTO Visit (apartmentid, personid, visitdate) VALUES (?, ?, ?)");
                    schedule.setInt(1, choice);
                    schedule.setInt(2, personID);
                    schedule.setDate(3, visit);
                    schedule.executeQuery();

                    System.out.println("Visit Successfully Scheduled!");
                    
                    return;
                }else{
                    valid = false;
                    System.out.println("\nPlease enter a valid APT_ID.\n");
                }

            }while(!valid);

        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    public static void exploreAvaiableProperties(Connection conn, Scanner scanner, int personID){
        boolean valid = true;
        int choice = 0;
        ArrayList<Integer> ca = new ArrayList<>();
        try{

            do{
                System.out.println("List of Properties:\n");
                System.out.println("ID\tADDRESS\t\t\t\t\tTOTAL UNITS\n-----------------------------------------------------------");
                PreparedStatement getProperties = conn.prepareStatement("select * from property");
                ResultSet properties = getProperties.executeQuery();
                valid = properties.next();
                while(valid){
                    System.out.printf("%d\t%s, %s, %s, %s\t\t%d\n", properties.getInt("propertyid"), properties.getString("streetaddress"), properties.getString("town"), properties.getString("state"), properties.getString("zipcode"), properties.getInt("totalunits"));
                    valid = properties.next();
                }

                System.out.println("\nWhich property would you like to learn more about (0 to exit): ");

                choice = getInt(scanner);

                if(choice == 0){
                    return;
                }

                PreparedStatement isProperty = conn.prepareStatement("select * from Property where propertyid = ?");
                isProperty.setInt(1, choice);
                ResultSet property = isProperty.executeQuery();
                if(property.next()){
                    
                    System.out.printf("You have selected:\n%s, %s, %s, %s\n\n", property.getString("streetaddress"), property.getString("town"), property.getString("state"), property.getString("zipcode"));

                    System.out.println("The following common amenities are avaliable to the selected property:\n");
                    System.out.println("NAME\t\t\tDESCRIPTION\n--------------------------------------");
                    PreparedStatement findCommonAmenities = conn.prepareStatement("select * from commonAmenity c join commonamenityproperty cp on c.commonamenityid = cp.commonamenityid join property p on cp.propertyid = p.propertyid where p.propertyid = ?");
                    findCommonAmenities.setInt(1, choice);
                    ResultSet commonAmenities = findCommonAmenities.executeQuery();
                    valid = commonAmenities.next();
                    while(valid){
                        if(commonAmenities.getString("name").length() < 15){
                            System.out.printf("%s\t\t%s\n",commonAmenities.getString("name"), commonAmenities.getString("description"));

                        }else{
                            System.out.printf("%s\t%s\n",commonAmenities.getString("name"), commonAmenities.getString("description"));

                        }
                        valid = commonAmenities.next();
                    }

                    System.out.println("\nThe following apartments are avaliable to the selected property:\n");

                    System.out.println("AVALIABILITY\t\tAPT_ID\tNUMBER\tBEDROOMS\tBATHROOMS\tSIZE\tRENT\n----------------------------------------------------------------------------------------------");
                    
                    PreparedStatement findApartments = conn.prepareStatement("select * from Property natural join apartment a full outer join lease l on a.apartmentid = l.apartmentid where propertyid = ? order by a.apartmentid, enddate desc");
                    findApartments.setInt(1, choice);
                    ResultSet apartments = findApartments.executeQuery();
                    valid = apartments.next();
                    while(valid){
                        if(apartments.getDate("enddate") == null){
                            System.out.printf("%s\t\t%d\t%d\t%d\t\t%.1f\t\t%d\t$%d.00\n", "Avaliable Now", apartments.getInt("apartmentid"),apartments.getInt("apartmentnumber"), apartments.getInt("bedrooms"), apartments.getFloat("bathrooms"), apartments.getInt("apartmentsize"),apartments.getInt("monthlyrent"));
                            valid = apartments.next();
                        }else{
                            System.out.printf("%s\t%d\t%d\t%d\t\t%.1f\t\t%d\t$%d.00\n", "Avaliable " + apartments.getDate("enddate"), apartments.getInt("apartmentid"),apartments.getInt("apartmentnumber"), apartments.getInt("bedrooms"), apartments.getFloat("bathrooms"), apartments.getInt("apartmentsize"),apartments.getInt("monthlyrent"));
                            int currentID = apartments.getInt("apartmentid");
                            valid = apartments.next();
                            while(valid && currentID == apartments.getInt("apartmentid")){
                                valid = apartments.next();
                            }
                        }
                        
                    }

                    System.out.println("\nWould you like to sign one of these apartments?");

                    
                    do{
                        ca.clear();
                        System.out.print("\nEnter the APT_ID to sign (0 to go back): ");

                        int apt = getInt(scanner);

                        if(apt == 0){
                            break;
                        }

                        PreparedStatement checkApartment = conn.prepareStatement("select * from Property natural join apartment where propertyid = ? and apartmentid = ?");
                        checkApartment.setInt(1, choice);
                        checkApartment.setInt(2, apt);
                        ResultSet check = checkApartment.executeQuery();
                        if(check.next()){
                            System.out.println("\nThe following chargeable amenities are avaliable to the selected apartment:\n");
                            System.out.println("ID\tNAME\t\t\tMONTHLYPRICE\t\tDESCRIPTION\n------------------------------------------------------------------");
                            PreparedStatement findCAmenities = conn.prepareStatement("select * from chargeableamenity c join chargeableamenityapartment ca on c.chargeableamenityid = ca.chargeableamenityid join apartment a on ca.apartmentid = a.apartmentid where a.apartmentid = ?");
                            findCAmenities.setInt(1, apt);
                            ResultSet chAmenities = findCAmenities.executeQuery();
                            valid = chAmenities.next();
                            while(valid){
                                ca.add(chAmenities.getInt("chargeableamenityid"));
                                if(chAmenities.getString("name").length() < 21){
                                    System.out.printf("%d\t%s\t\t\t%d\t%s\n",chAmenities.getInt("chargeableamenityid"),chAmenities.getString("name"), chAmenities.getInt("monthlycost"), chAmenities.getString("description"));

                                }else{
                                    System.out.printf("%d\t%s\t\t%d\t%s\n",chAmenities.getInt("chargeableamenityid"),chAmenities.getString("name"), chAmenities.getInt("monthlycost"), chAmenities.getString("description"));
                                }
                                valid = chAmenities.next();
                            }


                            ArrayList<Integer> chosenAmenities = getSelectedAmenities(ca);

                            System.out.println(chosenAmenities);

                            System.out.println("\n\nYour Selected Apartment: \n");
                            System.out.println("AVALIABILITY\t\tAPT_ID\tNUMBER\tBEDROOMS\tBATHROOMS\tSIZE\tRENT (per month)\n----------------------------------------------------------------------------------------------");

                            PreparedStatement findApt = conn.prepareStatement("select * from Property natural join apartment a full outer join lease l on a.apartmentid = l.apartmentid where a.apartmentid = ? order by a.apartmentid, enddate desc");
                            findApt.setInt(1, apt);
                            ResultSet leaseApartment = findApt.executeQuery();
                            if(leaseApartment.next()){
                                
                                PreparedStatement findTenantAndLeaseId = conn.prepareStatement("select * from tenant order by tenantid desc");
                                ResultSet tenantandleaseid = findTenantAndLeaseId.executeQuery();
                                tenantandleaseid.next();
                                int ids = tenantandleaseid.getInt("tenantid");
                                ids++;
                                
                                
                                if(leaseApartment.getDate("enddate") == null){
                                    System.out.printf("%s\t\t%d\t%d\t%d\t\t%.1f\t\t%d\t$%d.00\n", "Avaliable Now", leaseApartment.getInt("apartmentid"),leaseApartment.getInt("apartmentnumber"), leaseApartment.getInt("bedrooms"), leaseApartment.getFloat("bathrooms"), leaseApartment.getInt("apartmentsize"),leaseApartment.getInt("monthlyrent"));

                                    //avaliable now

                                    System.out.println("\nWith Chargeable Amenities: \n");
                                    System.out.println("ID\tNAME\t\t\t\tMONTHLYPRICE\tDESCRIPTION\n-----------------------------------------------------------------------------");

                                    for(int i : chosenAmenities){
                                        PreparedStatement findChaAmenitiies = conn.prepareStatement("select * from chargeableamenity where chargeableamenityid = ?");
                                        findChaAmenitiies.setInt(1, i);
                                        ResultSet chaAmenity = findChaAmenitiies.executeQuery();
                                        chaAmenity.next();

                                        if(chaAmenity.getString("name").length() < 21){
                                            System.out.printf("%d\t%s\t\t\t%d\t%s\n",chaAmenity.getInt("chargeableamenityid"),chaAmenity.getString("name"), chaAmenity.getInt("monthlycost"), chaAmenity.getString("description"));

                                        }else{
                                            System.out.printf("%d\t%s\t\t%d\t%s\n",chaAmenity.getInt("chargeableamenityid"),chaAmenity.getString("name"), chaAmenity.getInt("monthlycost"), chaAmenity.getString("description"));
                                        }
                                    }

                                    System.out.printf("\n\nYou will be asked to pay the security deposit of $%d.00 after the lease is created.\n", leaseApartment.getInt("securitydeposit"));

                                    System.out.println("\nDo you wish to continue?\nNO -- any int\nYes -- 1\n");
                                    int contin = getInt(scanner);
                                    if(contin == 1){
                                        LocalDate current = LocalDate.now();
                                        Date now = Date.valueOf(current);
                                        Date startDate = calculateFirstDayOfNextMonth(leaseApartment);
                                        Date endDate = calculateDate11MonthsAheadOnLastDay(startDate);
                                        int rent = leaseApartment.getInt("monthlyrent");

                                        //add to tenant table

                                        PreparedStatement addTenant = conn.prepareStatement("insert into tenant (tenantid, personid, leaseid) values (?, ?, ?)");
                                        addTenant.setInt(1, ids);
                                        addTenant.setInt(2, personID);
                                        addTenant.setInt(3, ids);
                                        addTenant.executeQuery();

                                        System.out.println("Created Tenant Successfully!\n");

                                        //add to lease table
                                        PreparedStatement addLease = conn.prepareStatement("INSERT INTO Lease (LEASEID, APARTMENTID, TENANTID, STARTDATE, ENDDATE, MONTHLYRENT, SECURITYDEPOSIT) VALUES (?, ?, ?, ?, ?, ?, 1500.00)");
                                        addLease.setInt(1, ids);
                                        addLease.setInt(2, apt);
                                        addLease.setInt(3, ids);
                                        addLease.setDate(4, startDate);
                                        addLease.setDate(5, endDate);
                                        addLease.setInt(6, rent);
                                        addLease.executeQuery();

                                        System.out.println("Created Lease Successfully!\n");

                                        //add amenities to lease
                                        for(int i : chosenAmenities){
                                            PreparedStatement addAmenity = conn.prepareStatement("insert into ChargeableAmenityLease (LEASEID, ChargeableAmenityID) VALUES (?,?)");
                                            addAmenity.setInt(1, ids);
                                            addAmenity.setInt(2, i);
                                            addAmenity.executeQuery();

                                            System.out.println("Added Amenity " + i + " to the Lease.");
                                        }

                                        System.out.println("Successfully added all Amenities to the Lease!\n");


                                        String method = "";
                                        String cvv = "";
                                        
                                        System.out.println("Security Deposit Payment:\n");
   
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
                                        pay.setInt(2, ids);
                                        pay.setInt(3, ids);
                                        pay.setDate(4, now);
                                        pay.setInt(5, 1500);
                                        pay.setString(6, method);
                                        pay.setInt(7, 1);

                                        pay.executeQuery();

                                        System.out.println("Payment Successful!\nThank you for creating your lease.\n");

                                        return;

                                        
                                    }else{
                                        System.out.println("Exiting Lease Creation\n");
                                        return;
                                    }

                                    
                                
                                }else{
                                    System.out.printf("%s\t%d\t%d\t%d\t\t%.1f\t\t%d\t$%d.00\n", "Avaliable " + leaseApartment.getDate("enddate"), leaseApartment.getInt("apartmentid"),leaseApartment.getInt("apartmentnumber"), leaseApartment.getInt("bedrooms"), leaseApartment.getFloat("bathrooms"), leaseApartment.getInt("apartmentsize"),leaseApartment.getInt("monthlyrent"));

                                    //avaliable after last lease is up

                                    System.out.println("\nWith Chargeable Amenities: \n");
                                    System.out.println("ID\tNAME\t\t\t\tMONTHLYPRICE\tDESCRIPTION\n-----------------------------------------------------------------------------");

                                    for(int i : chosenAmenities){
                                        PreparedStatement findChaAmenitiies = conn.prepareStatement("select * from chargeableamenity where chargeableamenityid = ?");
                                        findChaAmenitiies.setInt(1, i);
                                        ResultSet chaAmenity = findChaAmenitiies.executeQuery();
                                        chaAmenity.next();

                                        if(chaAmenity.getString("name").length() < 21){
                                            System.out.printf("%d\t%s\t\t\t%d\t%s\n",chaAmenity.getInt("chargeableamenityid"),chaAmenity.getString("name"), chaAmenity.getInt("monthlycost"), chaAmenity.getString("description"));

                                        }else{
                                            System.out.printf("%d\t%s\t\t%d\t%s\n",chaAmenity.getInt("chargeableamenityid"),chaAmenity.getString("name"), chaAmenity.getInt("monthlycost"), chaAmenity.getString("description"));
                                        }
                                    }

                                    System.out.printf("\n\nYou Will be asked to pay the security deposit of $%d.00 after the lease is created.\n", 1500);

                                    System.out.println("Do you wish to continue?\nAny integer -- NO\n1 -- Yes\n\n");
                                    int contin = getInt(scanner);
                                    if(contin == 1){
                                        LocalDate current = LocalDate.now();
                                        Date now = Date.valueOf(current);
                                        Date startDate = calculateFirstDayOfNextMonth(leaseApartment);
                                        Date endDate = calculateDate11MonthsAheadOnLastDay(startDate);
                                        int rent = leaseApartment.getInt("monthlyrent");

                                        //add to tenant table

                                        PreparedStatement addTenant = conn.prepareStatement("insert into tenant (tenantid, personid, leaseid) values (?, ?, ?)");
                                        addTenant.setInt(1, ids);
                                        addTenant.setInt(2, personID);
                                        addTenant.setInt(3, ids);
                                        addTenant.executeQuery();

                                        System.out.println("Created Tenant Successfully!\n");

                                        //add to lease table
                                        PreparedStatement addLease = conn.prepareStatement("INSERT INTO Lease (LEASEID, APARTMENTID, TENANTID, STARTDATE, ENDDATE, MONTHLYRENT, SECURITYDEPOSIT) VALUES (?, ?, ?, ?, ?, ?, 1500.00)");
                                        addLease.setInt(1, ids);
                                        addLease.setInt(2, apt);
                                        addLease.setInt(3, ids);
                                        addLease.setDate(4, startDate);
                                        addLease.setDate(5, endDate);
                                        addLease.setInt(6, rent);
                                        addLease.executeQuery();

                                        System.out.println("Created Lease Successfully!\n");

                                        

                                        //add amenities to lease
                                        for(int i : chosenAmenities){
                                            PreparedStatement addAmenity = conn.prepareStatement("insert into ChargeableAmenityLease (LEASEID, ChargeableAmenityID) VALUES (?,?)");
                                            addAmenity.setInt(1, ids);
                                            addAmenity.setInt(2, i);
                                            addAmenity.executeQuery();

                                            System.out.println("Added Amenity " + i + " to the Lease.");
                                        }

                                        System.out.println("Successfully added all Amenities to the Lease!\n");


                                        
                                        String method = "";
                                        String cvv = "";
                                        
                                        System.out.println("Security Deposit Payment:\n");

                                            
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
                                        pay.setInt(2, ids);
                                        pay.setInt(3, ids);
                                        pay.setDate(4, now);
                                        pay.setInt(5, 1500);
                                        pay.setString(6, method);
                                        pay.setInt(7, 1);

                                        pay.executeQuery();

                                        System.out.println("Payment Successful!\nThank you for creating your lease.\n");

                                        return;

                                    }else{
                                        System.out.println("Exiting Lease Creation\n");
                                        return;
                                    }

                                }

                            }else{
                                System.out.println("There was an error in preparing your lease.");
                                return;
                            }

                        }else{
                            System.out.println("Please enter an apt_id from this property.");
                            valid = false;
                        }

                    }while(!valid);

                }else{
                    valid = false;
                    System.out.println("Please enter an existing property id.");
                }


            }while(!valid);

        }catch (SQLException e){
                e.printStackTrace();
        }

    }

    public static Date calculateDate11MonthsAheadOnLastDay(Date inputDate) {
        LocalDate localDate = inputDate.toLocalDate();
        LocalDate date11MonthsAheadOnLastDay = localDate.plusMonths(11).withDayOfMonth(localDate.lengthOfMonth());
        return Date.valueOf(date11MonthsAheadOnLastDay);
    }

    public static Date calculateFirstDayOfNextMonth(ResultSet resultSet) throws SQLException {
        LocalDate endDate = resultSet.getDate("enddate") != null
                ? resultSet.getDate("enddate").toLocalDate()
                : LocalDate.now();

        LocalDate firstDayOfNextMonth = endDate.plusMonths(1).withDayOfMonth(1);

        return Date.valueOf(firstDayOfNextMonth);
    }

    public static ArrayList<Integer> getSelectedAmenities(ArrayList<Integer> ca) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Integer> chosenAmenities = new ArrayList<>();

        System.out.println("\nChoose amenities from the following list by entering their IDs. Enter 0 to finish:\n");

        while (true) {
            System.out.println("Available Amenities: " + ca);
            System.out.print("Enter amenity ID (0 to finish): ");
            if(scanner.hasNextInt()){
                int userInput = scanner.nextInt();
                if (userInput == 0) {
                    break;
                }
                if (ca.contains(userInput)) {
                    if (!chosenAmenities.contains(userInput)) {
                        chosenAmenities.add(userInput);
                        ca.remove(Integer.valueOf(userInput)); // Remove from the available list
                    } else {
                        System.out.println("You have already chosen this amenity. Please choose another one.");
                    }
                } else {
                    System.out.println("Invalid amenity ID. Please enter a valid ID from the list.");
                }
            }else{
                System.out.println("Please enter a valid integer.");
                scanner.nextLine();
            }
            
        }
        return chosenAmenities;
    }

    public static void signLease(Connection conn, Scanner scanner, int personID){
        System.out.println("\nEXPLORE AVALIABLE APARTMENTS:");
        exploreAvaiableProperties(conn, scanner, personID);
        return;
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
