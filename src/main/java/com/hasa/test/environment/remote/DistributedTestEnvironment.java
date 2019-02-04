package com.hasa.test.environment.remote;

import com.github.rholder.retry.*;
import com.hasa.test.config.Configuration;
import com.hasa.test.environment.remote.docker.DockerStack;
import com.hasa.test.module.TestModuleInfo;
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
public class DistributedTestEnvironment
{
  private static DistributedTestEnvironment instance = null;

  private DockerStack dockerStack;

  private Configuration config = Configuration.getInstance();

  public void spawn(TestModuleInfo testModuleInfo)
  {
    displayEnvInfo(testModuleInfo);
    dockerStack = new DockerStack(testModuleInfo);
    dockerStack.start();
  }

  public void kill()
  {
    dockerStack.kill();
  }

  public void waitAndSeeIfReady() throws ExecutionException, RetryException
  {
    waitTillEurekaServerIsUp();
    waitTillTestGatewayIsUp();
    waitTillAllSlavesAreUp(config.getNumberOfSlaves());
  }

  private void waitTillEurekaServerIsUp() throws ExecutionException, RetryException
  {
    retryTill(12, 10).call(checkIfTestRegistryServerIsUp()); //TODO These intervals has to be paramertized as System properties
  }

  private void waitTillTestGatewayIsUp() throws ExecutionException, RetryException
  {
    retryTill(6, 10).call(checkIfTestGatewayIsUp());
  }

  private void waitTillAllSlavesAreUp(int expectedNumberOfSlaves) throws ExecutionException, RetryException
  {
    retryTill(12, 10)
        .call(checkIfAllSlavesAreUp(expectedNumberOfSlaves));
  }

  private Callable<Boolean> checkIfAllSlavesAreUp(int expectedNumberOfSlaves)
  {
    return () -> {
      System.out.println("Checking If All " + expectedNumberOfSlaves + " Test Slaves are Ready ...");
      if (expectedNumberOfSlaves != getNumberOfSlavesRegisteredWithEureka())
      {
        throw new RuntimeException("Slaves not ready");
      }
      return true;
    };
  }

  private Callable<Boolean> checkIfTestGatewayIsUp()
  {
    return () -> {
      System.out.println("Checking If Test Gateway is Ready at : " + config.getTestServiceGatewayUrl());
      RestTemplate restTemplate = new RestTemplate();
      return !restTemplate.getForObject(config.getTestServiceGatewayUrl().concat("/health"), String.class).isEmpty();
    };
  }

  private Callable<Boolean> checkIfTestRegistryServerIsUp()
  {
    return () -> {
      System.out.println("Checking If Test Registry is Ready at : " + config.getTestRegistryUrl());
      RestTemplate restTemplate = new RestTemplate();
      return !restTemplate.getForObject(config.getTestRegistryUrl().concat("/eureka/apps/"), String.class).isEmpty();
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
    String json = restTemplate
        .getForObject(config.getTestRegistryUrl().concat("eureka/apps/slavetestservice"), String.class);
    JSONArray slavesInfo = JsonPath.parse(json).read("$..instanceId");
    return slavesInfo.size();
  }

  public static DistributedTestEnvironment getInstance()
  {
    if (instance == null)
    {
      instance = new DistributedTestEnvironment();
    }
    return instance;
  }

  private void displayEnvInfo(TestModuleInfo testModuleInfo)
  {
    System.out.println("--------------------------------------------------");
    System.out.println("Test Module to Run : " + testModuleInfo);
    System.out.println("Test Suite Xml to Run : " + config.getTestSuiteXml());
    System.out.println("Test Gateway Url : " + config.getTestServiceGatewayUrl());
    System.out.println("Test Registry Url : " + config.getTestRegistryUrl());
    System.out.println("Number Of Test Slaves in Cluster : " + config.getNumberOfSlaves());
    System.out.println("Cambio Remote Repository : " + config.getCambioRepoUrl());
    System.out.println("Maven Local Repository : " + config.getMavenLocalRepoPath());
    System.out.println("--------------------------------------------------");
  }

  private DistributedTestEnvironment()
  {
  }
}
