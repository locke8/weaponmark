logLevel := Level.Warn

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.3")
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.6.1")
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.1.4")
// disabled until/unless config set to ignore problems from scallops use of reflection
// addSbtPlugin("com.artima.supersafe" % "sbtplugin" % "1.1.0")

// util to produce dependency graphs s/b moved to global folder of .sbt
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.8.2")

// used for documentation generation
//addSbtPlugin("com.lihaoyi" % "scalatex-sbt-plugin" % "0.3.7")
