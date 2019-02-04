package com.hasa.test.environment.remote.docker;

import com.hasa.test.config.Configuration;
import com.hasa.test.module.TestModuleInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * - MasterTestService -
 * @author Hasantha Alahakoon 
 */
public class DockerStack
{
  private final TestModuleInfo testModuleInfo;

  public DockerStack(TestModuleInfo testModuleInfo)
  {
    this.testModuleInfo = testModuleInfo;
  }

  public void start()
  {
    try
    {
      ProcessBuilder docker = new ProcessBuilder(getDockerStackStartCommand()).inheritIO();
      docker.environment().put("testModuleGroupId", testModuleInfo.getGroupId());
      docker.environment().put("testModuleArtifactId", testModuleInfo.getArtifactId());
      docker.environment().put("testModuleVersion", testModuleInfo.getVersion());
      docker.environment().put("numberOfSlaves", Integer.toString(Configuration.getInstance().getNumberOfSlaves()));
      Process process = docker.start();
      int exitCode = process.waitFor();
      if (exitCode != 0)
      {
        throw new RuntimeException("Error Starting Docker Test Environment");
      }
    }
    catch (IOException | InterruptedException e)
    {
      throw new RuntimeException("Error Starting Docker Test Environment", e);
    }
  }

  public void kill()
  {
    try
    {
      ProcessBuilder docker = new ProcessBuilder(getDockerStackKillCommand()).inheritIO();
      Process process = docker.start();
      int exitCode = process.waitFor();
      if (exitCode != 0)
      {
        throw new RuntimeException("Error Destroying Docker Test Environment");
      }
    }
    catch (Exception e)
    {
      throw new RuntimeException("Error Killing Docker Test Environment", e);

    }
  }

  private List<String> getDockerStackStartCommand()
  {
    List<String> command = new ArrayList<>();
    command.add("docker");
    command.add("stack");
    command.add("deploy");
    command.add("-c");
    command.add("D:/Source_GIT/Other/TestDefaults/src/main/java/com/hasa/docker-compose.yml"); //TODO
    command.add("TestFramework");
    return command;
  }

  private List<String> getDockerStackKillCommand()
  {
    List<String> command = new ArrayList<>();
    command.add("docker");
    command.add("stack");
    command.add("rm");
    command.add("TestFramework");
    return command;
  }

  public static void main(String[] args)
  {
    new DockerStack(new TestModuleInfo("se.cambio.qa", "cambio-taf-nova-ward-test", "1.0-SNAPSHOT")).start();
  }
}
