module ch.rweiss.jmx.client
{
  exports ch.rweiss.jmx.client;
  requires transitive java.management;
  requires jdk.attach;
  requires ch.rweiss.check;
  requires org.apache.commons.lang3;
}