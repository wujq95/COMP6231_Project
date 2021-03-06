package replica3.DPSSModule;


/**
* DPSSModule/DPSSHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from DPSS.idl
* Wednesday, July 22, 2020 1:26:44 o'clock PM EDT
*/

abstract public class DPSSHelper
{
  private static String  _id = "IDL:DPSSModule/DPSS:1.0";

  public static void insert (org.omg.CORBA.Any a, DPSS that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static DPSS extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.CORBA.ORB.init ().create_interface_tc (DPSSHelper.id (), "DPSS");
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static DPSS read (org.omg.CORBA.portable.InputStream istream)
  {
    return narrow (istream.read_Object (_DPSSStub.class));
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, DPSS value)
  {
    ostream.write_Object ((org.omg.CORBA.Object) value);
  }

  public static DPSS narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof DPSS)
      return (DPSS)obj;
    else if (!obj._is_a (id ()))
      throw new org.omg.CORBA.BAD_PARAM ();
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      _DPSSStub stub = new _DPSSStub();
      stub._set_delegate(delegate);
      return stub;
    }
  }

  public static DPSS unchecked_narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof DPSS)
      return (DPSS)obj;
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      _DPSSStub stub = new _DPSSStub();
      stub._set_delegate(delegate);
      return stub;
    }
  }

}
