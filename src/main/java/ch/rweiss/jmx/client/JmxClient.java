package ch.rweiss.jmx.client;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import ch.rweiss.check.Check;


/**
 * @author Reto Weiss
 * @since 0.1
 *
 */
public class JmxClient implements AutoCloseable
{
  private final JMXConnector connector;
  private final MBeanServerConnection mBeanServerConnection;

  private JmxClient(JMXConnector connector) throws IOException
  {
    this.connector = connector;
    this.mBeanServerConnection = connector.getMBeanServerConnection();
  }
  
  private JmxClient(MBeanServer mBeanServer)
  {
    this.connector = null;
    this.mBeanServerConnection = mBeanServer;
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

    String serviceUrl = toServiceUrl(hostName, port);
    return connectTo(serviceUrl);
  }
  
  public static JmxClient connectTo(String hostName, int port, String userName, String password)
  {
    Check.parameter("hostName").withValue(hostName).isNotBlank();
    Check.parameter("port").withValue(port).isInRange(0, 65535);
    Check.parameter("userName").withValue(userName).isNotNull();
    Check.parameter("password").withValue(password).isNotNull();
    
    Map<String,Object> environment = new HashMap<>();
    environment.put(JMXConnector.CREDENTIALS, new String[] {userName, password});
    
    String serviceUrl = toServiceUrl(hostName, port); 
    return connectTo(serviceUrl, environment);
  }

  private static String toServiceUrl(String hostName, int port)
  {
    return "service:jmx:rmi:///jndi/rmi://"+hostName+":"+port+"/jmxrmi";
  }

  public static JmxClient connectToLocal()
  {
    return new JmxClient(ManagementFactory.getPlatformMBeanServer());
  }

  public static JmxClient connectTo(String jmxServiceUrl)
  {
    return connectTo(jmxServiceUrl, null);
  }

  @SuppressWarnings("resource")
  public static JmxClient connectTo(String jmxServiceUrl, Map<String,?> environment)
  {
    Check.parameter("jmxServiceUrl").withValue(jmxServiceUrl).isNotBlank();
    try
    {
      JMXConnector connector = JMXConnectorFactory.connect(new JMXServiceURL(jmxServiceUrl), environment);
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
  public void close() throws JmxException
  {
    if (connector != null)
    {
      try
      {
        connector.close();
      }
      catch(IOException ex)
      {
        throw new JmxException("Cannot close jmx client", ex);
      }
    }
  }
}
