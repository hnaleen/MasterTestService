package com.hasa.util;

/**
 * - MasterTestService -
 * @author hasantha.alahakoon
 */
public final class AppParams
{
  private static AppParams instance;

  private final String numberOfSlaves = System.getProperty("numberOfSlaves", "1");

  private final String discoveryServerUrl = System
      .getProperty("discoveryServerUrl", "http://localhost:8761/eureka/apps/slavetestservice");

  public static AppParams getInstance()
  {
    if (instance == null)
    {
      instance = new AppParams();
    }
    return instance;
  }

  public String getNumberOfSlaves()
  {
    return numberOfSlaves;
  }

  public String getDiscoveryServerUrl()
  {
    return discoveryServerUrl;
  }

  private AppParams()
  {
    System.out.println("--- Number of expected slaves : " + numberOfSlaves);
    System.out.println("--- Discovery Server at : " + discoveryServerUrl);
  }

}
