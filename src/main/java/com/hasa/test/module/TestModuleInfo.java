package com.hasa.test.module;

/**
 * - MasterTestService -
 * @author Hasantha Alahakoon 
 */
public class TestModuleInfo
{
  private String testModuleGroupId;

  private String testModuleArtifactId;

  private String testModuleVersion;

  public TestModuleInfo(String testModuleGroupId, String testModuleArtifactId, String testModuleVersion)
  {
    this.testModuleGroupId = testModuleGroupId;
    this.testModuleArtifactId = testModuleArtifactId;
    this.testModuleVersion = testModuleVersion;
  }

  public TestModuleInfo(String[] testInfoArray)
  {
    this(testInfoArray[0], testInfoArray[1], testInfoArray[2]);
  }

  public String getGroupId()
  {
    return testModuleGroupId;
  }

  public String getArtifactId()
  {
    return testModuleArtifactId;
  }

  public String getVersion()
  {
    return testModuleVersion;
  }

  @Override public String toString()
  {
    return testModuleGroupId.concat(" : ").concat(testModuleArtifactId).concat(" : ").concat(testModuleVersion);
  }
}
