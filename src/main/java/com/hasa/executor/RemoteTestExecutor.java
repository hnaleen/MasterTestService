package com.hasa.executor;

import com.hasa.util.LoadBalancer;
import org.springframework.web.client.RestTemplate;
import se.cambio.qa.multiprocess.testframework.dto.TestCaseResultDTO;
import se.cambio.qa.multiprocess.testframework.executor.TestExecutor;

/**
 * - MasterTestService -
 * @author Hasantha Alahakoon 
 */
public class RemoteTestExecutor implements TestExecutor
{
  String baseUrl = "http://cllk-hasanthaal:8080/v1/tests/";

  @Override public TestCaseResultDTO execute(final String testClassName, final String testMethodName)
      throws RuntimeException
  {
    RestTemplate restTemplate = new RestTemplate();
    TestCaseResultDTO result = restTemplate
        .getForObject(getUrl(testClassName, testMethodName), TestCaseResultDTO.class);
    return result;
  }

  private String getUrl(String testClassName, String testMethodName)
  {
    return new StringBuilder(LoadBalancer.getInstance().getNextAvailableSlave()).append("v1/tests/")
        .append(testClassName).append("/").append(testMethodName).toString();
  }
}
