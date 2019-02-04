package com.hasa.test.module;

/**
 * - MasterTestService -
 * @author Hasantha Alahakoon 
 */
public class TestModuleRuntimeInfo
{
  private TestModuleInfo testModuleInfo;

  private ClassLoader classLoader;

  private String testSuiteXmlFile;

  public TestModuleRuntimeInfo(TestModuleInfo testModuleInfo, ClassLoader testModuleClassLoader, String testSuiteXmlFile)
  {
    this.testModuleInfo = testModuleInfo;
    this.classLoader = testModuleClassLoader;
    this.testSuiteXmlFile = testSuiteXmlFile;
  }

  public ClassLoader getClassLoader()
  {
    return classLoader;
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
