package com.hasa.testmodule;

import com.hasa.util.Environment;
import com.hasa.util.XmlUtil;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.*;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.util.artifact.JavaScopes;
import org.eclipse.aether.util.filter.DependencyFilterUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * - MasterTestService -
 * @author Hasantha Alahakoon 
 */
public class DynamicTestModuleLoader
{
  private static DynamicTestModuleLoader instance;

  public TestModuleInfo loadTestModuleWithDependencies(String groupId, String artifactId, String version)
      throws DependencyResolutionException, IOException
  {
    DependencyResult dependencies = downloadTestDependenciesFromRepo(groupId, artifactId, version);
    ClassLoader classLoaderOfDependencies = loadTestDependenciesToVM(dependencies);
    InputStream testSuiteXmlAsASteam = getTestSuiteXMLFromTestModule(classLoaderOfDependencies);
    String testSuiteXMLPath = XmlUtil.getInstance().moveTestSuiteXmlToTempLocation(testSuiteXmlAsASteam);
    return new TestModuleInfo(classLoaderOfDependencies, testSuiteXMLPath);
  }

  private InputStream getTestSuiteXMLFromTestModule(ClassLoader classLoaderOfDependencies)
  {
    return classLoaderOfDependencies.getResourceAsStream("testplan/" + Environment.getInstance().getTestSuiteXml());
  }

  private ClassLoader loadTestDependenciesToVM(DependencyResult dependencies) throws MalformedURLException
  {
    return new URLClassLoader(getPathsToTestDependencies(dependencies), this.getClass().getClassLoader());
  }

  private DependencyResult downloadTestDependenciesFromRepo(String groupId, String artifactId, String version)
      throws DependencyResolutionException
  {
    DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
    locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
    locator.addService(TransporterFactory.class, FileTransporterFactory.class);
    locator.addService(TransporterFactory.class, HttpTransporterFactory.class);

    RepositorySystem system = locator.getService(RepositorySystem.class);

    DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();

    LocalRepository localRepo = new LocalRepository(Environment.getInstance().getMavenLocalRepoPath());
    session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));

    Artifact artifact = new DefaultArtifact(groupId, artifactId, "tests","jar", version);
    ArtifactRequest artifactRequest = new ArtifactRequest();
    artifactRequest.setArtifact(artifact);

    RemoteRepository cambioRepo = new RemoteRepository.Builder("central", "default", Environment.getInstance().getCambioRepoUrl()).build();
    artifactRequest.addRepository(cambioRepo);

    DependencyFilter classpathFlter = DependencyFilterUtils.classpathFilter( JavaScopes.COMPILE );

    CollectRequest collectRequest = new CollectRequest();
    collectRequest.setRoot( new Dependency( artifact, JavaScopes.COMPILE ) );
    collectRequest.setRepositories(Arrays.asList(cambioRepo));

    DependencyRequest dependencyRequest = new DependencyRequest( collectRequest, classpathFlter );
    return system.resolveDependencies(session, dependencyRequest);
  }

  static URL[] getPathsToTestDependencies(DependencyResult dependencyResult) throws MalformedURLException
  {
    List<URL> pathsToDependencies = new ArrayList<>();
    List<ArtifactResult> dependencies = dependencyResult.getArtifactResults();
    for (ArtifactResult dependency: dependencies)
    {
      pathsToDependencies.add(dependency.getArtifact().getFile().toURI().toURL());
    }
    return pathsToDependencies.toArray(new URL[0]);
  }

  public static DynamicTestModuleLoader getInstance()
  {
    if (instance == null)
    {
      instance = new DynamicTestModuleLoader();
    }
    return instance;
  }
}
