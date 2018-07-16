package ch.rweiss.jmx.client;

import java.io.IOException;

public class SetAttributeExample
{
  public static void main(String[] args) throws IOException
  {
    try (JmxClient client = JmxClient.connectToLocal())
    {
      MBean bean = client.bean(MBeanName.THREAD);
      MAttribute attribute = bean.attribute("ThreadCpuTimeEnabled");
      attribute.value(false);
    }
  }
}
