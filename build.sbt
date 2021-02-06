name := "corenlp-small"

version := "1.0-SNAPSHOT"

organization := "org.clulab"

scalaVersion := "2.12.6"
crossScalaVersions := Seq("2.11.11", "2.12.6")

javacOptions ++= Seq("-encoding", "UTF-8")

scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation")

publishArtifact in packageDoc := false

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",

  "de.jollyday" % "jollyday" % "0.4.9",
  "joda-time" % "joda-time" % "2.9.4",
  "org.apache.commons" % "commons-lang3" % "3.3.1",
  "xom" % "xom" % "1.3.2",
  "org.ejml" % "ejml-core" % "0.39",
  "org.ejml" % "ejml-ddense" % "0.39",
  "org.ejml" % "ejml-simple" % "0.39",
  "org.glassfish" % "javax.json" % "1.0.4",
  "com.google.protobuf" % "protobuf-java" % "3.9.2"
)
