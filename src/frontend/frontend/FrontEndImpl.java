package frontend.frontend;

import frontend.FrontEndModule.FrontEndPOA;
import frontend.config.FrontEndConfig;
import org.omg.CORBA.ORB;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;


public class FrontEndImpl extends FrontEndPOA {

    private ORB orb;

    public void setORB(ORB orb_val) {
        orb = orb_val;
    };

    /**
     * corba request: create player account
     * @param firstName
     * @param lastName
     * @param age
     * @param userName
     * @param password
     * @param ipAddress
     * @return
     */
    @Override
    public String createPlayerAccount(String firstName, String lastName, String age, String userName, String password, String ipAddress) {
        synchronized (this){
            System.out.println("Front end receives a client request: create player account");
            String message = "createPlayerAccount"+"|"+firstName+"|"+lastName+"|"+age+"|"+userName+"|"+password+"|"+ipAddress;
            String result = sendMsgToLeader(message);
            return result;
        }
    }

    /**
     * corba request: player sign in
     * @param userName
     * @param password
     * @param ipAddress
     * @return
     */
    @Override
    public String playerSignIn(String userName, String password, String ipAddress) {
        synchronized (this) {
            System.out.println("Front end receives a client request: player sign in");
            String message = "playerSignIn" + "|" + userName + "|" + password + "|" + ipAddress;
            String result = sendMsgToLeader(message);
            return result;
        }
    }

    /**
     * corba request: player sign out
     * @param userName
     * @param ipAddress
     * @return
     */
    @Override
    public String playerSignOut(String userName, String ipAddress) {
        synchronized (this){
            System.out.println("Front end receives a client request: player sign out");
            String message = "playerSignOut"+"|"+userName+"|"+ipAddress;
            String result = sendMsgToLeader(message);
            return result;
        }
    }

    /**
     * corba request: get players status
     * @param adminUsername
     * @param adminPassword
     * @param ipAddress
     * @return
     */
    @Override
    public String getPlayerStatus(String adminUsername, String adminPassword, String ipAddress) {
        synchronized (this){
            System.out.println("Front end receives a client request: get players status");
            String message = "getPlayerStatus"+"|"+adminUsername+"|"+adminPassword+"|"+ipAddress;
            String result = sendMsgToLeader(message);
            return result;
        }
    }

    /**
     * transfer players account
     * @param userName
     * @param password
     * @param oldIPAddress
     * @param newIPAddress
     * @return
     */
    @Override
    public String transferAccount(String userName, String password, String oldIPAddress, String newIPAddress) {
        synchronized (this){
            System.out.println("Front end receives a client request: transfer player account");
            String message = "transferAccount"+"|"+userName+"|"+password+"|"+oldIPAddress+"|"+newIPAddress;
            String result = sendMsgToLeader(message);
            return result;
        }
    }

    /**
     * suspend player account
     * @param adminUsername
     * @param adminPassword
     * @param ipAddress
     * @param usernameToSuspend
     * @return
     */
    @Override
    public String suspendAccount(String adminUsername, String adminPassword, String ipAddress, String usernameToSuspend) {
        synchronized (this){
            System.out.println("Front end receives a client request: suspend player account");
            String message = "suspendAccount"+"|"+adminUsername+"|"+adminPassword+"|"+ipAddress+"|"+usernameToSuspend;
            String result = sendMsgToLeader(message);
            return result;
        }
    }

    /**
     * shut down
     */
    @Override
    public void shutdown() {
        orb.shutdown(false);
    }

    /**
     * forward request to the leader
     * @param message
     * @return
     */
    public static String sendMsgToLeader(String message){

        DatagramSocket aSocket = null;
        String result = "Network failure!";
        try {
            aSocket = new DatagramSocket();
            byte[] sendData = message.getBytes();
            InetAddress host = InetAddress.getByName("localhost");
            Integer leaderUDPPort = getLeaderUDPPort();
            System.out.println("Front end sends the request to the leader: "+ message);
            DatagramPacket request = new DatagramPacket(sendData, message.length(), host,leaderUDPPort);
            aSocket.send(request);
            while(true){
                try {
                    byte[] buffer = new byte[1000];
                    DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                    aSocket.setSoTimeout(5000);
                    aSocket.receive(reply);
                    result = new String(reply.getData()).trim();
                    System.out.println("Front end receives the result from the leader: "+result);
                    if(result.startsWith("No reply")){
                        InetAddress hostResend = InetAddress.getByName("localhost");
                        DatagramPacket requestResend = new DatagramPacket(sendData, sendData.length, hostResend, getLeaderUDPPort());
                        aSocket.send(requestResend);
                    }else{
                        break;
                    }
                } catch (SocketTimeoutException e) {
                    InetAddress hostResend = InetAddress.getByName("localhost");
                    DatagramPacket requestResend = new DatagramPacket(sendData, sendData.length, hostResend, getLeaderUDPPort());
                    aSocket.send(requestResend);
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        finally{
            if(aSocket != null){
                aSocket.close();
            }
            return result;
        }
    }

    /**
     * assign the leader port
     * @return
     */
    public static Integer getLeaderUDPPort(){
        return FrontEndConfig.FEListener1;
    }
}
