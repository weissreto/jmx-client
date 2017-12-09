package ch.weiss.jmx.client;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import ch.weiss.check.Check;

public class MBeanFilter
{
  public static final MBeanFilter EMPTY = new MBeanFilter(null);
  private final ObjectName nameWithWildcards;
  
  private MBeanFilter(ObjectName nameWithWildcards)
  {
    this.nameWithWildcards = nameWithWildcards;
  }
  
  ObjectName nameWithWildcards()
  {
    return nameWithWildcards;
  }
  
  public static MBeanFilter with(MBeanName name)
  {
    Check.parameter("name").withValue(name).isNotNull();

    return with(name.fullQualifiedName());
  }

  public static MBeanFilter with(ObjectName nameWithWildcards)
  {
    Check.parameter("nameWithWildcards").withValue(nameWithWildcards).isNotNull();

    return new MBeanFilter(nameWithWildcards);
  }
  
  public static MBeanFilter with(String nameWithWildcards)
  {
    Check.parameter("nameWithWildcards").withValue(nameWithWildcards).isNotBlank();
    
    try
    {
      return with(new ObjectName(nameWithWildcards));
    }
    catch (MalformedObjectNameException ex)
    {
      throw new JmxException("Given bean name "+nameWithWildcards+" is not valid", ex);
    }
  }
}
