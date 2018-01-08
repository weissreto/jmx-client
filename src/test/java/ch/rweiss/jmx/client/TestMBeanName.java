package ch.rweiss.jmx.client;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import ch.rweiss.jmx.client.MBeanName;

public class TestMBeanName
{
  private final MBeanName testee; 
  
  public TestMBeanName() throws MalformedObjectNameException
  {
    testee = MBeanName.createFor(new ObjectName("ch.weiss:type=person,name=reto"));
  }
  
  @Test
  public void fullQualifiedName()
  {
    assertThat(testee.fullQualifiedName()).isEqualTo("ch.weiss:type=person,name=reto");
  }
  
  @Test
  public void valueParts()
  {
    assertThat(testee.valueParts()).containsExactly("ch.weiss", "person", "reto");
  }
  
  @Test
  public void simpleName()
  {
    assertThat(testee.simpleName()).isEqualTo("reto");
  }
  
}
