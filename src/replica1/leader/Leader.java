package replica1.leader;

import config.PortConfig;
import frontend.DPSSModule.DPSS;
import frontend.DPSSModule.DPSSHelper;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import java.io.IOException;
import java.net.*;
import java.util.Properties;
import java.util.Scanner;

public class Leader {

     static final Integer leaderUDPPort1 = 6000;
     static final Integer leaderUDPPort2 = 6003;
     static final Integer leaderUDPPort3 = 6004;

    public static void main(String[] args) {
        Runnable frontEndTaskUDP = () -> {
            try {
                listenFrontEnd(leaderUDPPort1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        new Thread(frontEndTaskUDP).start();
        System.out.println("Leader FrontEnd Listener has started");

        Runnable broadCastTaskUDP = () -> {
            try {
                listenBroadCast(leaderUDPPort2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        new Thread(broadCastTaskUDP).start();
        System.out.println("Leader BroadCast Listener has started");
        System.out.println("Leader Server has started");
    }

    public static void listenFrontEnd(Integer port) throws Exception{
        DatagramSocket aSocket = null;
        try {
            aSocket = new DatagramSocket(port);
            byte[] buffer = new byte[1024];
            while(true){
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);
                String requestData = new String(request.getData());
                System.out.println(requestData);

                //String result1 = broadCast(requestData,6001);
                //String result2 = broadCast(requestData,6002);
                String result3 = resultFromThis();
                /*if(result1.equals("fail")){
                    String Msg = "Server 1 is fail";
                    notifyRM(Msg);
                }
                if(result2.equals("fail")){
                    String Msg = "Server 2 is fail";
                    notifyRM(Msg);
                }*/
                if(result3.equals("fail")){
                    String Msg = "Server 3 is fail";
                    notifyRM(Msg);
                }

                //自己的server corba处理
                //接收另外两个server的结果
                //设置timeout，超时报告给rm
                //有结果不对的报告rm
                //选择一个正确的结果传回去


                byte[] sendData = result3.getBytes();
                DatagramPacket reply = new DatagramPacket(sendData, result3.length(), request.getAddress(),
                        request.getPort());
                aSocket.send(reply);

            }
        }catch (SocketException e) {
            System.out.println("SocketException: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        } finally {
            if (aSocket != null)
                aSocket.close();
        }
    }

    public static String listenBroadCast(Integer port) throws Exception{
        DatagramSocket aSocket = null;
        try{
            aSocket = new DatagramSocket(port);
            byte[] buffer = new byte[1000];
            while(true){
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);
                String requestData = new String(request.getData()).trim();
                System.out.println("replica1 gets the broadcast:" + requestData);
                String result = replica1Corba();
                System.out.println(result);
                return result;
            }
        }catch (SocketException e) {
            System.out.println("SocketException: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        } finally {
            if (aSocket != null)
                aSocket.close();
        }
        return "fail";
    }

    public static void broadCast(String message,Integer replicaUdpPort2){
        DatagramSocket aSocket = null;
        String[] res = new String[]{"fail","fail"};
        try{
            aSocket = new DatagramSocket(replicaUdpPort2);
            byte[] sendData = message.getBytes();
            InetAddress host = InetAddress.getByName("localhost");
            DatagramPacket request = new DatagramPacket(sendData, message.length(), host,6003);
            //DatagramPacket request2 = new DatagramPacket(sendData, message.length(), host,6004);
            aSocket.send(request);
            //aSocket.setSoTimeout(5000);
            //aSocket.send(request2);
            aSocket.setSoTimeout(5000);
            byte[] buffer = new byte[1000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            while(true){
                aSocket.receive(reply);
                String result = new String(reply.getData()).trim();
                if(result.startsWith("02")){
                    res[0] = result;
                }else {
                    res[1] = result;
                }
            }
        }catch (SocketException e) {
            System.out.println("SocketException: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        } finally {
            if (aSocket != null)
                aSocket.close();
        }
    }

    public static String resultFromThis() throws Exception{
        DPSS obj;
        Properties props = new Properties();
        props.put("org.omg.CORBA.ORBInitialPort", "1050");
        props.put("org.omg.CORBA.ORBInitialHost", "localhost");
        // create and initialize the ORB
        ORB orb = ORB.init(new String[0], props);
        // get the root naming context
        org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
        // Use NamingContextExt instead of NamingContext. This is part of the Interoperable naming Service.
        NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
        obj = DPSSHelper.narrow(ncRef.resolve_str("EU"));
        //return obj.playerSignIn("guanyu","gunayu","93.111.111.111");
        return "result3333";
    }

    public static void notifyRM(String Msg){

        System.out.println("notify rm:"+Msg);
    }

    public static String replica1Corba() throws Exception {

        DPSS obj;
        Properties props = new Properties();
        props.put("org.omg.CORBA.ORBInitialPort", "1050");
        props.put("org.omg.CORBA.ORBInitialHost", "localhost");
        // create and initialize the ORB
        ORB orb = ORB.init(new String[0], props);
        // get the root naming context
        org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
        // Use NamingContextExt instead of NamingContext. This is part of the Interoperable naming Service.
        NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
        obj = DPSSHelper.narrow(ncRef.resolve_str("EU"));
        return obj.playerSignIn("guanyu","gunayu","93.111.111.111");
    }
}
