ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.7"

val CirceVersion = "0.14.6"
val Http4sVersion = "0.23.23"

lazy val root = (project in file("."))
  .settings(
    name := "Notifier",

    libraryDependencies ++= Seq(
      "com.github.fd4s" %% "fs2-kafka" % "3.2.0",
      "is.cir"          %% "ciris"     % "3.5.0",
      "io.circe"        %% "circe-generic" % CirceVersion,
      "io.circe"        %% "circe-parser"  % CirceVersion,
      "org.typelevel"   %% "log4cats-slf4j" % "2.6.0",

      // logging
      "org.typelevel" %% "log4cats-slf4j" % "2.6.0",
      "ch.qos.logback" % "logback-classic" % "1.5.13",
      "net.logstash.logback" % "logstash-logback-encoder" % "7.4"

    )
  )
