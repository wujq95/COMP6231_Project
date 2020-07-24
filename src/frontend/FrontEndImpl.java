package frontend;

import frontend.DPSSModule.DPSSPOA;
import org.omg.CORBA.ORB;

public class FrontEndImpl extends DPSSPOA {

    private ORB orb;

    public FrontEndImpl(){

    }

    public void setORB(ORB orb_val) {
        orb = orb_val;
    }

    @Override
    public String createPlayerAccount(String firstName, String lastName, String age, String userName, String password, String ipAddress) {
        return null;
    }

    @Override
    public String playerSignIn(String userName, String password, String ipAddress) {
        return null;
    }

    @Override
    public String playerSignOut(String userName, String ipAddress) {
        return null;
    }

    @Override
    public String getPlayerStatus(String adminUsername, String adminPassword, String ipAddress) {
        return null;
    }

    @Override
    public String transferAccount(String userName, String password, String oldIPAddress, String newIPAddress) {
        return null;
    }

    @Override
    public String suspendAccount(String adminUsername, String adminPassword, String ipAddress, String usernameToSuspend) {
        return null;
    }

    @Override
    public void shutdown() {

    }
}
