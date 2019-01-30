package com.hasa.test.module;

/**
 * - MasterTestService -
 * @author Hasantha Alahakoon 
 */
public class LoadedTestModuleInfo
{
  private TestModuleInfo testModuleInfo;

  private ClassLoader testModuleClassLoader;

  private String testSuiteXmlFile;

  public LoadedTestModuleInfo(TestModuleInfo testModuleInfo, ClassLoader testModuleClassLoader, String testSuiteXmlFile)
  {
    this.testModuleInfo = testModuleInfo;
    this.testModuleClassLoader = testModuleClassLoader;
    this.testSuiteXmlFile = testSuiteXmlFile;
  }

  public ClassLoader getTestModuleClassLoader()
  {
    return testModuleClassLoader;
  }

  public String getTestSuiteXmlFile()
  {
    return testSuiteXmlFile;
  }

  public TestModuleInfo getTestModuleInfo()
  {
    return testModuleInfo;
  }
}
