package ch.weiss.jmx.client;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import ch.weiss.check.Check;

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

  public static Jvm runningJvm(String idOrPartOfTheDisplayName)
  {
    Check.parameter("idOrDisplayName").withValue(idOrPartOfTheDisplayName).isNotBlank();
    return getAvailableRunningJvms()
        .stream()
        .filter(jvm -> idOrPartOfTheDisplayName.equals(jvm.id()))
        .findAny()
        .orElseGet(
            () -> getAvailableRunningJvms()
            .stream()
            .filter(jvm -> jvm.displayName().contains(idOrPartOfTheDisplayName))
            .findAny()
            .orElse(null));
  }
}
