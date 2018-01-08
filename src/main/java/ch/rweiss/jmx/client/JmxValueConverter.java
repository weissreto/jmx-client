package ch.rweiss.jmx.client;

import java.lang.reflect.Array;
import java.util.Objects;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.TabularData;

import org.apache.commons.lang3.StringUtils;

public class JmxValueConverter
{
  private Object value;

  public JmxValueConverter(Object value)
  {
    this.value = value;
  }
  
  @Override
  public String toString()
  {
    String val = fromValue();
    return removeNewLines(val);
  }

  private String fromValue()
  {
    if (value != null && value.getClass().isArray())
    {
      return fromArray(value);
    }
    if (value instanceof CompositeData)
    {
      return fromCompositeDataSupport((CompositeData) value);
    }
    if (value instanceof TabularData)
    {
      return fromTabularDataSupport((TabularData) value);
    }
    return Objects.toString(value);
  }

  private static String removeNewLines(String valueStr)
  {
    StringBuilder builder = new StringBuilder();
    String val = StringUtils.remove(valueStr, "\r");
    boolean previousLineEndedWithWhitespace = true;
    for (String line : val.split("\n"))
    {
      if (!line.isEmpty())
      {
        if (!previousLineEndedWithWhitespace && !Character.isWhitespace(line.charAt(0)))
        {
          builder.append(" ");
        }
        previousLineEndedWithWhitespace = Character.isWhitespace(line.charAt(line.length()-1));
      }     
      builder.append(line);
    }
    return builder.toString();
  }

  private static String fromArray(Object value)
  {
    StringBuilder builder = new StringBuilder();
    builder.append("[");
    boolean first = true;
    for (int pos = 0; pos < Array.getLength(value); pos++)
    {
      Object entry = Array.get(value, pos);
      if (!first)
      {
        builder.append(", ");
      }
      first = false;
      builder.append(new JmxValueConverter(entry).toString());
    }
    builder.append("]");
    return builder.toString();
  }

  private static String fromCompositeDataSupport(CompositeData val)
  {
    StringBuilder builder = new StringBuilder();
    String type = getSimpleTypeName(val.getCompositeType());
    builder.append(type);
    builder.append("{");
    boolean first = true;
    for (String key : val.getCompositeType().keySet())
    {
      if (!first)
      {
        builder.append(", ");
      }
      first = false;
      builder.append(key);
      builder.append(": ");
      String subValue = new JmxValueConverter(val.get(key)).toString();
      builder.append(subValue);
    }
    builder.append("}");
    return builder.toString();
  }

  private static String fromTabularDataSupport(TabularData val)
  {
    StringBuilder builder = new StringBuilder();
    String type = getSimpleTypeName(val.getTabularType());
    builder.append(type);
    builder.append("[");
    boolean first = true;
    for (String columnName : val.getTabularType().getRowType().keySet())
    {
      if (!first)
      {
        builder.append("|");
      }
      first = false;
      builder.append(columnName);
    }
    builder.append("][");
    for (Object row : val.values())
    {
      builder.append("[");
      first = true;
      CompositeData rowComposite = (CompositeData)row;
      for (Object cell : rowComposite.values())
      {
        if (!first)
        {
          builder.append("|");
        }
        first = false;
        builder.append(new JmxValueConverter(cell).toString());
      }
      builder.append("]");
    }
    builder.append("]");
    return builder.toString();
  }

  private static String getSimpleTypeName(OpenType<?> openType)
  {
    String type = openType.getTypeName();
    type = StringUtils.substringBefore(type, "<");
    type = StringUtils.substringAfterLast(type, ".");
    return type;
  }
}
