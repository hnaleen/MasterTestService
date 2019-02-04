package com.hasa.test.environment.local;

import com.hasa.MasterTestRunner;
import com.hasa.test.module.TestModuleRuntimeInfo;
import com.hasa.test.module.TestModuleInfo;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.testng.TestNG;
import org.testng.xml.XmlSuite;

import java.io.IOException;
import java.util.Arrays;

/**
 * - MasterTestService -
 * @author Hasantha Alahakoon 
 */
public class DistributedTestEnvironmentDelegate
{
  private static DistributedTestEnvironmentDelegate instance;

  public TestModuleRuntimeInfo loadTestModuleToLocalVM(TestModuleInfo testModuleInfo)
      throws DependencyResolutionException, IOException
  {
    return DynamicTestModuleLoader.getInstance()
        .loadTestModuleWithDependencies(testModuleInfo);
  }

  public void coordinateTestSuiteRun(TestModuleRuntimeInfo testModuleRuntimeInfo, int numberOfSlaves)
  {
    TestNG testNG = new TestNG();
    testNG.setTestSuites(Arrays.asList(testModuleRuntimeInfo.getTestSuiteXmlFile()));
    testNG.setListenerClasses(Arrays.asList(MasterTestRunner.class));
    testNG.addClassLoader(testModuleRuntimeInfo.getClassLoader());
    if (numberOfSlaves > 1)
    {
      testNG.setParallel(XmlSuite.ParallelMode.METHODS);
      testNG.setThreadCount(numberOfSlaves);
    }
    testNG.run();
  }
  public static DistributedTestEnvironmentDelegate getInstance()
  {
    if (instance == null)
    {
      instance = new DistributedTestEnvironmentDelegate();
    }
    return instance;
  }
}
