organization  := "de.guderlei"

version       := "0.3"

scalaVersion  := "2.10.3"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers ++= Seq(
  "spray repo"         at "http://repo.spray.io/",
  "sonatype releases"  at "http://oss.sonatype.org/content/repositories/releases/",
  "sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
  "typesafe repo"      at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
  "io.spray"            %   "spray-can"         % "1.2.0",
  "io.spray"            %   "spray-routing"     % "1.2.0",
  "io.spray"            %   "spray-testkit"     % "1.2.0",
  "io.spray"            %%  "spray-json"        % "1.2.5",
  "com.typesafe.akka"   %%  "akka-actor"        % "2.2.3",
  "org.specs2"          %%  "specs2"            % "2.3.4"      % "test",
  "org.squeryl"         %%  "squeryl"           % "0.9.5-6",
  "com.h2database"      %   "h2"                % "1.3.174",
  "com.jolbox"          %   "bonecp"            % "0.8.0.RELEASE",
  "mysql"               % "mysql-connector-java" % "5.1.27",
  "org.slf4j"           %   "slf4j-simple"      % "1.7.2"
)

seq(Revolver.settings: _*)
