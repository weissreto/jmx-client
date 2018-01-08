package ch.rweiss.jmx.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MBeanTreeNode implements Comparable<MBeanTreeNode>
{
  private final MBeanName name;
  private final List<MBeanTreeNode> children;
  private final MBean bean;

  MBeanTreeNode(MBeanName name, List<MBean> allChildren)
  {
    this.name = name;
    Map<MBeanName, List<MBean>> childrenMap = new HashMap<>();
    MBean mySelf = null;
    for (MBean child : allChildren)
    {
      MBeanName fullChildName = child.name();
      if (fullChildName.equals(name))
      {
        mySelf = child;
      }
      else
      {
        MBeanName childName = fullChildName.suffix(name.countParts()+1);
        List<MBean> list = childrenMap.computeIfAbsent(childName, key -> new ArrayList<>());
        list.add(child);
      }
    }
    this.bean = mySelf;
    this.children = childrenMap
        .entrySet()
        .stream()
        .map(entry -> new MBeanTreeNode(entry.getKey(), entry.getValue()))
        .collect(Collectors.toList());
    Collections.sort(this.children);
  }
  
  public MBeanName name()
  {
    return name;
  }
  
  public MBean bean()
  {
    return bean;
  }
  
  public List<MBeanTreeNode> children()
  {
    return children;
  }

  @Override
  public int compareTo(MBeanTreeNode other)
  {
    return name.compareTo(other.name);
  }
}
