package ch.weiss.jmx.client;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.management.MBeanInfo;
import javax.management.ObjectInstance;

import ch.weiss.check.Check;

public class MBean
{
  private final JmxClient jmxClient;
  private final ObjectInstance objectInstance;

  MBean(JmxClient jmxClient, ObjectInstance objectInstance)
  {
    this.jmxClient = jmxClient;
    this.objectInstance = objectInstance;
  }
    
  public MBeanName name()
  {
    return MBeanName.createFor(objectInstance.getObjectName());
  }
  
  public String description()
  {
    return getMBeanInfo().getDescription();
  }

  public String type()
  {
    return new VmTypeConverter(objectInstance.getClassName()).toDisplayName();
  }
    
  public List<MAttribute> attributes()
  {
    return Arrays.asList(getMBeanInfo().getAttributes())
      .stream()
      .map(attribute -> new MAttribute(jmxClient, objectInstance, attribute))
      .collect(Collectors.toList());
  }
  
  public MAttribute attribute(String name)
  {
    Check.parameter("name").withValue(name).isNotBlank();
    
    return attributes()
       .stream()
       .filter(attribute -> attribute.name().equals(name))
       .findAny()
       .orElse(null);
  }

  
  public List<MOperation> operations()
  {
    return Arrays.asList(getMBeanInfo().getOperations())
        .stream()
        .map(operation -> new MOperation(jmxClient, objectInstance, operation))
        .collect(Collectors.toList());
  }
  
  public List<MOperation> operations(String name)
  {
    Check.parameter("name").withValue(name).isNotBlank();

    return operations()
        .stream()
        .filter(operation -> operation.name().equals(name))
        .collect(Collectors.toList());
  }
  
  public MOperation operation(String name)
  {
    Check.parameter("name").withValue(name).isNotBlank();

    return operations(name)
        .stream()
        .findAny()
        .orElse(null);
  }
  
  public MOperation operation(String name, String... parameterTypes)
  {
    Check.parameter("name").withValue(name).isNotBlank();

    return operations(name)
        .stream()
        .filter(operation -> operation.hasParamterTypes(parameterTypes))
        .findAny()
        .orElse(null);
  }
  
  private MBeanInfo getMBeanInfo()
  {
    try
    {
      return jmxClient.mBeanServerConnection().getMBeanInfo(objectInstance.getObjectName());
    }
    catch(Exception ex)
    {
      throw new JmxException("Cannot read bean info for "+objectInstance.getObjectName(), ex);
    }
  }
  
  @Override
  public String toString()
  {
    return "MBean["+objectInstance.getObjectName()+"]";
  }
}
