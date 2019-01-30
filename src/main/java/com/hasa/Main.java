package com.hasa;

import com.github.rholder.retry.RetryException;
import com.hasa.test.config.Configuration;
import com.hasa.test.environment.local.LocalTestEnvironment;
import com.hasa.test.environment.remote.RemoteEnvironment;
import com.hasa.test.module.LoadedTestModuleInfo;
import com.hasa.test.module.TestModuleInfo;
import org.eclipse.aether.resolution.DependencyResolutionException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * - MasterTestService -
 * @author Hasantha Alahakoon 
 */
public class Main
{
  public static void main(String args[])
  {
    try
    {
      TestModuleInfo testModuleInfo = validateAndGetTestModuleInfo(args);
      RemoteEnvironment.getInstance().start(testModuleInfo);
      LoadedTestModuleInfo loadedTestModuleInfo = LocalTestEnvironment.getInstance()
          .loadTestModuleWithDependencies(testModuleInfo);
      RemoteEnvironment.getInstance().waitAndSeeIfReady();
      LocalTestEnvironment.getInstance()
          .runTestSuite(loadedTestModuleInfo, Configuration.getInstance().getNumberOfSlaves());
    }
    catch (DependencyResolutionException e)
    {
      System.out.println("Error Loading Test Module from Maven dependency info.");
      e.printStackTrace();
    }
    catch (ExecutionException | RetryException e)
    {
      System.out.println("Error Setting up Test Environment. Test Suite Execution will be aborted.");
      e.printStackTrace();
    }
    catch (IOException e)
    {
      System.out.println("Error Extracting TestSuite XML File from Test Module.");
      e.printStackTrace();
    }
    finally
    {
      RemoteEnvironment.getInstance().kill();
    }
  }

  private static TestModuleInfo validateAndGetTestModuleInfo(String[] args)
  {
    TestModuleInfo dependencyInfo;
    if (args.length == 1)
    {
      String[] testModuleInfoArray = args[0].split(":");
      if (testModuleInfoArray.length == 3)
      {
        dependencyInfo = new TestModuleInfo(testModuleInfoArray);
      }
      else
      {
        throw new RuntimeException(
            "Test Module Dependency Information not provided. Need to be in Following Format: groupId:artifactid:version");
      }
    }
    else
    {
      throw new RuntimeException("Test Module Dependency Information not provided.");
    }
    return dependencyInfo;
  }
}