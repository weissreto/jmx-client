package ch.rweiss.jmx.client;

import java.io.IOException;

public class InvokeOperationExample
{
  public static void main(String[] args) throws IOException
  {
    try (JmxClient client = JmxClient.connectToLocal())
    {
      MBean bean = client.bean(MBeanName.MEMORY);
      MOperation operation = bean.operation("gc");
      operation.invoke();
    }
  }
}
