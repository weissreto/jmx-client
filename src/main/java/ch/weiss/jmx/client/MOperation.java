package ch.weiss.jmx.client;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.management.MBeanOperationInfo;
import javax.management.ObjectInstance;

public class MOperation
{
  private JmxClient jmxClient;
  private ObjectInstance objectInstance;
  private MBeanOperationInfo operation;

  public MOperation(JmxClient jmxClient, ObjectInstance objectInstance, MBeanOperationInfo operation)
  {
    this.jmxClient = jmxClient;
    this.objectInstance = objectInstance;
    this.operation = operation;
  }
  
  public String name()
  {
    return operation.getName();
  }
  
  public String description()
  {
    return operation.getDescription();
  }
  
  public MImpact impact()
  {
    return MImpact.forValue(operation.getImpact());
  }
  
  public String signature()
  {
    return toString();
  }
  
  public String returnType()
  {
    return new VmTypeConverter(operation.getReturnType()).toDisplayName();
  }
  
  public List<MParameter> parameters()
  {
    return Arrays.asList(operation.getSignature())
        .stream()
        .map(info -> new MParameter(info))
        .collect(Collectors.toList());
  }
  
  public Object invoke(Object... arguments)
  {
    try
    {
      return jmxClient.mBeanServerConnection().invoke(
          objectInstance.getObjectName(), 
          name(), 
          arguments, 
          vmParameterTypes());
    }
    catch (Exception ex)
    {
      throw new JmxException("Could not invoke method "+name(), ex);
    }
  }
  
  public String invoke(String... arguments)
  {
    String[] signature = parameterTypes();
    if (signature.length != arguments.length)
    {
      throw new JmxException("Wrong number of arguments given for operation "+name()+". Expected "+signature.length+" but was "+arguments.length);
    }

    Object[] convertedArguments = convertArguments(arguments, signature);
    Object result = invoke(convertedArguments);
    return new JmxValueConverter(result).toString();
  }

  private static Object[] convertArguments(String[] arguments, String[] signature)
  {
    Object[] convertedArguments = new Object[arguments.length];
    for (int pos = 0; pos < arguments.length; pos++)
    {
      convertedArguments[pos] = new JmxStringConverter(arguments[pos]).toType(signature[pos]);
    }
    return null;
  }
  
  private String[] parameterTypes()
  {
    return parameters()
        .stream()
        .map(param -> param.type())
        .toArray(String[]::new);
  }
  
  private String[] vmParameterTypes()
  {
    return parameters()
        .stream()
        .map(param -> param.vmType())
        .toArray(String[]::new);
  }
  
  public boolean hasParamterTypes(String[] parameterTypes)
  {
    return Arrays.equals(parameterTypes(), parameterTypes);
  }
  
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append(returnType());
    builder.append(" ");
    builder.append(name());
    builder.append("(");
    builder.append(
        parameters()
          .stream()
          .map(param -> param.type()+" "+param.name())
          .collect(Collectors.joining(", ")));
    builder.append(")");
    return builder.toString(); 
  }
}
