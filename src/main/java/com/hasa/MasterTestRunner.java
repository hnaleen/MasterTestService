package com.hasa;

import com.hasa.test.executor.RemoteTestExecutor;
import se.cambio.test.runner.framework.executor.TestExecutor;
import se.cambio.test.runner.framework.runner.DelegatingTestRunner;

/**
 * - MasterTestService -
 * @author Hasantha Alahakoon 
 */
public class MasterTestRunner extends DelegatingTestRunner
{
  @Override protected TestExecutor getTestExecutor(int testCount)
  {
    return new RemoteTestExecutor();
  }
}
