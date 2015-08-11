// modeled this build.sbt off of http://www.typesafe.com/activator/template/scala-library-seed#code/build.sbt

name := "query"

version := "1.0.1"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test"
)

// bintray stuff
licenses += ("MIT", url("http://opensource.org/licenses/MIT"))
organization := "com.github.davidkellis"
