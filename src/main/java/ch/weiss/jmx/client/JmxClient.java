package ch.weiss.jmx.client;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import ch.weiss.check.Check;


public class JmxClient implements AutoCloseable
{
  private final JMXConnector connector;
  private final MBeanServerConnection mBeanServerConnection;

  private JmxClient(JMXConnector connector) throws IOException
  {
    this.connector = connector;
    this.mBeanServerConnection = connector.getMBeanServerConnection();
  }
  
  public List<MBean> allBeans()
  {
    return beansThatMatch(MBeanFilter.EMPTY);
  }
  
  public List<MBean> beansThatMatch(MBeanFilter filter)
  {
    Check.parameter("filter").withValue(filter).isNotNull();
    try
    {
      return mBeanServerConnection()
          .queryMBeans(filter.nameWithWildcards(), null)
          .stream()
          .map(objectInstance -> new MBean(this, objectInstance))
          .collect(Collectors.toList());
    }
    catch(IOException ex)
    {
      throw new JmxException("Cannot get beans", ex);
    }    
  }
  
  public MBean bean(MBeanName name)
  {
    Check.parameter("name").withValue(name).isNotNull();
    return beansThatMatch(MBeanFilter.with(name))
        .stream()
        .findAny()
        .orElse(null);
  }

  
  public MBeanTreeNode beanTree() 
  {
    return new MBeanTreeNode(MBeanName.EMPTY, allBeans());
  }

  public static JmxClient connectTo(String hostName, int port)
  {
    Check.parameter("hostName").withValue(hostName).isNotBlank();
    Check.parameter("port").withValue(port).isInRange(0, 65535);

    return connectTo("service:jmx:rmi:///jndi/rmi://"+hostName+":"+port+"/jmxrmi");
  }
  
  @SuppressWarnings("resource")
  public static JmxClient connectTo(String jmxServiceUrl)
  {
    Check.parameter("jmxServiceUrl").withValue(jmxServiceUrl).isNotBlank();
    try
    {
      JMXConnector connector = JMXConnectorFactory.connect(new JMXServiceURL(jmxServiceUrl));
      return new JmxClient(connector);
    }
    catch(IOException ex)
    {
      throw new JmxException("Cannot connect to "+jmxServiceUrl, ex);
    }
  }

  MBeanServerConnection mBeanServerConnection()
  {
    return mBeanServerConnection;
  }

  @Override
  public void close() throws IOException
  {
    connector.close();
  }
}
