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
        System.out.println("Leader FrontEnd Listener has started");

        Runnable broadCastTaskUDP = () -> {
            try {
                listenBroadCast();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        new Thread(broadCastTaskUDP).start();
        System.out.println("Leader BroadCast Listener has started");
        System.out.println("Leader Server has started");
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
                System.out.println("leader gets the message from the front end:"+requestData);

                String result1 = broadCast(requestData);
                //String result2 = broadCast(requestData);
                String result3 = operation(requestData);
                ArrayList<String> res = new ArrayList<>();
                if(result1==null){
                    String Msg = "Server 1 is fail";
                    notifyRM(Msg);
                }else{
                    res.add(result1);
                }
                /*if(result2.equals("fail")){
                    String Msg = "Server 2 is fail";
                    notifyRM(Msg);
                }else{
                    res.add(result2);
                }*/
                if(result3==null){
                    String Msg = "Server 3 is fail";
                    notifyRM(Msg);
                }else{
                    res.add(result3);
                }
                String result;

                if(res.size()==0){
                    result = "System has a crash that no reply got";
                }else if(res.size()==1){
                    result = res.get(0);
                }else if(res.size()==2){
                    if(res.get(0).startsWith("S")){
                        result = res.get(0);
                    }else{
                        result = res.get(1);
                    }
                    //这里需要处理一下
                }else{
                    if(res.get(1).equals(res.get(2))){
                        result = res.get(1);
                    }else {
                        result = res.get(0);
                    }
                }
                //自己的server corba处理
                //接收另外两个server的结果
                //设置timeout，超时报告给rm
                //有结果不对的报告rm
                //选择一个正确的结果传回去

                //这里要保证至少一个不是null，不然会空指针

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

    public static String listenBroadCast() throws Exception{
        DatagramSocket aSocket = null;
        try{
            aSocket = new DatagramSocket(6666);
            byte[] buffer = new byte[1000];
            while(true){
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);
                String requestData = new String(request.getData()).trim();
                System.out.println("replica1 gets the broadcast:" + requestData);
                String result = operation(requestData);
                System.out.println(result);
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
        return "fail";
    }

    public static String broadCast(String message){
        DatagramSocket aSocket = null;
        try{
            aSocket = new DatagramSocket();
            byte[] sendData = message.getBytes();
            InetAddress host = InetAddress.getByName("localhost");
            DatagramPacket request = new DatagramPacket(sendData, message.length(), host,PortConfig.leader2);
            aSocket.send(request);
            aSocket.setSoTimeout(5000);
            byte[] buffer = new byte[1000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            while(true){
                aSocket.receive(reply);
                String result = new String(reply.getData()).trim();
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
        return null;
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
                obj = DPSSHelper.narrow(ncRef.resolve_str("EU"));
            }else if(strs[6].startsWith("132.")){
                obj = DPSSHelper.narrow(ncRef.resolve_str("NA"));
            }else{
                obj = DPSSHelper.narrow(ncRef.resolve_str("AS"));
            }
            result = obj.createPlayerAccount(strs[1],strs[2],strs[3],strs[4],strs[5],strs[6]);
        }else if(str.startsWith("playerSignIn|")){
            if(strs[3].startsWith("93.")){
                obj = DPSSHelper.narrow(ncRef.resolve_str("EU"));
            }else if(strs[3].startsWith("132.")){
                obj = DPSSHelper.narrow(ncRef.resolve_str("NA"));
            }else{
                obj = DPSSHelper.narrow(ncRef.resolve_str("AS"));
            }
            result = obj.playerSignIn(strs[1],strs[2],strs[3]);
        }else if(str.startsWith("playerSignOut|")){
            if(strs[2].startsWith("93.")){
                obj = DPSSHelper.narrow(ncRef.resolve_str("EU"));
            }else if(strs[2].startsWith("132.")){
                obj = DPSSHelper.narrow(ncRef.resolve_str("NA"));
            }else{
                obj = DPSSHelper.narrow(ncRef.resolve_str("AS"));
            }
            result = obj.playerSignOut(strs[1],strs[2]);
        }else if(str.startsWith("getPlayerStatus|")){
            if(strs[3].startsWith("93.")){
                obj = DPSSHelper.narrow(ncRef.resolve_str("EU"));
            }else if(strs[3].startsWith("132.")){
                obj = DPSSHelper.narrow(ncRef.resolve_str("NA"));
            }else{
                obj = DPSSHelper.narrow(ncRef.resolve_str("AS"));
            }
            result = obj.getPlayerStatus(strs[1],strs[2],strs[3]);
        }else if(str.startsWith("transferAccount|")){
            if(strs[3].startsWith("93.")){
                obj = DPSSHelper.narrow(ncRef.resolve_str("EU"));
            }else if(strs[3].startsWith("132.")){
                obj = DPSSHelper.narrow(ncRef.resolve_str("NA"));
            }else{
                obj = DPSSHelper.narrow(ncRef.resolve_str("AS"));
            }
            result = obj.transferAccount(strs[1],strs[2],strs[3],strs[4]);
        }else if(str.startsWith("suspendAccount|")){
            if(strs[3].startsWith("93.")){
                obj = DPSSHelper.narrow(ncRef.resolve_str("EU"));
            }else if(strs[3].startsWith("132.")){
                obj = DPSSHelper.narrow(ncRef.resolve_str("NA"));
            }else{
                obj = DPSSHelper.narrow(ncRef.resolve_str("AS"));
            }
            result = obj.suspendAccount(strs[1],strs[2],strs[3],strs[4]);
        }
        return result;
    }

    public static void notifyRM(String Msg){
        System.out.println("notify rm:"+Msg);
    }
}
