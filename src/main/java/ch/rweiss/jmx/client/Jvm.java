package ch.rweiss.jmx.client;

import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

@SuppressWarnings("restriction")
public class Jvm
{
  private final VirtualMachineDescriptor vmDescriptor;

  private Jvm(VirtualMachineDescriptor vmDescriptor)
  {
    this.vmDescriptor = vmDescriptor;
  }
  
  public String id()
  {
    return vmDescriptor.id();
  }
  
  public String displayName()
  {
    return StringUtils.defaultString(vmDescriptor.displayName());
  }
  
  public JmxClient connect() 
  {
    if (isLocalJvm())
    {
      return JmxClient.connectToLocal();
    }
    return attachAndConnect();
  }

  private boolean isLocalJvm()
  {
    String name = ManagementFactory.getRuntimeMXBean().getName();
    String id = StringUtils.substringBefore(name, "@");
    return id().equals(id);
  }

  private JmxClient attachAndConnect()
  {
    try
    {
      VirtualMachine vm = VirtualMachine.attach(vmDescriptor);
      String jmxServiceUri = vm.startLocalManagementAgent();
      return JmxClient.connectTo(jmxServiceUri);
    }
    catch(Exception ex)
    {
      throw new JmxException("Cannot connect jmx client to jvm "+id(), ex);
    }
  }
  
  public static List<Jvm> getAvailableRunningJvms()
  {
    return VirtualMachine
      .list()
      .stream()
      .map(vmDescriptor -> new Jvm(vmDescriptor))
      .collect(Collectors.toList());
  }

  public static Jvm runningJvm(String idOrPartOfTheMainClassName)
  {
    if (StringUtils.isBlank(idOrPartOfTheMainClassName))
    {
      return getAvailableRunningJvms()
          .stream()
          .filter(jvm -> !jvm.isLocalJvm())
          .findAny()
          .orElse(null);
    }
    return getAvailableRunningJvms()
      .stream()
      .filter(jvm -> idOrPartOfTheMainClassName.equals(jvm.id()))
      .findAny()
      .orElseGet(
          () -> getAvailableRunningJvms()
          .stream()
          .filter(jvm -> jvm.mainClassName().contains(idOrPartOfTheMainClassName))
          .findAny()
          .orElse(null));
  }

  private String mainClassName()
  {    
    StringBuilder mainClassName = new StringBuilder();
    for (int pos = 0; pos < displayName().length(); pos++)
    {
      char ch = displayName().charAt(pos);
      if (Character.isWhitespace(ch))
      {
        break;
      }
      mainClassName.append(ch);
    }
    return mainClassName.toString();
  }
}
