package client;


import frontend.DPSSModule.DPSS;
import frontend.DPSSModule.DPSSHelper;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Pattern;

public class AdministratorClient {

    /**
     * administrator client main method
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
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
        while (true){
            System.out.println("1. Check players status");
            System.out.println("2. Suspend the player account");
            System.out.println("3. Close the client");
            System.out.print("choice: ");

            String scan = scanner.nextLine();
            if (!(scan.equals("1")||scan.equals("2")||scan.equals("3"))){
                System.out.println("Sorry, your command is wrong");
                System.out.println("Please try again");
                System.out.println();
                continue;
            }
            Integer input = Integer.parseInt(scan);
            if (input == 1) {
                System.out.print("Please enter the ipAddress: ");
                ipAddress = scanner.nextLine();
                if(checkAddress(ipAddress)){
                    if(ipAddress.startsWith("182")){
                        obj = DPSSHelper.narrow(ncRef.resolve_str("AS"));
                    }else if(ipAddress.startsWith("93")){
                        obj = DPSSHelper.narrow(ncRef.resolve_str("EU"));
                    }else{
                        obj = DPSSHelper.narrow(ncRef.resolve_str("NA"));
                    }
                    System.out.print("Press enter your user name: ");
                    String userName = scanner.nextLine();
                    System.out.print("Please enter the password: ");
                    String password = scanner.nextLine();
                    String reply = obj.getPlayerStatus(userName,password,ipAddress);
                    System.out.println(reply);
                    System.out.println();
                }else{
                    System.out.println("Sorry, your ip address is wrong");
                    System.out.println("Please try again");
                    System.out.println();
                    continue;
                }
            }else if (input == 2) {
                System.out.print("Please enter the ipAddress: ");
                ipAddress = scanner.nextLine();
                if(checkAddress(ipAddress)){
                    if(ipAddress.startsWith("182")){
                        obj = DPSSHelper.narrow(ncRef.resolve_str("AS"));
                    }else if(ipAddress.startsWith("93")){
                        obj = DPSSHelper.narrow(ncRef.resolve_str("EU"));
                    }else{
                        obj = DPSSHelper.narrow(ncRef.resolve_str("NA"));
                    }
                    System.out.print("Press enter your user name: ");
                    String userName = scanner.nextLine();
                    System.out.print("Please enter the password: ");
                    String password = scanner.nextLine();
                    System.out.print("Please enter the user name that you want to suspend: ");
                    String userNameToSuspend = scanner.nextLine();
                    String reply = obj.suspendAccount(userName,password,ipAddress,userNameToSuspend);
                    System.out.println(reply);
                    System.out.println();
                }else{
                    System.out.println("Sorry, your ip address is wrong");
                    System.out.println("Please try again");
                    System.out.println();
                    continue;
                }
            }else if (input == 3) {
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
