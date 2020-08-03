package frontend;

import config.PortConfig;
import frontend.FrontEndModule.FrontEnd;
import frontend.FrontEndModule.FrontEndHelper;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import java.util.Properties;

public class FrontEndServer {

    public static void main(String[] args) {

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
            FrontEnd href = FrontEndHelper.narrow(ref);
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
            System.out.println("Front End Server is ready and listening ...");

            // wait for invocations from clients
            orb.run();
        }catch (Exception e) {
            System.err.println("ERROR: " + e);
            e.printStackTrace(System.out);
        }

        System.out.println("Front Server Server Exiting ...");
    }
}
