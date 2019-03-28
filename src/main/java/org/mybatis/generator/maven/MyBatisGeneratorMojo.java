//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.mybatis.generator.maven;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.ShellCallback;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.XMLParserException;
import org.mybatis.generator.internal.ObjectFactory;
import org.mybatis.generator.internal.util.ClassloaderUtility;
import org.mybatis.generator.internal.util.StringUtility;
import org.mybatis.generator.internal.util.messages.Messages;
import org.mybatis.generator.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

@Mojo(
        name = "generate",
        defaultPhase = LifecyclePhase.GENERATE_SOURCES,
        requiresDependencyResolution = ResolutionScope.TEST
)
public class MyBatisGeneratorMojo extends AbstractMojo {

    private static MyBatisGeneratorMojo myBatisGeneratorMojo = null;

    private ThreadLocal<ClassLoader> savedClassloader = new ThreadLocal();
    @Parameter(
            property = "project",
            required = true,
            readonly = true
    )
    private MavenProject project;
    @Parameter(
            property = "mybatis.generator.outputDirectory",
            defaultValue = "${project.build.directory}/generated-sources/mybatis-generator",
            required = true
    )
    private File outputDirectory;
    @Parameter(
            property = "mybatis.generator.configurationFile",
            defaultValue = "${project.basedir}/src/main/resources/generatorConfig.xml",
            required = true
    )
    private File configurationFile;
    @Parameter(
            property = "mybatis.generator.verbose",
            defaultValue = "false"
    )
    private boolean verbose;
    @Parameter(
            property = "mybatis.generator.overwrite",
            defaultValue = "false"
    )
    private boolean overwrite;
    @Parameter(
            property = "mybatis.generator.sqlScript"
    )
    private String sqlScript;
    @Parameter(
            property = "mybatis.generator.jdbcDriver"
    )
    private String jdbcDriver;
    @Parameter(
            property = "mybatis.generator.jdbcURL"
    )
    private String jdbcURL;
    @Parameter(
            property = "mybatis.generator.jdbcUserId"
    )
    private String jdbcUserId;
    @Parameter(
            property = "mybatis.generator.jdbcPassword"
    )
    private String jdbcPassword;
    @Parameter(
            property = "mybatis.generator.tableNames"
    )
    private String tableNames;
    @Parameter(
            property = "mybatis.generator.contexts"
    )
    private String contexts;
    @Parameter(
            property = "mybatis.generator.skip",
            defaultValue = "false"
    )
    private boolean skip;
    @Parameter(
            property = "mybatis.generator.includeCompileDependencies",
            defaultValue = "false"
    )
    private boolean includeCompileDependencies;
    @Parameter(
            property = "mybatis.generator.includeAllDependencies",
            defaultValue = "false"
    )
    private boolean includeAllDependencies;

    public MyBatisGeneratorMojo() {
        myBatisGeneratorMojo = this;
    }

    public static MyBatisGeneratorMojo getInstance(){
        return myBatisGeneratorMojo;
    }

