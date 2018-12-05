package com.hasa.executor;

import org.springframework.web.client.RestTemplate;
import se.cambio.qa.multiprocess.testframework.dto.TestCaseResultDTO;
import se.cambio.qa.multiprocess.testframework.executor.TestExecutor;

/**
 * - SlaveTestService -
 * @author Hasantha Alahakoon 
 */
public class RemoteTestExecutor implements TestExecutor
{
  String baseUrl = "http://cllk-hasanthaal:8080/v1/tests/";

  @Override public TestCaseResultDTO execute(final String testClassName, final String testMethodName) throws RuntimeException
  {
    RestTemplate restTemplate = new RestTemplate();
    TestCaseResultDTO result = restTemplate
        .getForObject(getUrl(testClassName, testMethodName), TestCaseResultDTO.class);
    return result;
  }

  private String getUrl(String testClassName, String testMethodName)
  {
    return new StringBuilder(baseUrl).append(testClassName).append("/").append(testMethodName).toString();
  }
}
