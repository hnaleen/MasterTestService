package com.hasa.test.environment.local;

import com.hasa.MasterTestRunner;
import com.hasa.test.module.LoadedTestModuleInfo;
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
public class LocalTestEnvironment
{
  private static LocalTestEnvironment instance;

  public LoadedTestModuleInfo loadTestModuleWithDependencies(TestModuleInfo testModuleInfo)
      throws DependencyResolutionException, IOException
  {
    return DynamicTestModuleLoader.getInstance()
        .loadTestModuleWithDependencies(testModuleInfo);
  }

  public void runTestSuite(LoadedTestModuleInfo testModuleInfo, int numberOfSlaves)
  {
    TestNG testNG = new TestNG();
    testNG.setTestSuites(Arrays.asList(testModuleInfo.getTestSuiteXmlFile()));
    testNG.setListenerClasses(Arrays.asList(MasterTestRunner.class));
    testNG.addClassLoader(testModuleInfo.getTestModuleClassLoader());
    if (numberOfSlaves > 1)
    {
      testNG.setParallel(XmlSuite.ParallelMode.METHODS);
      testNG.setThreadCount(numberOfSlaves);
    }
    testNG.run();
  }
  public static LocalTestEnvironment getInstance()
  {
    if (instance == null)
    {
      instance = new LocalTestEnvironment();
    }
    return instance;
  }
}
