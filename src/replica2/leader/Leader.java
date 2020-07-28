package replica2.leader;

import config.PortConfig;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Leader {

    public static void main(String[] args) {

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


    public static String listenBroadCast(){
        DatagramSocket aSocket = null;
        try{
            aSocket = new DatagramSocket(PortConfig.leader2);
            byte[] buffer = new byte[1000];
            while(true){
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);
                String requestData = new String(request.getData()).trim();
                System.out.println("replica2 gets the broadcast:" + requestData);
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

    public static String operation(String request){
        return "Success replica2";
    }
}
