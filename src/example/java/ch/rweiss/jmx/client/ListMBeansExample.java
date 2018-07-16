package ch.rweiss.jmx.client;

import java.io.IOException;

public class ListMBeansExample
{
  public static void main(String[] args) throws IOException
  {
    try(JmxClient client = JmxClient.connectToLocal())
    {
      for (MBean mBean : client.allBeans())
      {
        System.out.println(mBean.name().fullQualifiedName());
      }
    }
  }
}
