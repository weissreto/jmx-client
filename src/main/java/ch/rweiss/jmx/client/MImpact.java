package ch.rweiss.jmx.client;

import javax.management.MBeanOperationInfo;

import ch.rweiss.check.Check;

public enum MImpact
{
  /** @see MBeanOperationInfo#INFO */
  INFO(MBeanOperationInfo.INFO),
  
  /** @see MBeanOperationInfo#ACTION */
  ACTION(MBeanOperationInfo.ACTION),
  
  /** @see MBeanOperationInfo#ACTION_INFO */
  ACTION_INFO(MBeanOperationInfo.ACTION_INFO),
  
  /** @see MBeanOperationInfo#UNKNOWN */
  UNKNOWN(MBeanOperationInfo.UNKNOWN);
  
  private int value;

  private MImpact(int value)
  {
    this.value = value;
  }
  
  static MImpact forValue(int impactValue)
  {
    Check.parameter("impactValue").withValue(impactValue).isInRange(INFO.value, UNKNOWN.value);
    for (MImpact impact : values())
    {
      if (impact.value == impactValue)
      {
        return impact;
      }
    }
    return null;
  }
}
