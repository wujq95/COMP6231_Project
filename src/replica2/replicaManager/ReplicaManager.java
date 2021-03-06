package replica2.replicaManager;

import replica2.config.PortConfig;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ReplicaManager {

    public static Integer RMFailureCount = 0;

    /**
     * replica manager main method
     * @param args
     */
    public static void main(String[] args) {
        Runnable taskUDP = () -> {
            try {
                RMListener();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        new Thread(taskUDP).start();
        System.out.println("Replica Manager2 is ready and listening");
    }

    /**
     * replica manager listener
     */
    public static void RMListener(){
        DatagramSocket aSocket = null;
        try{
            aSocket = new DatagramSocket(PortConfig.RMPort2);
            byte[] buffer = new byte[1000];
            while(true){
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);
                String requestData = new String(request.getData()).trim();
                System.out.println("Replica manager listener2 receives a request: "+requestData);
                RMFailureCount++;
                if(RMFailureCount>=3){
                    reload();
                    RMFailureCount=0;
                }
                byte[] sendData = "ok".getBytes();
                DatagramPacket reply = new DatagramPacket(sendData, "ok".length(), request.getAddress(),
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


    public static void reload(){
        System.out.println("RM2 server reload");
    }
}
