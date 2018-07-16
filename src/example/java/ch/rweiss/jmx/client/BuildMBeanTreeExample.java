package ch.rweiss.jmx.client;

import java.io.IOException;
import java.util.stream.IntStream;

public class BuildMBeanTreeExample
{
  public static void main(String[] args) throws IOException
  {
    try(JmxClient client = JmxClient.connectToLocal())
    {
      MBeanTreeNode rootNode = client.beanTree();
      printChildNodes(0, rootNode);
    }
  }

  private static void printChildNodes(int intend, MBeanTreeNode rootNode)
  {
    for (MBeanTreeNode child : rootNode.children())
    {
      IntStream.range(0, intend).forEach(pos -> System.out.print(' '));
      System.out.println(child.name().simpleName());
      printChildNodes(intend+2, child);
    }    
  }
}
