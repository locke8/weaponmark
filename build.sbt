name := "Weaponmark"

version := "1.0a"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.rogach" %% "scallop" % "2.0.2",
  "org.scalactic" %% "scalactic" % "3.0.0",
  "com.diogonunes" % "JCDP" % "2.0.1",
  // exclude below removes warning about duplicate libraries with different versions (sbt bug #1933)
  "org.scalatest" %% "scalatest" % "3.0.0" % "test" exclude("org.scala-lang.modules", "scala-xml_2.11")
)

lazy val root = (project in file(".")).
  enablePlugins(BuildInfoPlugin).
  settings(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "weaponmark"
  )
