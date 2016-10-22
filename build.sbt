import AssemblyKeys._ // put this at the top of the file

import NativePackagerKeys._

def artifactory(name: String) = s"http://bdr-web-repo.eng.solidfire.net:8081/artifactory/$name"

packageArchetype.java_server

assemblySettings

scalariformSettings

organization := "io.swagger"

seq(webSettings :_*)

mainClass in assembly := Some("JettyMain")

name := "scalatra-sample"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.2"

scalacOptions += "-language:postfixOps"

libraryDependencies ++= Seq(
  "org.scalatest"           %% "scalatest"                      % "2.2.1"               % "test",
  "org.scalatra"            %% "scalatra"                       % "2.3.0.RC3",
  "org.scalatra"            %% "scalatra-scalate"               % "2.3.0.RC3",
  "org.scalatra"            %% "scalatra-json"                  % "2.3.0.RC3",
  "org.scalatra"            %% "scalatra-slf4j"                 % "2.3.0.RC3",
  "org.json4s"              %% "json4s-jackson"                 % "3.2.10",
  "org.json4s"              %% "json4s-ext"                     % "3.2.10",
  "commons-codec"            % "commons-codec"                  % "1.7",
  "net.databinder.dispatch" %% "dispatch-core"                  % "0.11.2",
  "net.databinder.dispatch" %% "dispatch-json4s-jackson"        % "0.11.2",
  "org.eclipse.jetty"        % "jetty-server"                   % "9.2.3.v20140905" % "container;compile;test",
  "org.eclipse.jetty"        % "jetty-webapp"                   % "9.2.3.v20140905" % "container;compile;test",
  "org.eclipse.jetty.orbit"  % "javax.servlet"                  % "3.0.0.v201112011016" % "container;compile;provided;test" artifacts (Artifact("javax.servlet", "jar", "jar")),
  "com.solidfire"            % "internal-element-api"           % "8.0.32.9999-SNAPSHOT"
)

resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository"

resolvers += "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

resolvers += "Sonatype OSS Releases" at "http://oss.sonatype.org/content/repositories/releases/"

resolvers +=  "artifactory-remotes" at artifactory("ivy-snapshot-local")

ivyXML := <dependencies>
    <exclude module="slf4j-log4j12"/>
    <exclude module="grizzled-slf4j_2.9.1"/>
    <exclude module="jsr311-api" />
  </dependencies>

mergeStrategy in assembly <<= (mergeStrategy in assembly) {
  (old) => {
    case "about.html"     => MergeStrategy.discard
    case x => old(x)
  }
}

net.virtualvoid.sbt.graph.Plugin.graphSettings
