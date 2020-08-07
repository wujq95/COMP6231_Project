package replica1.leader;

import config.PortConfig;
import replica1.DPSSModule.DPSS;
import replica1.DPSSModule.DPSSHelper;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Leader {

    public static void main(String[] args) {
        Runnable frontEndTaskUDP = () -> {
            try {
                listenFrontEnd();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        new Thread(frontEndTaskUDP).start();
        System.out.println("Leader frontend listener1 has Started");

        Runnable broadCastTaskUDP = () -> {
            try {
                listenBroadCast();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        new Thread(broadCastTaskUDP).start();
        System.out.println("Leader broadcast listener1 has started");
        System.out.println("Leader server1 has started");
    }

    public static void listenFrontEnd() throws Exception{
        DatagramSocket aSocket = null;
        try {
            aSocket = new DatagramSocket(PortConfig.leader1);
            byte[] buffer = new byte[1024];
            while(true){
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);
                String requestData = new String(request.getData());
                System.out.println("The leader1 receives the message from the front end: "+requestData);

                String result;
                String result1 = operation(requestData);
                String result2 = broadCast(requestData,PortConfig.leader2);
                String result3 = broadCast(requestData,PortConfig.leader3);

                ArrayList<String> res = new ArrayList<>();
                if(result1==null){
                    String Msg = "RM1";
                    notifyRM(Msg,PortConfig.RMPort1);
                }else{
                    res.add(result1);
                }
                if(result2==null){
                    String Msg = "RM2";
                    notifyRM(Msg,PortConfig.RMPort2);
                }else{
                    res.add(result2);
                }
                if(result3==null){
                    String Msg = "RM3";
                    notifyRM(Msg,PortConfig.RMPort3);
                }else{
                    res.add(result3);
                }
                System.out.println("result1:"+result1);
                System.out.println("result2:"+result2);
                System.out.println("result3:"+result3);
                if(res.size()==0){
                    result = "No reply";
                }else if(res.size()==1){
                    result = res.get(0);
                }else if(res.size()==2){
                    if(res.get(0).startsWith("S")){
                        result = res.get(0);
                    }else{
                        result = res.get(1);
                    }
                }else{
                    if(res.get(1).equals(res.get(2))){
                        result = res.get(1);
                    }else {
                        result = res.get(0);
                    }
                }
                if(result==null){
                    result = "No reply";
                }

                byte[] sendData = result.getBytes();
                DatagramPacket reply = new DatagramPacket(sendData, result.length(), request.getAddress(),
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

    public static void listenBroadCast() throws Exception{
        DatagramSocket aSocket = null;
        try{
            aSocket = new DatagramSocket(PortConfig.leader1);
            byte[] buffer = new byte[1000];
            while(true){
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);
                String requestData = new String(request.getData()).trim();
                String result = operation(requestData);
                byte[] sendData = result.getBytes();
                DatagramPacket reply = new DatagramPacket(sendData, result.length(), request.getAddress(),
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

    public static String broadCast(String message,Integer port){
        DatagramSocket aSocket = null;
        String result = null;
        try{
            aSocket = new DatagramSocket();
            byte[] sendData = message.getBytes();
            InetAddress host = InetAddress.getByName("localhost");
            DatagramPacket request = new DatagramPacket(sendData, message.length(), host,port);
            aSocket.send(request);
            try {
                aSocket.setSoTimeout(2000);
                byte[] buffer = new byte[1000];
                DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(reply);
                result = new String(reply.getData()).trim();
            }catch (SocketTimeoutException e){
                result = null;
            }
        }catch (SocketException e) {
            System.out.println("SocketException: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        } finally {
            if (aSocket != null)
                aSocket.close();
            return result;
        }
    }

    public static String operation(String str) throws Exception{
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

        String[] strs = str.split("\\|");
        String result = null;
        if(str.startsWith("createPlayerAccount|")){
            if(strs[6].startsWith("93.")){
                obj = DPSSHelper.narrow(ncRef.resolve_str("EU1"));
            }else if(strs[6].startsWith("132.")){
                obj = DPSSHelper.narrow(ncRef.resolve_str("NA1"));
            }else{
                obj = DPSSHelper.narrow(ncRef.resolve_str("AS1"));
            }
            result = obj.createPlayerAccount(strs[1],strs[2],strs[3],strs[4],strs[5],strs[6]);
        }else if(str.startsWith("playerSignIn|")){
            if(strs[3].startsWith("93.")){
                obj = DPSSHelper.narrow(ncRef.resolve_str("EU1"));
            }else if(strs[3].startsWith("132.")){
                obj = DPSSHelper.narrow(ncRef.resolve_str("NA1"));
            }else{
                obj = DPSSHelper.narrow(ncRef.resolve_str("AS1"));
            }
            result = obj.playerSignIn(strs[1],strs[2],strs[3]);
        }else if(str.startsWith("playerSignOut|")){
            if(strs[2].startsWith("93.")){
                obj = DPSSHelper.narrow(ncRef.resolve_str("EU1"));
            }else if(strs[2].startsWith("132.")){
                obj = DPSSHelper.narrow(ncRef.resolve_str("NA1"));
            }else{
                obj = DPSSHelper.narrow(ncRef.resolve_str("AS1"));
            }
            result = obj.playerSignOut(strs[1],strs[2]);
        }else if(str.startsWith("getPlayerStatus|")){
            if(strs[3].startsWith("93.")){
                obj = DPSSHelper.narrow(ncRef.resolve_str("EU1"));
            }else if(strs[3].startsWith("132.")){
                obj = DPSSHelper.narrow(ncRef.resolve_str("NA1"));
            }else{
                obj = DPSSHelper.narrow(ncRef.resolve_str("AS1"));
            }
            result = obj.getPlayerStatus(strs[1],strs[2],strs[3]);
        }else if(str.startsWith("transferAccount|")){
            if(strs[3].startsWith("93.")){
                obj = DPSSHelper.narrow(ncRef.resolve_str("EU1"));
            }else if(strs[3].startsWith("132.")){
                obj = DPSSHelper.narrow(ncRef.resolve_str("NA1"));
            }else{
                obj = DPSSHelper.narrow(ncRef.resolve_str("AS1"));
            }
            result = obj.transferAccount(strs[1],strs[2],strs[3],strs[4]);
        }else if(str.startsWith("suspendAccount|")){
            if(strs[3].startsWith("93.")){
                obj = DPSSHelper.narrow(ncRef.resolve_str("EU1"));
            }else if(strs[3].startsWith("132.")){
                obj = DPSSHelper.narrow(ncRef.resolve_str("NA1"));
            }else{
                obj = DPSSHelper.narrow(ncRef.resolve_str("AS1"));
            }
            result = obj.suspendAccount(strs[1],strs[2],strs[3],strs[4]);
        }
        return result;
    }

    public static void notifyRM(String Msg,Integer port){
        DatagramSocket aSocket = null;
        try {
            aSocket = new DatagramSocket();
            byte[] sendData = Msg.getBytes();
            InetAddress host = InetAddress.getByName("localhost");
            DatagramPacket request = new DatagramPacket(sendData, Msg.length(), host, port);
            aSocket.send(request);
            while(true){
                try {
                    byte[] buffer = new byte[1000];
                    DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                    aSocket.setSoTimeout(2000);
                    aSocket.receive(reply);
                    break;
                } catch (SocketTimeoutException e) {
                    InetAddress hostResend = InetAddress.getByName("localhost");
                    DatagramPacket requestResend = new DatagramPacket(sendData, sendData.length, hostResend, port);
                    aSocket.send(requestResend);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        finally{
            if(aSocket != null){
                aSocket.close();
            }
        }

    }
}
