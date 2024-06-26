CONTENTS:

To explaining what my directory is and where everything is located, I have all of my class
files in ./bin all my ojdbc11.jar in ./lib if you need to recompile. My ./nic225 directory has my 
App.java and my 3 other java files in ./nic225/other. There you will find all of the interface
logic and methods. I have an ./sql directory which I will attach my datageneration files and my DDL
but this is not exactly up to with my Database on oracle because I had some trouble with Pet and 
some constraints on my Person and Payments tables during the process of making this program. 
Next in my directory there is the Makefile, which I used to test, Manifest.txt, to make the jar file, 
and my nic225.jar, and this document. 

Run: 
java -jar nic225.jar
to run my jar file. 

If that does not work, you can remake the jar with
jar cfm nic225.jar Manifest.txt -C bin . -C lib .
which should recreate my jar file

If none of this is working you can use
make all
to use the makefile to compile and run my code.


SOURCES:

I coded almost all of this myself but I did use code from 
https://howtodoinjava.com/java/regex/java-regex-validate-email-address/
and 
https://howtodoinjava.com/java-regular-expression-tutorials/
in my Tenant.java getValidPhoneNumber and getValidEmail methods
to help me with regualar expressions and using regex.


USING MY APP: 

I am writing this Readme after completing the project, granted, I don't find any more errors.

I have decided to implement the Tenant, Property Manager, and Financial Manager interfaces
for this project. You asked us to create a process for you in the Readme, and I will briefly 
explain what I would do to test my code.

I would say start with the Financial Manager Interface because it is the simplest. 
This will show you the current leases and the total revenue in the company, which 
you can refer back to after you make payments or add leases in order to see this data change.
The current lease display does show the total rent including the added on amenties. 
Both options are fairly straightfoward and there is not much interacting.

Next move to the Property Manager Interface.
First, just take a second to explore the properties using the 3rd menu option (type 3).
This will start out by displaying a list of all the properties we own. You can learn more
about each property, which typing in their ID, will display some of the common amenities
avaliable to that property along with the specific apartments and related detailes within 
that property. Next you can enter in an apartment id, our press 0 to go back, which will
bring up a list of avaliable chargeable amentities for that specific apartment.

Once you have played with the explore function. You can go back to the Property Manager
interface and either handle a new customer or handle an existing customer. 

Handling a new customer will ask you to input the required information for new customers. 
Then you will be directed to either schedule a visit to an apartment or sign a lease with
apartment. 

Handling an existing customer will ask you to choose from a list of existing customers. If 
that person is already a tenant, the program will tell you to use the tenant menu and return.
If not, you can schedule a visit or sign a lease with that person. I have marked each of the 
tenants with an *, so you can easily see who are tenants and who are prospective tenants.

If you press schedule visit, you will see a list of you already scheduled visits, and then 
the explore properties feature will come up again so you can navigate and see which apartment
you would like to visit. You have to exit out of the explore mode feature in order to select which
apartment you would like to visit. When you exit out, if you remember which apartment id you want
to visit you can enter it, or you can enter -1 to pull up a list of all of the apartments. Then, When
you correctly select an apartment you will have to enter a future date in order to schedule in the
correct format. Then, that will create a row in the visit table with your scheduled visit, which
you can see if you go back to add another visit.

Next you can sign a lease with this person, or go back and choose another person you
want to sign a lease with. This will bring you to a modified version of the explore
feature which you will have to select which apartment you want to sign by going through the
properties. This feed will also show you the soonest date the apartment is avaliable.
When you select select an apartment you would like to create a lease with, it will
show you to options for chargeable amenities on that apartment. You can add up to all of 
the avaliable amentities or none and press 0 whenever you are done. This will bring up
a final lease model, which wil show you the rent and the selected amenities you chose. 
Then you can choose to exit, by entering anything integer other than 1, or continuing by
entering 1. If you continue you will be prompted for a credit card and cvv, in order to
pay the security deposit. You will have to enter a 16 digit credit card in xxxx-xxxx-xxxx-xxxx
format and a 3 or 4 digit cvv. I am only storing the credit/debit card number in the database.
If you enter in the correct format, the payment should go through and the lease will be created. 
This program will automatically bring you back to the property manager interface, which you will
see now, the person you created a lease with will have an *. 

Next, you can move to the tenant interface, which you should now see your created tenant.
Clicking on this interface will output all of the tenants in the system. You will have to 
input their id in order to bring up the tenant menu. When you choose which tenant you would like
to be, there a a numerous amount of things you can do. You can try doing everything on the list
with each tenant but you will not be able to make payments with the newly created tenants,
because I implemented my interface in a way so you can only make a payment on the month that it
is due. In order to test the make payment functionalality, please use tenants 1-3 that can still
make a payment on their lease.

Thats pretty much it with the functionality in my app. Thank you.