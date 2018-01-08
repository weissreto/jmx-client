package ch.rweiss.jmx.client;

import javax.management.MBeanParameterInfo;

public class MParameter
{
  private MBeanParameterInfo info;

  MParameter(MBeanParameterInfo info)
  {
    this.info = info;
  }
  
  public String name()
  {
    return info.getName();
  }
  
  public String type()
  {
    return new VmTypeConverter(vmType()).toDisplayName();
  }
  
  String vmType()
  {
    return info.getType();
  }
  
  public String description()
  {
    return info.getDescription();
  }  
}