package replica3.na;

import config.PortConfig;
import org.omg.CORBA.ORB;
import replica3.DPSSModule.DPSSPOA;
import replica3.logger.Logger;
import replica3.util.Util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


public class NaGameClass extends DPSSPOA {

    Logger logger = new Logger();
    Util util = new Util();
    static String serverName = "na";

    public NaGameClass() {
        super();
    }

    private ORB orb;
    public void setORB(ORB orb_val) {
        orb = orb_val;
    }

    /**
     * create player account method
     * @param firstName
     * @param lastName
     * @param age
     * @param userName
     * @param password
     * @param ipAddress
     * @return
     * @throws IOException
     */
    @Override
    public String createPlayerAccount(String firstName, String lastName, String age, String userName, String password, String ipAddress){
        String regex = "\\-?[0-9]+";
        Pattern pattern = Pattern.compile(regex);
        if(userName.trim().length()<6||userName.trim().length()>15){
            try {
                logger.serverLog(serverName,"Fail to create a player account: The length of the user name does not meet requirements");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "the length of user name must be between 6 and 15";
        }
        if(password.trim().length()<6){
            try {
                logger.serverLog(serverName,"Fail to create a player account: The length of the password does not meet requirements");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "the length of password can not be less than 6";
        }
        if(util.checkUserName(userName.trim(),ipAddress)){
            try {
                logger.serverLog(serverName,"Fail to create a player account: User name has been existed");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "user name has been existed";
        }
        if(firstName.trim().equals("")){
            try {
                logger.serverLog(serverName,"Fail to create a player account: First name is empty");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "first name can not be empty";
        }
        if(lastName.trim().equals("")){
            try {
                logger.serverLog(serverName,"Fail to create a player account: Last name is empty");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "last name can not be empty";
        }
        if(!pattern.matcher(age.trim()).matches()){
            try {
                logger.serverLog(serverName,"Fail to create a player account: Age is not an integer");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "age should be a integer";
        }
        if(Integer.parseInt(age.trim())<=0){
            try {
                logger.serverLog(serverName,"Fail to create a player account: Age is not positive");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "age should be positive";
        }
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter("src/replica3/na/naAccounts.txt",true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter printWriter = new PrintWriter(fileWriter);
        Character c = userName.charAt(0);
        String str = userName+"|"+firstName+"|"+lastName+"|"+age+"|"+password+"|"+ipAddress;
        if(NaGameServer.northAmericaMap.containsKey(c)){
            List<String> list = NaGameServer.northAmericaMap.get(c);
            list.add(str);
            NaGameServer.northAmericaMap.put(c,list);
        }else {
            List<String> list = new ArrayList<>();
            list.add(str);
            NaGameServer.northAmericaMap.put(c,list);
        }
        try {
            if(!util.checkNameExist("src/replica3/na/naAccounts.txt",str.split("\\|")[0])){
                printWriter.println(str);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        printWriter.flush();
        printWriter.close();
        try {
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File file = new File("src/replica3/logger/logs/naPlayerLogs/"+userName+".txt");
        if(!file.exists()){
            try {
                file.createNewFile();
                logger.playerLog(serverName,userName,"The account has been created");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            logger.serverLog(serverName,"Successfully create an account: "+userName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "user has been created";
    }

    /**
     * player sign in method
     * @param userName
     * @param password
     * @param ipAddress
     * @return response message
     * @throws Exception
     */
    @Override
    public String playerSignIn(String userName, String password, String ipAddress){
        if(userName.trim().equals("")){
            try {
                logger.serverLog(serverName,"Fail to login: User name is empty");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "user name can not be empty!";
        }
        if (password.trim().equals("")){
            try {
                logger.serverLog(serverName,"Fail to login: Password is empty");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "password can not be empty";
        }
        if(!util.checkUserName(userName.trim(),ipAddress)){
            try {
                logger.serverLog(serverName,"Fail to login: User name does not exist");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "user name does not exist";
        }
        if(!util.checkPassword(userName.trim(),password,ipAddress)){
            try {
                logger.serverLog(serverName,"Fail to login: Password is wrong");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "password is wrong";
        }
        if(NaGameServer.northAmericaOnline.contains(userName.trim())){
            try {
                logger.serverLog(serverName,"Fail to login: User is currently signed-in");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "user is currently signed-in";
        }
        try {
            logger.serverLog(serverName,"Successfully login: "+userName.trim());
            logger.playerLog(serverName,userName.trim(),"Login to game");
        } catch (IOException e) {
            e.printStackTrace();
        }
        NaGameServer.northAmericaOnline.add(userName.trim());
        return "user login successful";
    }

    /**
     * player sign out method
     * @param userName
     * @param ipAddress
     * @return response message
     * @throws Exception
     */
    @Override
    public String playerSignOut(String userName, String ipAddress){
        if(userName.trim().equals("")){
            try {
                logger.serverLog(serverName,"Fail to logout: User name is empty");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "user name can not be empty!";
        }
        if(!util.checkUserName(userName.trim(),ipAddress)){
            try {
                logger.serverLog(serverName,"Fail to logout: User name does not exist");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "user name does not exist";
        }
        if(!NaGameServer.northAmericaOnline.contains(userName.trim())){
            try {
                logger.serverLog(serverName,"Fail to logout: User is not currently signed-in");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "user is not currently signed-in";
        }
        try {
            logger.serverLog(serverName,"Successfully logout: "+userName.trim());
            logger.playerLog(serverName,userName.trim(),"Logout the game");
        } catch (IOException e) {
            e.printStackTrace();
        }
        NaGameServer.northAmericaOnline.remove(userName.trim());
        return "user logout successful";
    }

    /**
     * administrators get status of players
     * @param adminUsername
     * @param adminPassword
     * @param ipAddress
     * @return response message
     * @throws Exception
     */
    @Override
    public String getPlayerStatus(String adminUsername, String adminPassword, String ipAddress)  {
        if (!adminUsername.equals("Admin")) {
            try {
                logger.serverLog(serverName,"Fail to login: Admin user name is wrong");
                logger.adminLog("Fail to login: Admin user name is wrong");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "admin name is wrong";
        }
        if (!adminPassword.equals("Admin")) {
            try {
                logger.serverLog(serverName,"Fail to login: Admin password is wrong");
                logger.adminLog("Fail to login: Admin password name is wrong");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "admin password is wrong";
        }
        int sum = 0;
        for (Map.Entry<Character, List<String>> entry : NaGameServer.northAmericaMap.entrySet()) {
            sum += entry.getValue().size();
        }
        int online = NaGameServer.northAmericaOnline.size();
        DatagramSocket aSocket = null;
        DatagramSocket aSocket1 = null;
        String euMsg = null;
        String asMsg = null;
        try {
            aSocket = new DatagramSocket();
            euMsg = util.getRemote(PortConfig.replicaEU3,aSocket,"");
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IO: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception: " + e.getMessage());
        } finally {
            if (aSocket != null)
                aSocket.close();
        }
        try {
            aSocket1 = new DatagramSocket();
            asMsg = util.getRemote(PortConfig.replicaAS3,aSocket1,"");
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IO: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception: " + e.getMessage());
        } finally {
            if (aSocket1 != null)
                aSocket1.close();
        }
        String res = "NA: "+online+" online, "+(sum-online)+"offline, "+
                "EU: "+ euMsg.split(";")[0]+" online, "+euMsg.split(";")[1]+"offline, "+
                "AS: "+asMsg.split(";")[0]+" online, "+asMsg.split(";")[1]+"offline ";
        try {
            logger.serverLog(serverName,"Successfully check players status: "+res);
            logger.adminLog("Successfully check players status: "+res);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * player transfer to another location
     * @param userName
     * @param password
     * @param oldIPAddress
     * @param newIPAddress
     * @return
     */
    @Override
    public String transferAccount(String userName, String password, String oldIPAddress, String newIPAddress) {
        String res;
        boolean removeResult;
        boolean addResult = false;
        if(!util.checkAddress(newIPAddress)){
            try {
                logger.serverLog(serverName,"Fail to transfer the account: New ip address is not right");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "new ip address is wrong";
        }
        if(newIPAddress.startsWith("132.")){
            try {
                logger.serverLog(serverName,"Fail to transfer the account: Old and new ip address are in the same location");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "old and new ip address can not be in the same location";
        }
        if(!util.checkUserName(userName.trim(),oldIPAddress)){
            try {
                logger.serverLog(serverName,"Fail to transfer the account: User name does not exist");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "user name does not exist";
        }
        if(!util.checkPassword(userName.trim(),password,oldIPAddress)){
            try {
                logger.serverLog(serverName,"Fail to transfer the account: Password is wrong");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "password is wrong";
        }
        String str = util.findPlayer(userName.trim(),oldIPAddress);
        removeResult = util.removePlayer(userName,oldIPAddress);
        if (removeResult) {
            addResult = addPlayer(str, newIPAddress);
        }
        if (removeResult && addResult) {
            if (NaGameServer.northAmericaOnline.contains(userName.trim())) {
                NaGameServer.northAmericaOnline.remove(userName.trim());
                try {
                    logger.playerLog(serverName, userName.trim().trim(), "User has logout because the account has been transfered");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            res = "successfully transfer the player";
        } else if (removeResult && (!addResult)) {
            List list = NaGameServer.northAmericaMap.get(str.charAt(0));
            list.add(str);
            NaGameServer.northAmericaMap.put(str.charAt(0),list);
            res = "Fail to transfer the account: Unable to add the player to new location, and the reason may be that an account with the same user name already exists in the new server";
        } else {
            res = "Fail to transfer the account: Unable to add the player to new location and remove the player from old location";
        }
        try {
            logger.serverLog(serverName,res);
            logger.playerLog(serverName,userName,res);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * admin suspend the player account
     * @param adminUsername
     * @param adminPassword
     * @param ipAddress
     * @param usernameToSuspend
     * @return
     */
    @Override
    public String suspendAccount(String adminUsername, String adminPassword, String ipAddress, String usernameToSuspend) {
        if (!adminUsername.equals("Admin")) {
            try {
                logger.serverLog(serverName,"Fail to suspend the account: Admin user name is wrong");
                logger.adminLog("Fail to suspend the account: Admin user name is wrong");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "admin name is wrong";
        }
        if (!adminPassword.equals("Admin")) {
            try {
                logger.serverLog(serverName,"Fail to suspend the account: Admin password is wrong");
                logger.adminLog("Fail to suspend the account: Admin password name is wrong");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "admin password is wrong";
        }
        if(!util.checkUserName(usernameToSuspend.trim(),ipAddress)){
            try {
                logger.serverLog(serverName,"Fail to suspend the account: User name does not exist");
                logger.adminLog("Fail to suspend the account: User name does not exist");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "user name does not exist";
        }
        List<String> list = NaGameServer.northAmericaMap.get(usernameToSuspend.charAt(0));
        list = util.removeItemFromList(list,usernameToSuspend);
        NaGameServer.northAmericaMap.put(usernameToSuspend.charAt(0),list);
        try {
            logger.serverLog(serverName,"Successfully suspend the user account: "+usernameToSuspend);
            logger.adminLog("Successfully suspend the user account: "+usernameToSuspend);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(NaGameServer.northAmericaOnline.contains(usernameToSuspend.trim())){
            NaGameServer.northAmericaOnline.remove(usernameToSuspend.trim());
            try {
                logger.playerLog(serverName,usernameToSuspend.trim(),"User has logout because the account has been suspended");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "successfully suspend the player account";
    }

    /**
     * shut down method
     */
    @Override
    public void shutdown() {

        orb.shutdown(false);
    }

    /**
     * add player
     * @param str
     * @param newIPAddress
     * @return
     */
    public boolean addPlayer(String str, String newIPAddress){
        boolean flag = false;
        DatagramSocket aSocket = null;
        DatagramSocket aSocket1 = null;
        String euMsg = null;
        String asMsg = null;
        if(newIPAddress.startsWith("182.")){
            try {
                aSocket = new DatagramSocket();
                asMsg = util.getRemote(PortConfig.replicaAS3,aSocket,str);
            } catch (SocketException e) {
                System.out.println("Socket: " + e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("IO: " + e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Exception: " + e.getMessage());
            } finally {
                if (aSocket != null)
                    aSocket.close();
                if(asMsg!=null&&asMsg.startsWith("success")){
                    flag = true;
                }
            }
            return flag;
        }else{
            try {
                aSocket1 = new DatagramSocket();
                euMsg = util.getRemote(PortConfig.replicaEU3,aSocket1,str);
            } catch (SocketException e) {
                System.out.println("Socket: " + e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("IO: " + e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Exception: " + e.getMessage());
            } finally {
                if (aSocket1 != null)
                    aSocket1.close();
                if(euMsg!=null&&euMsg.startsWith("success")){
                    flag = true;
                }
            }
            return flag;
        }
    }



}
