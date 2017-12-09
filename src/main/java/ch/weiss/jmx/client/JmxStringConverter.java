package ch.weiss.jmx.client;

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
    if (StringUtils.startsWith(type, "["))
    {
      return fromArray(type);
    }
    return value;
  }

  private Object fromArray(String type)
  {
    String cType = StringUtils.removeStart(type, "[");
    
    Class<?> componentType = toComponentType(cType);
    int length = StringUtils.countMatches(value, ",") + 1;
    Object array = Array.newInstance(componentType, length);
    
    String values = StringUtils.removeStart(value, "[");
    values = StringUtils.removeEnd(value, "]");
    int pos = 0;
    for (String val : values.split(","))
    {
      Object item = new JmxStringConverter(val).toType(componentType.getName());
      Array.set(array, pos, item);
    }
    return array;
  }
  
  private static Class<?> toComponentType(String vmType)
  {
    switch(vmType.charAt(0))
    {
      case 'Z':
        return Boolean.TYPE;
      case 'B':
        return Byte.TYPE;
      case 'C':
        return Character.TYPE;
      case 'S':
        return Short.TYPE;        
      case 'I':
        return Integer.TYPE;
      case 'J':        
        return Long.TYPE;
      case 'F':
        return Float.TYPE;
      case 'D':
        return Double.TYPE;
      case 'L':
        return toClass(vmType);
      case '[':
        return toArrayClass(vmType);
      default:
        throw new IllegalStateException("Unknown type "+vmType);
    }
  }
  
  private static Class<?> toClass(String vmType)
  {
    try
    {
      String vmRawType = StringUtils.removeStart(vmType, "L");
      vmRawType = StringUtils.substringBefore(vmRawType, ";");
      return Class.forName(vmRawType);
    }
    catch (ClassNotFoundException ex)
    {
      throw new JmxException("Could not found class "+vmType, ex); 
    }
  }

  private static Class<?> toArrayClass(String vmType)
  {
    String vmCType = StringUtils.removeStart(vmType, "[");
    Class<?> componentType = toComponentType(vmCType);
    return Array.newInstance(componentType, 0).getClass(); 
  }  
}
