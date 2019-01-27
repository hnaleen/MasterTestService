package com.hasa.testmodule;

/**
 * - MasterTestService -
 * @author Hasantha Alahakoon 
 */
public class TestModuleInfo
{
  private ClassLoader testModuleClassLoader;

  private String testSuiteXmlFile;

  public TestModuleInfo(ClassLoader testModuleClassLoader, String testSuiteXmlFile)
  {
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
}
