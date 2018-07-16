package ch.rweiss.jmx.client;

public class ConnectExample
{
  @SuppressWarnings({ "resource", "unused" })
  public static void main(String[] args)
  {
    Jvm jvm = Jvm.getAvailableRunningJvms().get(0);
    JmxClient client = jvm.connect();
    
    JmxClient local = JmxClient.connectToLocal();
  }
}
