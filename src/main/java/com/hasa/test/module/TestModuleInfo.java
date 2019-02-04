package com.hasa.test.module;

/**
 * - MasterTestService -
 * @author Hasantha Alahakoon 
 */
public class TestModuleInfo
{
  private String groupId;

  private String artifactId;

  private String version;

  public TestModuleInfo(String testModuleGroupId, String testModuleArtifactId, String testModuleVersion)
  {
    this.groupId = testModuleGroupId;
    this.artifactId = testModuleArtifactId;
    this.version = testModuleVersion;
  }

  public TestModuleInfo(String[] testInfoArray)
  {
    this(testInfoArray[0], testInfoArray[1], testInfoArray[2]);
  }

  public String getGroupId()
  {
    return groupId;
  }

  public String getArtifactId()
  {
    return artifactId;
  }

  public String getVersion()
  {
    return version;
  }

  @Override public String toString()
  {
    return groupId.concat(" : ").concat(artifactId).concat(" : ").concat(version);
  }
}
