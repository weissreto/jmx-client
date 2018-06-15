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
      return toDisplayName(vmType);
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
        return toArrayDisplayName(type);
      default:
        throw new IllegalStateException("Unknown type "+type);
    }
  }
  
  private static String toClassDisplayName(String vmType)
  {
    String vmRawType = StringUtils.removeStart(vmType, "L");
    vmRawType = StringUtils.substringBefore(vmRawType, ";");
    return vmRawType;
  }
  
  private static String toArrayDisplayName(String vmType)
  {
    String elementVmType = StringUtils.removeStart(vmType, "[");
    String elementType = toDisplayName(elementVmType);
    return elementType+"[]";
  }
}
