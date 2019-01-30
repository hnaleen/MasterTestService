package com.hasa.test.config;

/**
 * - MasterTestService -
 * @author Hasantha Alahakoon 
 */
public class Configuration
{
  private static Configuration instance;

  private final String testRegistryUrl = System.getProperty("testRegistryUrl", "http://localhost:8761");

  private String testServiceGatewayUrl = System.getProperty("testGatewayUrl", "http://localhost:8910");

  private String numberOfSlaves = System.getProperty("numberOfSlaves", "1");

  private String mavenLocalRepoPath = System.getProperty("localRepo", "C:/Software/mvn_repo_352");

  private String cambioRepoUrl = System.getProperty("cambioRepo", "http://repo.cambio.lk/nexus/content/groups/public");

  private String testSuiteXml = System.getProperty("testSuiteXml", "smoke.xml");

  public int getNumberOfSlaves()
  {
    return Integer.parseInt(numberOfSlaves);
  }

  public String getTestServiceGatewayUrl()
  {
    return testServiceGatewayUrl;
  }

  public String getCambioRepoUrl()
  {
    return cambioRepoUrl;
  }

  public String getMavenLocalRepoPath()
  {
    return mavenLocalRepoPath;
  }

  public String getTestSuiteXml()
  {
    return testSuiteXml;
  }

  public String getTestRegistryUrl()
  {
    return testRegistryUrl;
  }

  public static Configuration getInstance()
  {
    if (instance == null)
    {
      instance = new Configuration();
    }
    return instance;
  }
}
