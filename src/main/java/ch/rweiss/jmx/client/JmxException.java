package ch.rweiss.jmx.client;

import javax.management.RuntimeMBeanException;

public class JmxException extends RuntimeException
{
  public JmxException(String message, Exception cause)
  {
    super(message, cause);
  }

  public JmxException(String message)
  {
    super(message);
  }

  public String getShortDisplayMessage()
  {
    if (getCause() instanceof RuntimeMBeanException)
    {
      Throwable cause = getCause();
      if (cause.getCause() instanceof UnsupportedOperationException)
      {
        return "unsupported";
      }
    }
    return getMessage();
  }
}
