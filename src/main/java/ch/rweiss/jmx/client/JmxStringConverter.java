package ch.rweiss.jmx.client;

import java.lang.reflect.Array;

import org.apache.commons.lang3.StringUtils;

public class JmxStringConverter
{
  private String value;

  public JmxStringConverter(String value)
  {
    this.value = value;
  }

  public Object toType(String type)
  {
    if (Boolean.class.getName().equals(type)||
        Boolean.TYPE.getName().equals(type))
    {
      return Boolean.parseBoolean(value);
    }
    if (Character.class.getName().equals(type)||
        Character.TYPE.getName().equals(type))
    {
      return value.charAt(0);
    }
    if (Byte.class.getName().equals(type)||
        Byte.TYPE.getName().equals(type))
    {
      return Byte.parseByte(value);
    }
    if (Short.class.getName().equals(type)||
        Short.TYPE.getName().equals(type))
    {
      return Short.parseShort(value);
    }
    if (Integer.class.getName().equals(type)||
        Integer.TYPE.getName().equals(type))
    {
      return Integer.parseInt(value);
    }
    if (Long.class.getName().equals(type)||
        Long.TYPE.getName().equals(type))
    {
      return Long.parseLong(value);
    }
    if (Float.class.getName().equals(type)||
        Float.TYPE.getName().equals(type))
    {
      return Float.parseFloat(value);
    }
    if (Double.class.getName().equals(type)||
        Double.TYPE.getName().equals(type))
    {
      return Double.parseDouble(value);
    }
    if (StringUtils.endsWith(type, "[]"))
    {
      return fromArray(type);
    }
    return value;
  }

  private Object fromArray(String type)
  {
    if (StringUtils.startsWith(value, "[") && StringUtils.endsWith(value, "]"))
    {
      value = StringUtils.substringBetween(value, "[", "]");
    }
    String[] values = value.split(",");
    String cType = StringUtils.removeEnd(type, "[]");
    
    Class<?> componentType = toComponentType(cType);
    Object array = Array.newInstance(componentType, values.length);
    
    int pos = 0;
    for (String val : values)
    {
      Object item = new JmxStringConverter(val).toType(componentType.getName());
      Array.set(array, pos++, item);
    }
    return array;
  }
  
  private static Class<?> toComponentType(String type)
  {
    if (Boolean.TYPE.getName().equals(type))
    {
      return Boolean.TYPE;
    }
    if (Character.TYPE.getName().equals(type))
    {
      return Character.TYPE;
    }
    if (Byte.TYPE.getName().equals(type))
    {
      return Byte.TYPE;
    }
    if (Short.TYPE.getName().equals(type))
    {
      return Short.TYPE;
    }
    if (Integer.TYPE.getName().equals(type))
    {
      return Integer.TYPE;
    }
    if (Long.TYPE.getName().equals(type))
    {
      return Long.TYPE;
    }
    if (Float.TYPE.getName().equals(type))
    {
      return Float.TYPE;
    }
    if (Double.TYPE.getName().equals(type))
    {
      return Double.TYPE;
    }
    try
    {
      return Class.forName(type);
    }
    catch (ClassNotFoundException ex)
    {
      throw new JmxException("Could not found class "+type, ex); 
    }
  }
}
