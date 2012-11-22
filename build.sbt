organization  := "com.guderlei"

version       := "0.1"

scalaVersion  := "2.10.0-RC2"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers ++= Seq(
  "spray repo"         at "http://repo.spray.io/",
  "sonatype releases"  at "http://oss.sonatype.org/content/repositories/releases/",
  "sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
  "typesafe repo"      at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
  "io.spray"            %   "spray-can"     % "1.1-M5",
  "io.spray"            %   "spray-routing" % "1.1-M5",
  "io.spray"            %   "spray-testkit" % "1.1-M5",
  "com.typesafe.akka"   %%  "akka-actor"    % "2.1.0-RC2"          cross CrossVersion.full,
  "org.specs2"          %%  "specs2"        % "1.12.2"    % "test" cross CrossVersion.full,
  "org.squeryl" % "squeryl_2.10.0-RC2" % "0.9.5-4",
  "com.h2database" % "h2" % "1.3.168"
)

seq(Revolver.settings: _*)
