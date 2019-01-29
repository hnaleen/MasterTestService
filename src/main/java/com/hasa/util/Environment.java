package com.hasa.util;

import com.github.rholder.retry.*;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * - MasterTestService -
 * @author Hasantha Alahakoon 
 */
public class Environment
{
  private static Environment instance = null;

  private final String discoveryServerUrl = System.getProperty("testRegistryUrl", "http://localhost:8761");

  private String testServiceGatewayUrl = System.getProperty("testGatewayUrl", "http://localhost:8910");

  private String numberOfSlaves = System.getProperty("numberOfSlaves", "1");

  private String mavenLocalRepoPath = System.getProperty("localRepo", "C:/Software/mvn_repo_352");

  private String cambioRepoUrl = System.getProperty("cambioRepo", "http://repo.cambio.lk/nexus/content/groups/public");

  private String testSuiteXml = System.getProperty("testSuiteXml", "smoke.xml");

  public void waitTillReady() throws ExecutionException, RetryException
  {
    waitTillEurekaServerIsUp();
    waitTillTestGatewayIsUp();
    waitTillAllSlavesAreUp(getNumberOfSlaves());
  }

  public int getNumberOfSlaves()
  {
    return Integer.parseInt(numberOfSlaves);
  }

  public String getTestServiceGatewayUrl()
  {
    return testServiceGatewayUrl;
  }

  private void waitTillEurekaServerIsUp() throws ExecutionException, RetryException
  {
    retryTill(3, 10).call(checkIfEurekaServerIsUp());
  }

  private void waitTillTestGatewayIsUp() throws ExecutionException, RetryException
  {
    retryTill(3, 10).call(checkIfTestGatewayIsUp());
  }

  private void waitTillAllSlavesAreUp(int expectedNumberOfSlaves) throws ExecutionException, RetryException
  {
    retryTill(6, 10).call(checkIfAllSlavesAreUp(expectedNumberOfSlaves)); //TODO This does not seem to work when numberOfSlaves > 1
  }

  private Callable<Boolean> checkIfAllSlavesAreUp(int expectedNumberOfSlaves)
  {
    System.out.println("Checking If All " + expectedNumberOfSlaves + " Test Slaves are Ready ...");
    return () -> expectedNumberOfSlaves == getNumberOfSlavesRegisteredWithEureka();
  }

  private Callable<Boolean> checkIfTestGatewayIsUp()
  {
    return () -> {
      System.out.println("Checking If Test Gateway is Ready at : " + testServiceGatewayUrl);
      RestTemplate restTemplate = new RestTemplate();
      return !restTemplate.getForObject(testServiceGatewayUrl.concat("/health"), String.class).isEmpty();
    };
  }

  private Callable<Boolean> checkIfEurekaServerIsUp()
  {
    return () -> {
      System.out.println("Checking If Test Registry is Ready at : " + discoveryServerUrl);
      RestTemplate restTemplate = new RestTemplate();
      return !restTemplate.getForObject(discoveryServerUrl.concat("/eureka/apps/"), String.class).isEmpty();
    };
  }

  private Retryer<Boolean> retryTill(int numberOfRetries, int retryInterval)
  {
    return RetryerBuilder.<Boolean>newBuilder().retryIfException()
        .withWaitStrategy(WaitStrategies.fixedWait(retryInterval, TimeUnit.SECONDS))
        .withStopStrategy(StopStrategies.stopAfterAttempt(numberOfRetries)).build();
  }

  private int getNumberOfSlavesRegisteredWithEureka()
  {
    RestTemplate restTemplate = new RestTemplate();
    String json = restTemplate.getForObject(discoveryServerUrl.concat("eureka/apps/slavetestservice"), String.class);
    JSONArray slavesInfo = JsonPath.parse(json).read("$..instanceId");
    return slavesInfo.size();
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

  public static Environment getInstance()
  {
    if (instance == null)
    {
      instance = new Environment();
    }
    return instance;
  }

  private Environment()
  {
    System.out.println("--------------------------------------------------");
    System.out.println("Test Suite Xml to Run : " + testSuiteXml);
    System.out.println("Test Gateway Url : " + testServiceGatewayUrl);
    System.out.println("Test Registry Url : " + discoveryServerUrl);
    System.out.println("Number Of Slaves : " + numberOfSlaves);
    System.out.println("Cambio Remote Repository : " + cambioRepoUrl);
    System.out.println("Maven Local Repository : " + mavenLocalRepoPath);
    System.out.println("--------------------------------------------------");
  }
}
