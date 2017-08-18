name := """play3"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

//libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % "test"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"

libraryDependencies +="com.typesafe.play" %% "play-slick" % "2.0.0"

libraryDependencies += "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0"

libraryDependencies +="org.postgresql" % "postgresql" % "42.1.4"

libraryDependencies += "org.mockito" % "mockito-all" % "1.9.5" % "test"

libraryDependencies += "com.h2database" % "h2" % "1.4.188"

libraryDependencies += evolutions

javaOptions in Test += "-Dconfig.file=conf/test.conf"

libraryDependencies += specs2 % Test