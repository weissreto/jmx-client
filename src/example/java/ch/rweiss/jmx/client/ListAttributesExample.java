package ch.rweiss.jmx.client;

import java.io.IOException;

public class ListAttributesExample
{
  public static void main(String[] args) throws IOException
  {
    try(JmxClient client = JmxClient.connectToLocal())
    {
      MBean bean = client.bean(MBeanName.RUNTIME);
      for (MAttribute attribute : bean.attributes())
      {
        System.out.print(attribute.name());
        System.out.print(": ");
        System.out.println(attribute.value());
      }
    }
  }
}
