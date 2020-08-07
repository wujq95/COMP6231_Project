package replica2.as;

import config.PortConfig;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import replica2.DPSSModule.DPSS;
import replica2.DPSSModule.DPSSHelper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AsGameServer {

    public static ConcurrentHashMap<Character, List<String>> asiaMap = new ConcurrentHashMap<>();
    public static Set<String> asiaOnline = Collections.synchronizedSet(new HashSet<>());


    /**
     * main method
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        load();
        Runnable taskUDP = () -> {
            try {
                serverReceive();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        new Thread(taskUDP).start();
        AsGameClass asGameClass = new AsGameClass();
        System.out.println("Asia server2 has been started");
        Properties props = new Properties();
        props.put("org.omg.CORBA.ORBInitialPort", "1050");
        props.put("org.omg.CORBA.ORBInitialHost", "localhost");
        try{
            // create and initialize the ORB
            ORB orb = ORB.init(args, props);
            // get reference to rootpoa & activate the POAManager
            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();
            // create servant and register it with the ORB
            asGameClass.setORB(orb);
            // get object reference from the servant
            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(asGameClass);
            // and cast the reference to a CORBA reference
            DPSS href = DPSSHelper.narrow(ref);
            // get the root naming context
            // NameService invokes the transient name service
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            // Use NamingContextExt, which is part of the
            // Interoperable Naming Service (INS) specification.
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
            // bind the Object Reference in Naming
            String name = "AS2";
            NameComponent path[] = ncRef.to_name(name);
            ncRef.rebind(path, href);
            System.out.println("Asia Server2 is ready and listening ...  ...");
            // wait for invocations from clients
            orb.run();
        }catch (Exception e) {
            System.err.println("ERROR: " + e);
            e.printStackTrace(System.out);
        }
        System.out.println("Asia Server2 Exiting ...");
    }
    /**
     * add a listener for udp message
     * @throws Exception
     */
    public static void serverReceive(){
        DatagramSocket aSocket = null;
        try {
            aSocket = new DatagramSocket(PortConfig.replicaAS2);
            byte[] buffer = new byte[1024];
            while (true){
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);
                String requestData = new String(request.getData());
                String result;
                if(request.getLength()==0){
                    Integer sum = 0;
                    for(Map.Entry<Character, List<String>> entry:asiaMap.entrySet()){
                        sum +=entry.getValue().size();
                    }
                    Integer online = asiaOnline.size();
                    result = (online)+";"+(sum-online)+";";
                }else{
                    String str = requestData.substring(0,request.getLength());
                    List<String> list;
                    if(!asiaMap.containsKey(str.charAt(0))){
                        list = new ArrayList();list.add(str);
                        asiaMap.put(str.charAt(0),list);
                        result = "success";
                    }else{
                        list = asiaMap.get(str.charAt(0));
                        boolean flag = true;
                        for(String s:list){
                            if(s.split("\\|")[0].equals(str.split("\\|")[0])){
                                flag = false;
                            }
                        }
                        if(flag){
                            list.add(str);
                            asiaMap.put(str.charAt(0),list);
                            result = "success";
                        }else{
                            result = "fail";
                        }
                    }
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
     * load players information from accounts
     * @throws IOException
     */
    public static void load() throws IOException {
        FileReader fr = new FileReader("src/replica2/as/asAccounts.txt");
        BufferedReader br = new BufferedReader(fr);
        String str;
        while((str=br.readLine())!=null){
            char c = str.charAt(0);
            if(asiaMap.get(c)==null){
                List<String> list = new ArrayList<>();
                list.add(str);
                asiaMap.put(c,list);
            }else{
                List<String> list = asiaMap.get(c);
                list.add(str);
                asiaMap.put(c,list);
            }
        }
        br.close();
        fr.close();
    }
}
