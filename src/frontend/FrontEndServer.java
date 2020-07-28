package frontend;

import config.PortConfig;
import frontend.DPSSModule.DPSS;
import frontend.DPSSModule.DPSSHelper;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Properties;

public class FrontEndServer {

    public static void main(String[] args) {

        Runnable taskUDP = () -> {
            try {
                frontEndListen();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        new Thread(taskUDP).start();

        FrontEndImpl servant = new FrontEndImpl();
        Properties props = new Properties();
        props.put("org.omg.CORBA.ORBInitialPort", "1050");
        props.put("org.omg.CORBA.ORBInitialHost", "localhost");

        try {
            // create and initialize the ORB
            ORB orb = ORB.init(args, props);
            // get reference to rootpoa & activate the POAManager
            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();
            // create servant and register it with the ORB
            servant.setORB(orb);
            // get object reference from the servant
            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(servant);
            // and cast the reference to a CORBA reference
            DPSS href = DPSSHelper.narrow(ref);
            // get the root naming context
            // NameService invokes the transient name service
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            // Use NamingContextExt, which is part of the
            // Interoperable Naming Service (INS) specification.
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
            // bind the Object Reference in Naming
            String name = "FrontEnd";
            NameComponent path[] = ncRef.to_name(name);
            ncRef.rebind(path, href);
            System.out.println("Front End Server is ready and listening ...  ...");

            // wait for invocations from clients
            orb.run();
        }catch (Exception e) {
            System.err.println("ERROR: " + e);
            e.printStackTrace(System.out);
        }

        System.out.println("Front Server Server Exiting ...");
    }

    public static void frontEndListen(){
        DatagramSocket aSocket = null;
        try{
            aSocket = new DatagramSocket(PortConfig.FrontEndPort);
            byte[] buffer = new byte[1000];
            while(true){
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);
                String requestData = new String(request.getData()).trim();
                System.out.println("front end listen: "+requestData);



                //需要设置time out的时间
                //需要设置发送的id，并与接收的比较


            /*
            if(requestData.equals("who is leader?")){
                String resend_leader_port = Integer.toString(Front_End_Config.PRIMARY_SERVER_PORT);
                DatagramPacket reply_leader = new DatagramPacket(resend_leader_port.getBytes(),resend_leader_port.getBytes().length, request.getAddress(), request.getPort());
                aSocket.send(reply_leader);
            }else{
                String receivedContent = new String(request.getData()).trim();
                int leader_port = Integer.parseInt(receivedContent.substring(9));
                Front_End_Config.PRIMARY_SERVER_PORT = leader_port;
                String acknowledgement = "OK";
                DatagramPacket update_leader = new DatagramPacket(acknowledgement.getBytes(),acknowledgement.getBytes().length, request.getAddress(), request.getPort());
                System.out.println("new leader is " + leader_port);
                socket.send(update_leader);
            }
            */
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

}