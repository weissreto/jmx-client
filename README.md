# jmx-cli [![Build Status](https://travis-ci.org/weissreto/jmx-client.svg?branch=master)](https://travis-ci.org/weissreto/jmx-client)

This library provides classes to easily:
* list all JVMs that are running on the local machine
* connect to a JVM via JMX (Java Management Extension)
* list all MBeans that a JVM provides
* build a tree of MBeans that a JVM provides 
* list all attributes and operations of a MBean
* set the value of an attribute
* invoke operations 

## List all JMVs
 
```java
for (Jvm jvm : Jvm.getAvailableRunningJvms())
{
  System.out.print(jvm.id());
  System.out.print(": ");
  System.out.println(jvm.displayName());
}
```

## Connect to a JVM via JMX

```java
Jvm jvm = Jvm.getAvailableRunningJvms().get(0);
JmxClient client = jvm.connect();
```

```java
JmxClient local = JmxClient.connectToLocal();
```

## List all MBeans

```java
try(JmxClient client = JmxClient.connectToLocal())
{
  for (MBean mBean : client.allBeans())
  {
    System.out.println(mBean.name().fullQualifiedName());
  }
}
```

## Build MBean Tree

```java
public static void main(String[] args) throws IOException
{
  try(JmxClient client = JmxClient.connectToLocal())
  {
    MBeanTreeNode rootNode = client.beanTree();
    printChildNodes(0, rootNode);
  }
}

private static void printChildNodes(int intend, MBeanTreeNode rootNode)
{
  for (MBeanTreeNode child : rootNode.children())
  {
    IntStream.range(0, intend).forEach(pos -> System.out.print(' '));
    System.out.println(child.name().simpleName());
    printChildNodes(intend+2, child);
  }    
}
```

## List Attributes

```java
try(JmxClient client = JmxClient.connectToLocal())
{
  MBean bean = client.bean(MBeanName.RUNTIME);
  for (MAttribute attribute : bean.attributes())
  {
    System.out.print(attribute.name());
    System.out.print(": ");
    System.out.println(attribute.value());  
  }
}
```

## List Operations

```java
try(JmxClient client = JmxClient.connectToLocal())
{
  MBean bean = client.bean(MBeanName.THREAD);
  for (MOperation operation : bean.operations())
  {
    System.out.println(operation.signature());
  }
}
```

## Set Attribute Value

```java
try (JmxClient client = JmxClient.connectToLocal())
{
  MBean bean = client.bean(MBeanName.THREAD);
  MAttribute attribute = bean.attribute("ThreadCpuTimeEnabled");
  attribute.value(false);
}
```

## Invoke Operation

```java
try (JmxClient client = JmxClient.connectToLocal())
{
  MBean bean = client.bean(MBeanName.MEMORY);
  MOperation operation = bean.operation("gc");
  operation.invoke();
}
```