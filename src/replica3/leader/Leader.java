package replica3.leader;

import replica3.config.PortConfig;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import replica3.DPSSModule.DPSS;
import replica3.DPSSModule.DPSSHelper;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
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
        System.out.println("Leader frontend listener3 has started");

        Runnable broadCastTaskUDP = () -> {
            try {
                listenBroadCast();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        new Thread(broadCastTaskUDP).start();
        System.out.println("Leader broadCast listener3 has started");
        System.out.println("Leader server3 has started");
    }

    /**
     * The listener for front end requests
     * @throws Exception
     */
    public static void listenFrontEnd() throws Exception{
        DatagramSocket aSocket = null;
        try {
            aSocket = new DatagramSocket(PortConfig.FEListener3);
            while(true){
                byte[] buffer = new byte[1024];
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);
                String requestData = new String(request.getData());
                System.out.println("The leader3 receives the message from the front end: "+requestData.trim());

                String result;
                String result1 = operation(requestData);
                String result2 = broadCast(requestData,PortConfig.leader1);
                String result3 = broadCast(requestData,PortConfig.leader2);
                ArrayList<String> res = new ArrayList<>();
                if(result1==null||result1.equals("Random incorrect result")){
                    String Msg = "RM3 failure";
                    notifyRM(Msg,PortConfig.RMPort3);
                }else{
                    res.add(result1);
                }
                if(result2==null||result2.equals("Random incorrect result")){
                    String Msg = "RM1 failure";
                    notifyRM(Msg,PortConfig.RMPort1);
                }else{
                    res.add(result2);
                }
                if(result3==null||result3.equals("Random incorrect result")){
                    String Msg = "RM3 failure";
                    notifyRM(Msg,PortConfig.RMPort2);
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

    /**
     * The listener for broadcast from other leaders
     * @throws Exception
     */
    public static void listenBroadCast() throws Exception{
        DatagramSocket aSocket = null;
        try{
            aSocket = new DatagramSocket(PortConfig.leader3);
            byte[] buffer = new byte[1000];
            while(true){
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);
                String requestData = new String(request.getData()).trim();
                System.out.println("Leader3 receives a message from broadcast: "+requestData.trim());
                String result = operation(requestData);
                byte[] sendData = result.getBytes();
                DatagramPacket reply = new DatagramPacket(sendData, result.length(), request.getAddress(),
                        request.getPort());
                System.out.println("Leader3 replies to broadcast: "+result.trim());
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

    /**
     * broadcast message to other leaders
     * @param message
     * @param port
     * @return
     */
    public static String broadCast(String message,Integer port){
        DatagramSocket aSocket = null;
        String result = null;
        try{
            aSocket = new DatagramSocket();
            byte[] sendData = message.getBytes();
            InetAddress host = InetAddress.getByName("localhost");
            DatagramPacket request = new DatagramPacket(sendData, message.length(), host,port);
            System.out.println("Lead1 broadcast request to other two leaders:"+message.trim());
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

    /**
     * Use corba to do the operations
     * @param str
     * @return
     * @throws Exception
     */
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
                obj = DPSSHelper.narrow(ncRef.resolve_str("EU3"));
            }else if(strs[6].startsWith("132.")){
                obj = DPSSHelper.narrow(ncRef.resolve_str("NA3"));
            }else{
                obj = DPSSHelper.narrow(ncRef.resolve_str("AS3"));
            }
            result = obj.createPlayerAccount(strs[1],strs[2],strs[3],strs[4],strs[5],strs[6]);
        }else if(str.startsWith("playerSignIn|")){
            if(strs[3].startsWith("93.")){
                obj = DPSSHelper.narrow(ncRef.resolve_str("EU3"));
            }else if(strs[3].startsWith("132.")){
                obj = DPSSHelper.narrow(ncRef.resolve_str("NA3"));
            }else{
                obj = DPSSHelper.narrow(ncRef.resolve_str("AS3"));
            }
            result = obj.playerSignIn(strs[1],strs[2],strs[3]);
        }else if(str.startsWith("playerSignOut|")){
            if(strs[2].startsWith("93.")){
                obj = DPSSHelper.narrow(ncRef.resolve_str("EU3"));
            }else if(strs[2].startsWith("132.")){
                obj = DPSSHelper.narrow(ncRef.resolve_str("NA3"));
            }else{
                obj = DPSSHelper.narrow(ncRef.resolve_str("AS3"));
            }
            result = obj.playerSignOut(strs[1],strs[2]);
        }else if(str.startsWith("getPlayerStatus|")){
            if(strs[3].startsWith("93.")){
                obj = DPSSHelper.narrow(ncRef.resolve_str("EU3"));
            }else if(strs[3].startsWith("132.")){
                obj = DPSSHelper.narrow(ncRef.resolve_str("NA3"));
            }else{
                obj = DPSSHelper.narrow(ncRef.resolve_str("AS3"));
            }
            result = obj.getPlayerStatus(strs[1],strs[2],strs[3]);
        }else if(str.startsWith("transferAccount|")){
            if(strs[3].startsWith("93.")){
                obj = DPSSHelper.narrow(ncRef.resolve_str("EU3"));
            }else if(strs[3].startsWith("132.")){
                obj = DPSSHelper.narrow(ncRef.resolve_str("NA3"));
            }else{
                obj = DPSSHelper.narrow(ncRef.resolve_str("AS3"));
            }
            result = obj.transferAccount(strs[1],strs[2],strs[3],strs[4]);
        }else if(str.startsWith("suspendAccount|")){
            if(strs[3].startsWith("93.")){
                obj = DPSSHelper.narrow(ncRef.resolve_str("EU3"));
            }else if(strs[3].startsWith("132.")){
                obj = DPSSHelper.narrow(ncRef.resolve_str("NA3"));
            }else{
                obj = DPSSHelper.narrow(ncRef.resolve_str("AS3"));
            }
            result = obj.suspendAccount(strs[1],strs[2],strs[3],strs[4]);
        }

        //simulate random failure
        /*if(Math.random()<0.1){
            result = "Random incorrect result";
            System.out.println("A random incorrect result appears");
        }*/
        return result;
    }

    /**
     * notify the failure information to the replica manager
     * @param Msg
     * @param port
     */
    public static void notifyRM(String Msg,Integer port){
        DatagramSocket aSocket = null;
        try {
            aSocket = new DatagramSocket();
            byte[] sendData = Msg.getBytes();
            InetAddress host = InetAddress.getByName("localhost");
            DatagramPacket request = new DatagramPacket(sendData, Msg.length(), host, port);
            System.out.println("Leader3 notify a failure to replica manager: "+Msg.trim());
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
