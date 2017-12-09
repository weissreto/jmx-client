package ch.weiss.jmx.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ch.weiss.check.Check;

public class MBeanName implements Comparable<MBeanName>
{
  public static final MBeanName EMPTY = new MBeanName();
  private final String domain;
  private final List<Property> properties;
  
  private MBeanName()
  {
    this("", Collections.emptyList());
  }
  
  private MBeanName(String domain, List<Property> properties)
  {
    this.domain = domain;
    this.properties = properties;
  }
  
  public static MBeanName createFor(String name)
  {
    Check.parameter("name").withValue(name).isNotBlank();

    try
    {
      return createFor(new ObjectName(name));
    }
    catch (MalformedObjectNameException ex)
    {
      throw new JmxException(name+" is not a valid MBean name", ex);
    }
  }

  static MBeanName createFor(ObjectName objectName)
  {
    Check.parameter("objectName").withValue(objectName).isNotNull();

    List<Property> properties = new ArrayList<>();
    String keyPropertyList = objectName.getKeyPropertyListString();
    for(String property : keyPropertyList.split(","))
    {
      String key = StringUtils.substringBefore(property, "=");
      String value = StringUtils.substringAfter(property, "=");
      properties.add(new Property(key, value));
    }
    return new MBeanName(objectName.getDomain(), properties);
  }
  
  public String fullQualifiedName()
  {
    return toString();
  }
  
  public String simpleName()
  {
    if (this == EMPTY)
    {
      return "";
    }
    List<String> valueParts = valueParts();
    return valueParts.get(valueParts.size()-1);
  }
  
  public String lastKey()
  {
    if (this == EMPTY)
    {
      return "";
    }
    List<String> keyParts = keyParts();
    return keyParts.get(keyParts.size()-1);
  }
  
  public int countParts()
  {
    if (this == EMPTY)
    {
      return 0;
    }
    return valueParts().size();
  }
  
  public List<String> valueParts()
  {
    if (this == EMPTY)
    {
      return Collections.emptyList();
    }
    List<String> valueParts = new ArrayList<>();
    valueParts.add(domain);
    properties.forEach(property -> valueParts.add(property.value));
    return valueParts;
  }
  
  public List<String> keyParts()
  {
    if (this == EMPTY)
    {
      return Collections.emptyList();
    }
    List<String> keyParts = new ArrayList<>();
    keyParts.add("domain");
    properties.forEach(property -> keyParts.add(property.key));
    return keyParts;
  }

  public MBeanName suffix(int parts)
  {
    if (parts <= 0)
    {
      return EMPTY;
    }
    if (parts == 1)
    {
      return new MBeanName(domain, Collections.emptyList());
    }
    if (parts <= properties.size())
    {
      return new MBeanName(domain, properties.subList(0,  parts-1));
    }
    return new MBeanName(domain, properties);
  }
  
  @Override
  public String toString()
  {
    if (properties.isEmpty())
    {
      return domain;
    }
    StringBuilder builder = new StringBuilder();
    builder.append(domain);
    builder.append(":");
    
    builder.append(
        properties
          .stream()
          .map(property -> property.toString())
          .collect(Collectors.joining(",")));
    return builder.toString();
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    if (obj.getClass() != MBeanName.class)
    {
      return false;
    }
    MBeanName other = (MBeanName) obj;
    return new EqualsBuilder().append(domain, other.domain).append(properties, other.properties).isEquals();
  }
  
  @Override
  public int hashCode()
  {
    return new HashCodeBuilder().append(domain).append(properties).toHashCode();
  }

  @Override
  public int compareTo(MBeanName other)
  {
    return fullQualifiedName().compareTo(other.fullQualifiedName());
  }

  private static class Property
  {
    private String key;
    private String value;
    private Property(String key, String value)
    {
      this.key = key;
      this.value = value;
    }
    
    @Override
    public String toString()
    {
      return key+"="+value;
    }

    @Override
    public boolean equals(Object obj)
    {
      if (this == obj)
      {
        return true;
      }
      if (obj == null)
      {
        return false;
      }
      if (obj.getClass() != Property.class)
      {
        return false;
      }
      Property other = (Property) obj;
      return new EqualsBuilder().append(key, other.key).append(value, other.value).isEquals();
    }
    
    @Override
    public int hashCode()
    {
      return new HashCodeBuilder().append(key).append(value).toHashCode();
    }

  }
}
