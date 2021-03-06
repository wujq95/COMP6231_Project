package replica1.DPSSModule;


/**
* DPSSModule/DPSSOperations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from DPSS.idl
* Wednesday, July 22, 2020 1:26:44 o'clock PM EDT
*/

public interface DPSSOperations 
{
  String createPlayerAccount(String firstName, String lastName, String age, String userName, String password, String ipAddress);
  String playerSignIn(String userName, String password, String ipAddress);
  String playerSignOut(String userName, String ipAddress);
  String getPlayerStatus(String adminUsername, String adminPassword, String ipAddress);
  String transferAccount(String userName, String password, String oldIPAddress, String newIPAddress);
  String suspendAccount(String adminUsername, String adminPassword, String ipAddress, String usernameToSuspend);
  void shutdown();
} // interface DPSSOperations
