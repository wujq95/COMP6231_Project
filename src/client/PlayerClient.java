package client;

import frontend.DPSSModule.DPSS;
import frontend.DPSSModule.DPSSHelper;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Pattern;

public class PlayerClient {

    /**
     * player client main method
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception{
        Scanner scanner = new Scanner(System.in);
        String ipAddress;
        DPSS obj;
        Properties props = new Properties();
        props.put("org.omg.CORBA.ORBInitialPort", "1050");
        props.put("org.omg.CORBA.ORBInitialHost", "localhost");
        // create and initialize the ORB
        ORB orb = ORB.init(args, props);
        // get the root naming context
        org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
        // Use NamingContextExt instead of NamingContext. This is part of the Interoperable naming Service.
        NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

        while (true) {
            System.out.println("Please select an action");
            System.out.println("1. Create Player Account");
            System.out.println("2. Player Sign In");
            System.out.println("3. Player Sign Out");
            System.out.println("4. Player Transfer Account");
            System.out.println("5. Close the client");
            System.out.print("choice: ");
            String scan = scanner.nextLine();
            if (!(scan.equals("1")||(scan.equals("2"))||(scan.equals("3"))||(scan.equals("4"))||(scan.equals("5")))) {
                System.out.println("Sorry, your command is wrong");
                System.out.println("Please try again");
                System.out.println();
                continue;
            }
            Integer input = Integer.parseInt(scan);
            if (input == 1) {
                System.out.print("Please enter the ipAddress: ");
                ipAddress = scanner.nextLine();
                if(checkAddress(ipAddress.trim())){
                    if(ipAddress.startsWith("182")){
                        obj = DPSSHelper.narrow(ncRef.resolve_str("AS"));
                    }else if(ipAddress.startsWith("93")){
                        obj = DPSSHelper.narrow(ncRef.resolve_str("EU"));
                    }else{
                        obj = DPSSHelper.narrow(ncRef.resolve_str("NA"));
                    }
                    System.out.print("Please enter the first name: ");
                    String firstName = scanner.nextLine();
                    System.out.print("Please enter the last name: ");
                    String lastName = scanner.nextLine();
                    System.out.print("Please enter the age: ");
                    String age = scanner.nextLine();
                    System.out.print("Please enter the user name: ");
                    String userName = scanner.nextLine();
                    System.out.print("Please enter the password: ");
                    String password = scanner.nextLine();
                    String reply = obj.createPlayerAccount(firstName.trim(), lastName.trim(), age.trim(), userName.trim(), password.trim(), ipAddress);
                    System.out.println(reply);
                    System.out.println();
                }else{
                    System.out.println("Sorry, your ip address is wrong");
                    System.out.println("Please try again");
                    System.out.println();
                    continue;
                }
            } else if (input == 2) {
                System.out.print("Please enter the ipAddress: ");
                ipAddress = scanner.nextLine();
                if(checkAddress(ipAddress.trim())){
                    obj = DPSSHelper.narrow(ncRef.resolve_str("FrontEnd"));
                    System.out.print("Please enter the user name: ");
                    String userName = scanner.nextLine();
                    System.out.print("Please enter the password: ");
                    String password = scanner.nextLine();
                    String reply = obj.playerSignIn(userName.trim(), password.trim(), ipAddress);
                    System.out.println(reply);
                    System.out.println();
                }else{
                    System.out.println("Sorry, your ip address is wrong");
                    System.out.println("Please try again");
                    System.out.println();
                    continue;
                }
            } else if (input == 3) {
                System.out.print("Please enter the ipAddress:");
                ipAddress = scanner.nextLine();
                if(checkAddress(ipAddress.trim())){
                    if(ipAddress.startsWith("182")){
                        obj = DPSSHelper.narrow(ncRef.resolve_str("AS"));
                    }else if(ipAddress.startsWith("93")){
                        obj = DPSSHelper.narrow(ncRef.resolve_str("EU"));
                    }else{
                        obj = DPSSHelper.narrow(ncRef.resolve_str("NA"));
                    }
                    System.out.print("Please enter the user name: ");
                    String userName = scanner.nextLine();
                    String reply = obj.playerSignOut(userName.trim(), ipAddress);
                    System.out.println(reply);
                    System.out.println();
                }else{
                    System.out.println("Sorry, your ip address is wrong");
                    System.out.println("Please try again");
                    System.out.println();
                    continue;
                }
            } else if (input == 4) {
                System.out.print("Please enter your old ip address: ");
                ipAddress = scanner.nextLine();
                if(checkAddress(ipAddress.trim())){
                    if(ipAddress.startsWith("182")){
                        obj = DPSSHelper.narrow(ncRef.resolve_str("AS"));
                    }else if(ipAddress.startsWith("93")){
                        obj = DPSSHelper.narrow(ncRef.resolve_str("EU"));
                    }else{
                        obj = DPSSHelper.narrow(ncRef.resolve_str("NA"));
                    }
                    System.out.print("Please enter the user name: ");
                    String userName = scanner.nextLine();
                    System.out.print("Please enter the password: ");
                    String password = scanner.nextLine();
                    System.out.print("Please enter your new ip address: ");
                    String newIPAddress = scanner.nextLine();
                    String reply = obj.transferAccount(userName.trim(),password.trim(),ipAddress,newIPAddress.trim());
                    System.out.println(reply);
                    System.out.println();
                }else{
                    System.out.println("Sorry, your old ip address is wrong");
                    System.out.println("Please try again");
                    System.out.println();
                    continue;
                }
            }else if (input == 5) {
                System.out.println("client has been closed");
                break;
            }
        }
    }

    /**
     * check if ip address is in the right pattern
     * @param ipAddress
     * @return
     */
    public static boolean checkAddress(String ipAddress){
        String regex="\\d{3}\\.\\d{3}\\.\\d{3}";
        Pattern pattern = Pattern.compile(regex);
        if(ipAddress.startsWith("182.")){
            if(pattern.matcher(ipAddress.substring(4)).matches()){
                return true;
            }
        }else if(ipAddress.startsWith("93.")){
            if(pattern.matcher(ipAddress.substring(3)).matches()){
                return true;
            }
        }else if(ipAddress.startsWith("132.")){
            if(pattern.matcher(ipAddress.substring(4)).matches()){
                return true;
            }
        }
        return false;
    }
}
