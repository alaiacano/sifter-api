name := "sifter-api"

version := "0.1"

scalaVersion := "2.12.5"

libraryDependencies ++= Seq(
  "com.twitter" %% "finatra-http" % "18.4.0",
  "sifter-lib" %% "sifter-lib" % "0.1-SNAPSHOT",
  "ch.qos.logback" % "logback-classic" % "1.1.7",
  "com.twitter" %% "finatra-http" % "18.4.0" % "test" classifier "tests"
)
