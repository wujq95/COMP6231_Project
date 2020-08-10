package client;

import frontend.FrontEndModule.FrontEnd;
import frontend.FrontEndModule.FrontEndHelper;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import java.util.Properties;

class MultipleTestCases {

    /**
     * This class is for testing concurrency and atomicity. There are 4 test cases, and the program will rest for one
     * second between each two test cases. The first test case can start 20 threads, and every thread will transfer an account
     * from the europe server to the asia server. The right result is that only one thread can finish this process, and other
     * 19 threads will fail to transfer the account. The second and third test cases are similar to the first test case, but
     * the second one will start 20 threads to suspend for the same account, and the third one will start 10 threads to transfer
     * and 10 threads to suspend. The last one is to confirm the atomicity. Because the account has been transferred, the original
     * sever can not let this account sign in, and the new server can let it sign in. If this result happens, the result and
     * explanation will be shown.
     *
     * Attention: Because this program uses the same data as the main program, you must restart three servers before running this
     * this program, or unexpected result may appear.
     * @param args
     */
    public static void main(String[] args) {
        try {
            Properties props = new Properties();
            props.put("org.omg.CORBA.ORBInitialPort", "1050");
            props.put("org.omg.CORBA.ORBInitialHost", "localhost");
            ORB orb = ORB.init(args, props);
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
            FrontEnd obj = FrontEndHelper.narrow(ncRef.resolve_str("FrontEnd"));

            System.out.println("test1: right result will only have one success reply");
            test1(obj);
            Thread.sleep(1000);
            System.out.println();
            System.out.println("test2: right result will only have one success reply");
            test2(obj);
            Thread.sleep(1000);
            System.out.println();
            System.out.println("test3: right result will only have one success reply");
            test3(obj);
            Thread.sleep(1000);
            System.out.println();
            System.out.println("atomicity maintenance check:");
            atomicTest(obj);
        } catch (Exception e) {
            System.out.println("Client ORB init exception: " + e);
            e.printStackTrace();
        }
    }

    /**
     * a test case to start 20 threads to transfer the same data
     * @param obj
     * @throws Exception
     */
    public static void test1(FrontEnd obj){
        for(int i=0;i<20;i++){

            try{
                new Thread(new Runnable(){
                    public void run() {
                        String reply = obj.transferAccount("guanyu","guanyu","93.222.222.222","182.222.222.222");
                        if(reply.startsWith("successfully")){
                            System.out.println("success");
                        }else{
                            System.out.println("not success");
                        }
                    }
                }).start();
            }catch (Exception e){
                System.out.println("Thread Exception" + e.getMessage());
            }
        }
    }

    /**
     * a test case to start 20 threads to suspend the same data
     * @param obj
     */
    public static void test2(FrontEnd obj){
        for(int i=0;i<20;i++){
            try{
                new Thread(new Runnable(){
                    public void run() {
                        String reply = obj.suspendAccount("Admin","Admin","132.222.222.222","ming11");
                        if(reply.startsWith("successfully")){
                            System.out.println("success");
                        }else{
                            System.out.println("not success");
                        }
                    }
                }).start();
            }catch (Exception e){
                System.out.println("Thread Exception" + e.getMessage());
            }
        }
    }

    /**
     * a test case to start 10 threads to transfer and 10 threads to suspend the same data
     * @param obj
     * @throws Exception
     */
    public static void test3(FrontEnd obj){
        for(int i=0;i<20;i++){
            try{
                int finalI = i;
                new Thread(new Runnable(){
                    public void run() {
                        String reply;
                        if(finalI <10){
                            reply = obj.transferAccount("ming88","ming88","182.222.222.222","93.222.222.222");
                            if(reply.startsWith("successfully")){
                                System.out.println("success");
                            }else{
                                System.out.println("not success");
                            }
                        }else{
                            reply = obj.suspendAccount("Admin","Admin","182.222.222.222","ming88");
                            if(reply.startsWith("successfully")){
                                System.out.println("success");
                            }else{
                                System.out.println("not success");
                            }
                        }
                    }
                }).start();
            }catch (Exception e){
                System.out.println("Thread Exception" + e.getMessage());
            }
        }
    }

    /**
     * a test case to confirm the atomicity of the operations
     * @param obj
     * @throws Exception
     */
    public static void atomicTest(FrontEnd obj) throws Exception{
        try{
            new Thread(new Runnable(){
                public void run() {
                    String reply1 = obj.playerSignIn("guanyu","guanyu","93.222.222.222");
                    String reply2 = obj.playerSignIn("guanyu","guanyu","182.222.222.222");
                    if(reply1.trim().equals("user name does not exist")&&(reply2.trim().equals("user login successful"))){
                        System.out.println("europe server can sign in the account and asia server can not find the account, ");
                        System.out.println("so the account has been transferred from asia server to europe server, ");
                        System.out.println("the atomicity is maintained and no data is lost");
                    }else{
                        System.out.println("not success");
                    }
                }
            }).start();
        }catch (Exception e){
            System.out.println("Thread Exception" + e.getMessage());
        }
    }
}
