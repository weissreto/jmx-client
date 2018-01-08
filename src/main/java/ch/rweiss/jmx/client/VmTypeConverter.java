package ch.rweiss.jmx.client;

import org.apache.commons.lang3.StringUtils;

public class VmTypeConverter
{
  private String vmType;

  public VmTypeConverter(String vmType)
  {
    this.vmType = vmType;
  }

  public String toDisplayName()
  {
    if (StringUtils.startsWith(vmType, "["))
    {
      String type = toDisplayName(StringUtils.removeStart(vmType, "["));
      return type+"[]";
    }
    return vmType;
  }

  private static String toDisplayName(String type)
  {
    switch(type.charAt(0))
    {
      case 'Z':
        return Boolean.TYPE.getName();
      case 'B':
        return Byte.TYPE.getName();
      case 'C':
        return Character.TYPE.getName();
      case 'S':
        return Short.TYPE.getName();        
      case 'I':
        return Integer.TYPE.getName();
      case 'J':        
        return Long.TYPE.getName();
      case 'F':
        return Float.TYPE.getName();
      case 'D':
        return Double.TYPE.getName();
      case 'L':
        return toClassDisplayName(type);
      case '[':
        return toDisplayName(type);
      default:
        throw new IllegalStateException();
    }
  }
  
  private static String toClassDisplayName(String vmType)
  {
    String vmRawType = StringUtils.removeStart(vmType, "L");
    vmRawType = StringUtils.substringBefore(vmRawType, ";");
    return vmRawType;
  }
}
