package com.hasa.util;

import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.springframework.web.client.RestTemplate;

/**
 * - MasterTestService -
 * @author hasantha.alahakoon
 */
public final class AppParams
{
  private static AppParams instance;

  private final String numberOfSlaves = System.getProperty("numberOfSlaves", "1");

  private final String discoveryServerUrl = System.getProperty("discoveryServerUrl", "http://localhost:8761");

  private String testServiceGatewayUrl;

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

  public String getTestServiceGatewayUrl()
  {
    return testServiceGatewayUrl;
  }

  private String queryTestSlavesGatewayUrlFromEureka()
  {
    RestTemplate restTemplate = new RestTemplate();
    String json = restTemplate.getForObject(discoveryServerUrl.concat("/eureka/apps/testservice"), String.class);
    JSONArray slavesArray = JsonPath.parse(json).read("$..homePageUrl");
    return slavesArray.get(0).toString();
  }

  private AppParams()
  {
    System.out.println("--- Number of expected slaves : " + numberOfSlaves);
    System.out.println("--- Discovery Server at : " + discoveryServerUrl);
    testServiceGatewayUrl = queryTestSlavesGatewayUrlFromEureka();
    System.out.println("--- TestService Gateway Server at : " + testServiceGatewayUrl);
  }

}
