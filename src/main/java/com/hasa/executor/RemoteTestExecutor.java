package com.hasa.executor;

import com.hasa.util.AppParams;
import org.springframework.web.client.RestTemplate;
import se.cambio.qa.multiprocess.testframework.dto.TestCaseResultDTO;
import se.cambio.qa.multiprocess.testframework.executor.TestExecutor;

/**
 * - MasterTestService -
 * @author Hasantha Alahakoon 
 */
public class RemoteTestExecutor implements TestExecutor
{
  @Override public TestCaseResultDTO execute(final String testClassName, final String testMethodName)
      throws RuntimeException
  {
    RestTemplate restTemplate = new RestTemplate();
    TestCaseResultDTO result = restTemplate
        .getForObject(getTestServerUrl(testClassName, testMethodName), TestCaseResultDTO.class);
    return result;
  }

  private String getTestServerUrl(String testClassName, String testMethodName)
  {
    return new StringBuilder(AppParams.getInstance().getTestServiceGatewayUrl()).append("v1/tests/")
        .append(testClassName).append("/").append(testMethodName).toString();
  }
}
