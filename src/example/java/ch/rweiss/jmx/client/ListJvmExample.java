package ch.rweiss.jmx.client;

public class ListJvmExample
{
  public static void main(String[] args)
  {
    for (Jvm jvm : Jvm.getAvailableRunningJvms())
    {
      System.out.print(jvm.id());
      System.out.print(": ");
      System.out.println(jvm.displayName());
    }
  }
}
