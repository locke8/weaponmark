import sbt.Keys._
import sbt._

resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"
libraryDependencies ++= Seq(
  "org.rogach" %% "scallop" % "2.0.2",
  "ch.qos.logback" %  "logback-classic" % "1.1.7",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
  "com.diogonunes" % "JCDP" % "2.0.1",
  "com.lihaoyi" %% "upickle" % "0.4.3", // % "test",
  "com.lihaoyi" %% "ammonite-ops" % "0.8.0", // % "test",
  "org.scalactic" %% "scalactic" % "3.0.0",
  "org.scalatest" %% "scalatest" % "3.0.0" % "test"
)

name := "weaponmark"
version := "1.0" //-BETA"
scalaVersion in ThisBuild := "2.11.8"
maintainer := "Paul L. Bryan"
packageSummary := "Attack benchmarking tool for Mage: The Ascension (2nd Edition, WW4600)"
packageDescription := "Attack benchmark tool for Mage: The Ascension"
//mainClass in Compile := Some("WeaponMark")
assemblyJarName in assembly := "weaponmark-assembly.jar"
assemblyOutputPath in assembly := baseDirectory.value / ("target/universal/jdkpackager/lib/" + (assemblyJarName in assembly).value)
// remove "app" directory from root, sbt-native-packager:
// http://sbt-native-packager.readthedocs.io/en/latest/formats/universal.html#universal-plugin
//topLevelDirectory := None

scalacOptions ++= List(
// "-unchecked",
//  "-Xdisable-assertions",
  "-language:_",
  "-encoding", "UTF-8",
  "-deprecation",       // warns on use of deprecated api's
  "-Xfatal-warnings",   // compile fails if any warnings occur - keep code clean
  "-Xfuture",           // turn on future language features
  "-feature"            // provide warning info about misused language features
)

// for sbt-dependency-graph
filterScalaLibrary := true

javaOptions in Universal ++= Seq(
  //"-java-home C:/Program Files/Java/jre1.8.0_111/bin"
)

lazy val iconGlob = sys.props("os.name").toLowerCase match {
  case os if os.contains("mac") ⇒ "*.icns"
  case os if os.contains("win") ⇒ "*.ico"
  case _ ⇒ "*.png"
}
jdkAppIcon := (sourceDirectory.value ** iconGlob).getPaths.headOption.map(file)
jdkPackagerType := "exe" //"installer"

// removes all jar mappings in universal and appends the fat jar
/*
mappings in Universal := {
  // universalMappings: Seq[(File,String)]
  val universalMappings = (mappings in Universal).value
  val fatJar = (assembly in Compile).value
  // removing means filtering
  val filtered = universalMappings filter {
    case (file, name) =>  !name.endsWith(".jar") && !name.contains("launcher")
  }
  // add the fat jar
  filtered :+ (fatJar -> ("lib/" + fatJar.getName))
}
*/

lazy val root = (project in file(".")).
  enablePlugins(JDKPackagerPlugin).
  enablePlugins(BuildInfoPlugin).
  settings(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "weaponmark"
  )

// ScalaText documentation generation
scalatex.SbtPlugin.projectSettings

lazy val readme = scalatex.ScalatexReadme(
  projectId = "readme",
  wd = file(""),
  url = "https://github.com/locke8/weaponmark/tree/master",
  source = "Readme"
).settings(
  scalaVersion := "2.11.8"
)

/*
lazy val readme = scalatex.ScalatexReadme(
  projectId = "readme",
  wd = file(""),
  url = "https://github.com/lihaoyi/scalatags/tree/master",
  source = "Readme",
  autoResources = Seq("Autocomplete.png", "ErrorHighlighting.png", "InlineDocs.png", "example-opt.js")
).settings(
  scalaVersion := "2.11.8",
  (unmanagedSources in Compile) += baseDirectory.value/".."/"project"/"Constants.scala",
  (resources in Compile) += (fullOptJS in (example, Compile)).value.data,
  (resources in Compile) += (doc in (scalatagsJS, Compile)).value,
  (run in Compile) := (run in Compile).dependsOn(Def.task{
    sbt.IO.copyDirectory(
      (doc in (scalatagsJS, Compile)).value,
      (target in Compile).value/"scalatex"/"api",
      overwrite = true
    )
  }).evaluated
)
*/