    @Override
    public void execute() throws MojoExecutionException {
        if (this.skip) {
            this.getLog().info("MyBatis generator is skipped.");
        } else {
            this.saveClassLoader();
            LogFactory.setLogFactory(new MavenLogFactory(this));
            this.calculateClassPath();
            List<Resource> resources = this.project.getResources();
            List<String> resourceDirectories = new ArrayList();
            Iterator var3 = resources.iterator();

            while(var3.hasNext()) {
                Resource resource = (Resource)var3.next();
                resourceDirectories.add(resource.getDirectory());
            }

            ClassLoader cl = ClassloaderUtility.getCustomClassloader(resourceDirectories);
            ObjectFactory.addExternalClassLoader(cl);
            if (this.configurationFile == null) {
                throw new MojoExecutionException(Messages.getString("RuntimeError.0"));
            } else {
                List<String> warnings = new ArrayList();
                if (!this.configurationFile.exists()) {
                    throw new MojoExecutionException(Messages.getString("RuntimeError.1", this.configurationFile.toString()));
                } else {
                    this.runScriptIfNecessary();
                    Set<String> fullyqualifiedTables = new HashSet();
                    if (StringUtility.stringHasValue(this.tableNames)) {
                        StringTokenizer st = new StringTokenizer(this.tableNames, ",");

                        while(st.hasMoreTokens()) {
                            String s = st.nextToken().trim();
                            if (s.length() > 0) {
                                fullyqualifiedTables.add(s);
                            }
                        }
                    }

                    Set<String> contextsToRun = new HashSet();
                    String error;
                    if (StringUtility.stringHasValue(this.contexts)) {
                        StringTokenizer st = new StringTokenizer(this.contexts, ",");

                        while(st.hasMoreTokens()) {
                            error = st.nextToken().trim();
                            if (error.length() > 0) {
                                contextsToRun.add(error);
                            }
                        }
                    }

                    error = null;
                    Iterator var22;
                    try {
                        ConfigurationParser cp = new ConfigurationParser(this.project.getProperties(), warnings);
                        Configuration config = cp.parseConfiguration(this.configurationFile);
                        ShellCallback callback = new MavenShellCallback(this, this.overwrite);
                        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
                        myBatisGenerator.generate(new MavenProgressCallback(this.getLog(), this.verbose), contextsToRun, fullyqualifiedTables);
                    } catch (XMLParserException var11) {
                        var22 = var11.getErrors().iterator();

                        while(var22.hasNext()) {
                            error = (String)var22.next();
                            this.getLog().error(error);
                        }

                        throw new MojoExecutionException(var11.getMessage());
                    } catch (SQLException var12) {
                        throw new MojoExecutionException(var12.getMessage());
                    } catch (IOException var13) {
                        throw new MojoExecutionException(var13.getMessage());
                    } catch (InvalidConfigurationException var14) {
                        var22 = var14.getErrors().iterator();

                        while(var22.hasNext()) {
                            error = (String)var22.next();
                            this.getLog().error(error);
                        }

                        throw new MojoExecutionException(var14.getMessage());
                    } catch (InterruptedException var15) {
                        ;
                    }

                    Iterator var21 = warnings.iterator();

                    while(var21.hasNext()) {
                        error = (String)var21.next();
                        this.getLog().warn(error);
                    }

                    if (this.project != null && this.outputDirectory != null && this.outputDirectory.exists()) {
                        this.project.addCompileSourceRoot(this.outputDirectory.getAbsolutePath());
                        Resource resource = new Resource();
                        resource.setDirectory(this.outputDirectory.getAbsolutePath());
                        resource.addInclude("**/*.xml");
                        this.project.addResource(resource);
                    }

                    this.restoreClassLoader();
                }
            }
        }
    }

    private void calculateClassPath() throws MojoExecutionException {
        if (this.includeCompileDependencies || this.includeAllDependencies) {
            try {
                Set<String> entries = new HashSet();
                if (this.includeCompileDependencies) {
                    entries.addAll(this.project.getCompileClasspathElements());
                }

                if (this.includeAllDependencies) {
                    entries.addAll(this.project.getTestClasspathElements());
                }

                entries.remove(this.project.getBuild().getOutputDirectory());
                entries.remove(this.project.getBuild().getTestOutputDirectory());
                ClassLoader contextClassLoader = ClassloaderUtility.getCustomClassloader(entries);
                Thread.currentThread().setContextClassLoader(contextClassLoader);
            } catch (DependencyResolutionRequiredException var3) {
                throw new MojoExecutionException("Dependency Resolution Required", var3);
            }
        }

    }

    private void runScriptIfNecessary() throws MojoExecutionException {
        if (this.sqlScript != null) {
            SqlScriptRunner scriptRunner = new SqlScriptRunner(this.sqlScript, this.jdbcDriver, this.jdbcURL, this.jdbcUserId, this.jdbcPassword);
            scriptRunner.setLog(this.getLog());
            scriptRunner.executeScript();
        }
    }

    public File getOutputDirectory() {
        return this.outputDirectory;
    }

    private void saveClassLoader() {
        this.savedClassloader.set(Thread.currentThread().getContextClassLoader());
    }

    private void restoreClassLoader() {
        Thread.currentThread().setContextClassLoader((ClassLoader)this.savedClassloader.get());
    }
}
