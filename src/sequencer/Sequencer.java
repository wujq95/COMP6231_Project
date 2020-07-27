package sequencer;

import config.PortConfig;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Sequencer {

    private static Integer sequencerNumber = 1;

    public static synchronized int getRequestID(){
        return sequencerNumber++;
    }


    public static void main(String[] args){
        DatagramSocket aSocket = null;

        try {
            aSocket = new DatagramSocket(PortConfig.sequencerPort);
            DatagramPacket request;
            byte[] data;
            int count = 0;
            System.out.println("Sequencer UDP Server Started");
            while(true){

                data = new byte[1024];
                request = new DatagramPacket(data, data.length);
                aSocket.receive(request);

                String requestData = new String(request.getData()).trim();
                System.out.println(requestData);

                String result  = String.format("%04d", getRequestID());

                DatagramPacket reply = new DatagramPacket(result.getBytes(),result.getBytes().length, request.getAddress(), request.getPort());
                aSocket.send(reply);

                //String FEIpAddress = packet.getAddress().getHostAddress();
                //String receiveMessage = new String(packet.getData(), 0, packet.getLength());

                //******
                //packetAndSendMessage(FEIpAddress, receiveMessage, aSocket);

                //count++;
                //System.out.println("Server Connected：" + count);
                //InetAddress address = packet.getAddress();
                //System.out.println("Server IP："+address.getHostAddress());
            }
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        } finally {
            if (aSocket != null)
                aSocket.close();
        }
    }

    public static synchronized void packetAndSendMessage(String FEIpAddress, String receiveMessage, DatagramSocket socket) throws IOException {

        String sendMessage = sequencerNumber.toString() + ":" + FEIpAddress + ":" + receiveMessage;
        sequencerNumber++;

        //2.multicast the message to replica
        multicastMessage(sendMessage, socket);
    }


    private static void multicastMessage(String packageMessage, DatagramSocket socket) throws IOException {

        //TODO：可能不是本地的localhost，需要修改为replica主机的ip
        InetAddress address = InetAddress.getByName("localhost");

        byte[] data = packageMessage.getBytes();
        DatagramPacket sendPacket1 = new DatagramPacket(data, data.length, address, PortConfig.replica1);
        //DatagramPacket sendPacket2 = new DatagramPacket(data, data.length, address, PortConfig.replica2);
        //DatagramPacket sendPacket3 = new DatagramPacket(data, data.length, address, PortConfig.replica3);

        socket.send(sendPacket1);
        //socket.send(sendPacket2);
        //socket.send(sendPacket3);
    }


}
