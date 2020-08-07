package frontend;

import config.PortConfig;
import frontend.FrontEndModule.FrontEndPOA;
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

    @Override
    public String createPlayerAccount(String firstName, String lastName, String age, String userName, String password, String ipAddress) {
        synchronized (this){
            String message = "createPlayerAccount"+"|"+firstName+"|"+lastName+"|"+age+"|"+userName+"|"+password+"|"+ipAddress;
            String result = sendMsgToLeader(message);
            return result;
        }
    }

    @Override
    public String playerSignIn(String userName, String password, String ipAddress) {
        synchronized (this) {
            String message = "playerSignIn" + "|" + userName + "|" + password + "|" + ipAddress;
            String result = sendMsgToLeader(message);
            return result;
        }
    }

    @Override
    public String playerSignOut(String userName, String ipAddress) {
        synchronized (this){
            String message = "playerSignOut"+"|"+userName+"|"+ipAddress;
            String result = sendMsgToLeader(message);
            return result;
        }
    }

    @Override
    public String getPlayerStatus(String adminUsername, String adminPassword, String ipAddress) {
        synchronized (this){
            String message = "getPlayerStatus"+"|"+adminUsername+"|"+adminPassword+"|"+ipAddress;
            String result = sendMsgToLeader(message);
            return result;
        }
    }

    @Override
    public String transferAccount(String userName, String password, String oldIPAddress, String newIPAddress) {
        synchronized (this){
            String message = "transferAccount"+"|"+userName+"|"+password+"|"+oldIPAddress+"|"+newIPAddress;
            String result = sendMsgToLeader(message);
            return result;
        }
    }

    @Override
    public String suspendAccount(String adminUsername, String adminPassword, String ipAddress, String usernameToSuspend) {
        synchronized (this){
            String message = "suspendAccount"+"|"+adminUsername+"|"+adminPassword+"|"+ipAddress+"|"+usernameToSuspend;
            String result = sendMsgToLeader(message);
            return result;
        }
    }

    @Override
    public void shutdown() {
        orb.shutdown(false);
    }

    public static String sendMsgToLeader(String message){

        DatagramSocket aSocket = null;
        String result = "Network failure!";

        try {
            aSocket = new DatagramSocket();
            byte[] sendData = message.getBytes();
            InetAddress host = InetAddress.getByName("localhost");
            Integer leaderUDPPort = getLeaderUDPPort();
            System.out.println("Front end sends the request to the leader: "+message);
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

    public static Integer getLeaderUDPPort(){
        return PortConfig.leader1;
    }
}
