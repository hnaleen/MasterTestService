package com.hasa.util;

import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * - MasterTestService -
 * @author Hasantha Alahakoon 
 */
public class LoadBalancer //TODO Replace with a proper client side load balancer like Ribbon
{
  private String DISCOVERY_SERVER_URL = AppParams.getInstance().getDiscoveryServerUrl();

  private final int expectedSlaveCount = Integer.parseInt(AppParams.getInstance().getNumberOfSlaves());

  private final List<String> allAvailableSlaves;

  int currentSlaveIndex = 0;

  private static LoadBalancer instance;

  public synchronized static LoadBalancer getInstance()
  {
    if (instance == null)
    {
      instance = new LoadBalancer();
    }
    return instance;
  }

  private LoadBalancer()
  {
    waitUntilAllSlavesAreUp();
    allAvailableSlaves = getAllAvailableSlaves();
  }

  public synchronized String getNextAvailableSlave()
  {
    waitUntilASlaveIsFree();
    return allAvailableSlaves.get(currentSlaveIndex++ % expectedSlaveCount);
  }

  private void waitUntilASlaveIsFree()
  {
  }

  private void waitUntilAllSlavesAreUp()
  {
  }

  private ArrayList<String> getAllAvailableSlaves()
  {
    ArrayList<String> slaves = new ArrayList<>();
    RestTemplate restTemplate = new RestTemplate();
    String json = restTemplate.getForObject(DISCOVERY_SERVER_URL, String.class);
    JSONArray slavesArray = JsonPath.parse(json).read("$..homePageUrl");
    for (Object obj : slavesArray)
    {
      slaves.add((String) obj);
    }
    return slaves;
  }
}
