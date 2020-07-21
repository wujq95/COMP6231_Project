package client;

import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;


import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.URL;
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
        //GameInterface obj;

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
                        URL asiaURL = new URL("http://localhost:8080/asia?wsdl");
                        QName aisaQName = new QName("http://impl.service.web.com/", "AsGameClassService");
                        Service aisaService = Service.create(asiaURL, aisaQName);
                        //obj = aisaService.getPort(GameInterface.class);
                    }else if(ipAddress.startsWith("93")){
                        URL europeURL = new URL("http://localhost:8081/europe?wsdl");
                        QName europeQName = new QName("http://impl.service.web.com/", "EuGameClassService");
                        Service europeService = Service.create(europeURL, europeQName);
                        //obj = europeService.getPort(GameInterface.class);
                    }else{
                        URL northAmericaURL = new URL("http://localhost:8082/northAmerica?wsdl");
                        QName northAmericaQName = new QName("http://impl.service.web.com/", "NaGameClassService");
                        Service northAmericaService = Service.create(northAmericaURL, northAmericaQName);
                        //obj = northAmericaService.getPort(GameInterface.class);
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
                    //String reply = obj.createPlayerAccount(firstName.trim(), lastName.trim(), age.trim(), userName.trim(), password.trim(), ipAddress);
                    //System.out.println(reply);
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
                    if(ipAddress.startsWith("182")){
                    	URL asiaURL = new URL("http://localhost:8080/asia?wsdl");
                        QName aisaQName = new QName("http://impl.service.web.com/", "AsGameClassService");
                        Service aisaService = Service.create(asiaURL, aisaQName);
                        //obj = aisaService.getPort(GameInterface.class);
                    }else if(ipAddress.startsWith("93")){
                    	URL europeURL = new URL("http://localhost:8081/europe?wsdl");
                        QName europeQName = new QName("http://impl.service.web.com/", "EuGameClassService");
                        Service europeService = Service.create(europeURL, europeQName);
                        //obj = europeService.getPort(GameInterface.class);
                    }else{
                    	URL northAmericaURL = new URL("http://localhost:8082/northAmerica?wsdl");
                        QName northAmericaQName = new QName("http://impl.service.web.com/", "NaGameClassService");
                        Service northAmericaService = Service.create(northAmericaURL, northAmericaQName);
                        //obj = northAmericaService.getPort(GameInterface.class);
                    }
                    System.out.print("Please enter the user name: ");
                    String userName = scanner.nextLine();
                    System.out.print("Please enter the password: ");
                    String password = scanner.nextLine();
                    //String reply = obj.playerSignIn(userName.trim(), password.trim(), ipAddress);
                    //System.out.println(reply);
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
                    	URL asiaURL = new URL("http://localhost:8080/asia?wsdl");
                        QName aisaQName = new QName("http://impl.service.web.com/", "AsGameClassService");
                        Service aisaService = Service.create(asiaURL, aisaQName);
                        //obj = aisaService.getPort(GameInterface.class);
                    }else if(ipAddress.startsWith("93")){
                    	URL europeURL = new URL("http://localhost:8081/europe?wsdl");
                        QName europeQName = new QName("http://impl.service.web.com/", "EuGameClassService");
                        Service europeService = Service.create(europeURL, europeQName);
                        //obj = europeService.getPort(GameInterface.class);
                    }else{
                    	URL northAmericaURL = new URL("http://localhost:8082/northAmerica?wsdl");
                        QName northAmericaQName = new QName("http://impl.service.web.com/", "NaGameClassService");
                        Service northAmericaService = Service.create(northAmericaURL, northAmericaQName);
                        //obj = northAmericaService.getPort(GameInterface.class);
                    }
                    System.out.print("Please enter the user name: ");
                    String userName = scanner.nextLine();
                    //String reply = obj.playerSignOut(userName.trim(), ipAddress);
                    //System.out.println(reply);
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
                    	URL asiaURL = new URL("http://localhost:8080/asia?wsdl");
                        QName aisaQName = new QName("http://impl.service.web.com/", "AsGameClassService");
                        Service aisaService = Service.create(asiaURL, aisaQName);
                        //obj = aisaService.getPort(GameInterface.class);
                    }else if(ipAddress.startsWith("93")){
                    	URL europeURL = new URL("http://localhost:8081/europe?wsdl");
                        QName europeQName = new QName("http://impl.service.web.com/", "EuGameClassService");
                        Service europeService = Service.create(europeURL, europeQName);
                        //obj = europeService.getPort(GameInterface.class);
                    }else{
                    	URL northAmericaURL = new URL("http://localhost:8082/northAmerica?wsdl");
                        QName northAmericaQName = new QName("http://impl.service.web.com/", "NaGameClassService");
                        Service northAmericaService = Service.create(northAmericaURL, northAmericaQName);
                        //obj = northAmericaService.getPort(GameInterface.class);
                    }
                    System.out.print("Please enter the user name: ");
                    String userName = scanner.nextLine();
                    System.out.print("Please enter the password: ");
                    String password = scanner.nextLine();
                    System.out.print("Please enter your new ip address: ");
                    String newIPAddress = scanner.nextLine();
                    //String reply = obj.transferAccount(userName.trim(),password.trim(),ipAddress,newIPAddress.trim());
                    //System.out.println(reply);
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
