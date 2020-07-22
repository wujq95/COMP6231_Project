package frontend.DPSSModule;


/**
* DPSSModule/_DPSSStub.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from DPSS.idl
* Wednesday, July 22, 2020 1:26:44 o'clock PM EDT
*/

public class _DPSSStub extends org.omg.CORBA.portable.ObjectImpl implements DPSS
{

  public String createPlayerAccount (String firstName, String lastName, String age, String userName, String password, String ipAddress)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("createPlayerAccount", true);
                $out.write_string (firstName);
                $out.write_string (lastName);
                $out.write_string (age);
                $out.write_string (userName);
                $out.write_string (password);
                $out.write_string (ipAddress);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return createPlayerAccount (firstName, lastName, age, userName, password, ipAddress        );
            } finally {
                _releaseReply ($in);
            }
  } // createPlayerAccount

  public String playerSignIn (String userName, String password, String ipAddress)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("playerSignIn", true);
                $out.write_string (userName);
                $out.write_string (password);
                $out.write_string (ipAddress);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return playerSignIn (userName, password, ipAddress        );
            } finally {
                _releaseReply ($in);
            }
  } // playerSignIn

  public String playerSignOut (String userName, String ipAddress)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("playerSignOut", true);
                $out.write_string (userName);
                $out.write_string (ipAddress);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return playerSignOut (userName, ipAddress        );
            } finally {
                _releaseReply ($in);
            }
  } // playerSignOut

  public String getPlayerStatus (String adminUsername, String adminPassword, String ipAddress)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("getPlayerStatus", true);
                $out.write_string (adminUsername);
                $out.write_string (adminPassword);
                $out.write_string (ipAddress);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return getPlayerStatus (adminUsername, adminPassword, ipAddress        );
            } finally {
                _releaseReply ($in);
            }
  } // getPlayerStatus

  public String transferAccount (String userName, String password, String oldIPAddress, String newIPAddress)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("transferAccount", true);
                $out.write_string (userName);
                $out.write_string (password);
                $out.write_string (oldIPAddress);
                $out.write_string (newIPAddress);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return transferAccount (userName, password, oldIPAddress, newIPAddress        );
            } finally {
                _releaseReply ($in);
            }
  } // transferAccount

  public String suspendAccount (String adminUsername, String adminPassword, String ipAddress, String usernameToSuspend)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("suspendAccount", true);
                $out.write_string (adminUsername);
                $out.write_string (adminPassword);
                $out.write_string (ipAddress);
                $out.write_string (usernameToSuspend);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return suspendAccount (adminUsername, adminPassword, ipAddress, usernameToSuspend        );
            } finally {
                _releaseReply ($in);
            }
  } // suspendAccount

  public void shutdown ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("shutdown", false);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                shutdown (        );
            } finally {
                _releaseReply ($in);
            }
  } // shutdown

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:DPSSModule/DPSS:1.0"};

  public String[] _ids ()
  {
    return (String[])__ids.clone ();
  }

  private void readObject (java.io.ObjectInputStream s) throws java.io.IOException
  {
     String str = s.readUTF ();
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init (args, props);
   try {
     org.omg.CORBA.Object obj = orb.string_to_object (str);
     org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl) obj)._get_delegate ();
     _set_delegate (delegate);
   } finally {
     orb.destroy() ;
   }
  }

  private void writeObject (java.io.ObjectOutputStream s) throws java.io.IOException
  {
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init (args, props);
   try {
     String str = orb.object_to_string (this);
     s.writeUTF (str);
   } finally {
     orb.destroy() ;
   }
  }
} // class _DPSSStub