package ch.rweiss.jmx.client;

import java.io.IOException;

public class ListOperationsExample
{
  public static void main(String[] args) throws IOException
  {
    try(JmxClient client = JmxClient.connectToLocal())
    {
      MBean bean = client.bean(MBeanName.THREAD);
      for (MOperation operation : bean.operations())
      {
        System.out.println(operation.signature());
      }
    }
  }
}
