package com.hasa.executor;

import com.hasa.util.Environment;
import org.springframework.web.client.RestTemplate;
import se.cambio.test.runner.framework.dto.TestCaseResultDTO;
import se.cambio.test.runner.framework.executor.TestExecutor;

/**
 * - MasterTestService -
 * @author Hasantha Alahakoon 
 */
public class RemoteTestExecutor implements TestExecutor
{
  @Override public TestCaseResultDTO execute(final String testClassName, final String testMethodName)
      throws RuntimeException
  {
    System.out.println("Executing : " + testClassName + "." + testMethodName);
    TestCaseResultDTO result = new RestTemplate()
        .getForObject(getTestServerUrl(testClassName, testMethodName), TestCaseResultDTO.class);
    return result;
  }

  private String getTestServerUrl(String testClassName, String testMethodName)
  {
    return new StringBuilder(Environment.getInstance().getTestServiceGatewayUrl()).append("/v1/tests/")
        .append(testClassName).append("/").append(testMethodName).toString();
  }
}
