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
public class RemoteEnvironment
{
  private static RemoteEnvironment instance = null;

  private DockerStack dockerStack;

  private Configuration config = Configuration.getInstance();

  public void start(TestModuleInfo testModuleInfo)
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
    retryTill(6, 10).call(checkIfTestRegistryServerIsUp()); //TODO These intervals has to be paramertized as System properties
  }

  private void waitTillTestGatewayIsUp() throws ExecutionException, RetryException
  {
    retryTill(3, 10).call(checkIfTestGatewayIsUp());
  }

  private void waitTillAllSlavesAreUp(int expectedNumberOfSlaves) throws ExecutionException, RetryException
  {
    retryTill(6, 10)
        .call(checkIfAllSlavesAreUp(expectedNumberOfSlaves)); //TODO This does not seem to work when numberOfSlaves > 1
  }

  private Callable<Boolean> checkIfAllSlavesAreUp(int expectedNumberOfSlaves)
  {
    System.out.println("Checking If All " + expectedNumberOfSlaves + " Test Slaves are Ready ...");
    return () -> expectedNumberOfSlaves == getNumberOfSlavesRegisteredWithEureka();
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

  public static RemoteEnvironment getInstance()
  {
    if (instance == null)
    {
      instance = new RemoteEnvironment();
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
    System.out.println("Number Of Slaves : " + config.getNumberOfSlaves());
    System.out.println("Cambio Remote Repository : " + config.getCambioRepoUrl());
    System.out.println("Maven Local Repository : " + config.getMavenLocalRepoPath());
    System.out.println("--------------------------------------------------");
  }

  private RemoteEnvironment()
  {
  }
}
