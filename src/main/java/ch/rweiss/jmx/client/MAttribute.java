package ch.rweiss.jmx.client;

import javax.management.Attribute;
import javax.management.MBeanAttributeInfo;
import javax.management.ObjectInstance;
import javax.management.openmbean.OpenMBeanAttributeInfo;
import javax.management.openmbean.OpenType;

public class MAttribute
{
  private final JmxClient jmxClient;
  private final ObjectInstance objectInstance;
  private final MBeanAttributeInfo attribute;

  MAttribute(JmxClient jmxClient, ObjectInstance objectInstance, MBeanAttributeInfo attribute)
  {
    this.jmxClient = jmxClient;
    this.objectInstance = objectInstance;
    this.attribute = attribute;
  }
  
  public String name()
  {
   return attribute.getName();
  }
  
  public String description()
  {
    return attribute.getDescription();
  }
  
  public String type()
  {
    return new VmTypeConverter(attribute.getType()).toDisplayName();
  }
  
  public OpenType<?> openType()
  {
    if (attribute instanceof OpenMBeanAttributeInfo)
    {
      return ((OpenMBeanAttributeInfo)attribute).getOpenType();
    }
    return null;
  }
  
  public boolean isReadable()
  {
    return attribute.isReadable();
  }
  
  public boolean isWritable()
  {
    return attribute.isWritable();
  }
  
  public String valueAsString() 
  {
    Object value = value();
    return new JmxValueConverter(value).toString();
  }

  public Object value()
  {
    try
    {
      return jmxClient.mBeanServerConnection().getAttribute(objectInstance.getObjectName(), name());
    }
    catch(Exception ex)
    {
      throw new JmxException("Cannot read value of attribute "+name(), ex);
    }
  }

  public void value(String valueAsString)
  {
    Object value = new JmxStringConverter(valueAsString).toType(type());
    value(value);
  }

  public void value(Object value)
  {
    try
    {
      jmxClient.mBeanServerConnection().setAttribute(
        objectInstance.getObjectName(), 
        new Attribute(name(), value));
    }
    catch(Exception ex)
    {
      throw new JmxException("Cannot write value of attribute "+name(), ex);
    }
  }
}
