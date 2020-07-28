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

    public FrontEndImpl(){

    }

    public void setORB(ORB orb_val) {
        orb = orb_val;
    }

    @Override
    public synchronized String createPlayerAccount(String firstName, String lastName, String age, String userName, String password, String ipAddress) {
        String message = "createPlayerAccount"+"|"+firstName+"|"+lastName+"|"+age+"|"+userName+"|"+password+"|"+ipAddress;
        String result = sendMsgToLeader(message);
        return result;
    }

    @Override
    public String playerSignIn(String userName, String password, String ipAddress) {
        String message = "playerSignIn"+"|"+userName+"|"+password+"|"+ipAddress;
        String result = sendMsgToLeader(message);
        return result;
    }

    @Override
    public String playerSignOut(String userName, String ipAddress) {
        String message = "playerSignOut"+"|"+userName+"|"+ipAddress;
        String result = sendMsgToLeader(message);
        return result;
    }

    @Override
    public String getPlayerStatus(String adminUsername, String adminPassword, String ipAddress) {
        String message = "getPlayerStatus"+"|"+adminUsername+"|"+adminPassword+"|"+ipAddress;
        String result = sendMsgToLeader(message);
        return result;
    }

    @Override
    public String transferAccount(String userName, String password, String oldIPAddress, String newIPAddress) {
        String message = "transferAccount"+"|"+userName+"|"+password+"|"+oldIPAddress+"|"+newIPAddress;
        String result = sendMsgToLeader(message);
        return result;
    }

    @Override
    public String suspendAccount(String adminUsername, String adminPassword, String ipAddress, String usernameToSuspend) {
        String message = "suspendAccount"+"|"+adminUsername+"|"+adminPassword+"|"+ipAddress+"|"+usernameToSuspend;
        String result = sendMsgToLeader(message);
        return result;
    }

    @Override
    public void shutdown() {
    }


    public static String getNumberFromRequestIDGenerator(){
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket();
            byte[] message = ("ask").getBytes();
            InetAddress host = InetAddress.getByName("localhost");
            DatagramPacket request = new DatagramPacket(message, message.length, host, PortConfig.sequencerPort);
            socket.send(request);
            byte[] buffer = new byte[100];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            socket.receive(reply);
            String result = new String(reply.getData()).trim();
            return result;
        }
        catch(Exception e){
            System.out.println("Socket: " + e.getMessage());
        }
        finally{
            if(socket != null){
                socket.close();
            }
        }
        return null;
    }

    public static String sendMsgToLeader(String message){

        System.out.println("Front End Sends message to the leader: "+message);

        DatagramSocket aSocket = null;

        //front server 和impl这里需要处理一下
        //现在server那边是接不到东西的
        try {
            aSocket = new DatagramSocket();
            byte[] sendData = message.getBytes();
            InetAddress host = InetAddress.getByName("localhost");
            Integer leaderUDPPort = getLeaderUDPPort();
            DatagramPacket request = new DatagramPacket(sendData, message.length(), host,leaderUDPPort);
            aSocket.send(request);
            aSocket.setSoTimeout(5000);
            byte[] buffer = new byte[1000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            while(true){
                try {
                    aSocket.receive(reply);
                    String result = new String(reply.getData()).trim();
                    System.out.println("front end gets the result: "+result);
                    return result;
                } catch (SocketTimeoutException e) {
                    InetAddress hostResend = InetAddress.getByName("localhost");
                    DatagramPacket requestResend = new DatagramPacket(sendData, sendData.length, hostResend, getLeaderUDPPort());
                    aSocket.send(requestResend);
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            if(aSocket != null){
                aSocket.close();
            }
        }
        return null;
    }

    public static Integer getLeaderUDPPort(){

        return PortConfig.leader1;
    }
}
