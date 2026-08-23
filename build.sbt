scalaVersion := "3.8.4"

lazy val root = rootProject
  .settings(
    name := "parabellum",
    idePackagePrefix := Some("it.unibo.parabellum"),
    libraryDependencies ++= Seq(
      "org.scalafx" %% "scalafx" % "20.0.0-R31",
      "com.lihaoyi" %% "fastparse" % "3.1.1",
      "org.scalatest" %% "scalatest-flatspec" % "3.2.20" % "test"
      // You can add library dependencies here, for example,
      //"org.scalatest" %% "scalatest" % "3.2.19" % Test,
      //"org.scalameta" %% "munit" % "1.2.3" % Test
    )
  )

lazy val app = (project in file ("parabellum"))
    .settings(
        assembly / assemblyJarName := "Parabellum.jar",
        assembly / mainClass := Some("it.unibo.parabellum.Parabellum"),
        assembly / assemblyMergeStrategy := {
            case "module-info.class" => MergeStrategy.discard
            case PathList("META-INF", "substrate", "config", _*) => MergeStrategy.first
            case PathList("META-INF", _*) => MergeStrategy.discard
            case "reference.conf"         => MergeStrategy.concat
            case _                        => MergeStrategy.first
        }
    )