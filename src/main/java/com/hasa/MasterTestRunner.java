package com.hasa;

import com.hasa.executor.RemoteTestExecutor;
import se.cambio.qa.multiprocess.testframework.executor.TestExecutor;
import se.cambio.qa.multiprocess.testframework.runner.DelegatingTestRunner;

/**
 * - SlaveTestService -
 * @author Hasantha Alahakoon 
 */
public class MasterTestRunner extends DelegatingTestRunner
{
  @Override protected TestExecutor getTestExecutor(int testCount)
  {
    return new RemoteTestExecutor();
  }
}
