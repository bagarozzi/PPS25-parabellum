scalaVersion := "3.8.4"

ThisBuild / assemblyMergeStrategy := {
    case path if path.contains("module-info.class") => MergeStrategy.discard
    case path if path.contains("META-INF/substrate/config") => MergeStrategy.discard
    case x => MergeStrategy.defaultMergeStrategy(x)
}

lazy val javaFXModules = Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
lazy val javaFxVersion = "21.0.1"

lazy val root = rootProject
    .settings(
        name := "parabellum",
        idePackagePrefix := Some("it.unibo.parabellum"),

        assembly / assemblyJarName := "Parabellum.jar",
        assembly / mainClass := Some("it.unibo.parabellum.main"),

        libraryDependencies ++= Seq(
            "org.scalafx" %% "scalafx" % "20.0.0-R31",
            "com.lihaoyi" %% "fastparse" % "3.1.1",
            "org.scalatest" %% "scalatest" % "3.2.20" % "test",
            // You can add library dependencies here, for example,
            //"org.scalatest" %% "scalatest" % "3.2.19" % Test,
            //"org.scalameta" %% "munit" % "1.2.3" % Test
        ) ++ javaFXModules.flatMap(m =>
                Seq(
                    "org.openjfx" % s"javafx-$m" % javaFxVersion classifier "win",
                    "org.openjfx" % s"javafx-$m" % javaFxVersion classifier "mac",
                    "org.openjfx" % s"javafx-$m" % javaFxVersion classifier "mac-aarch64",
                    "org.openjfx" % s"javafx-$m" % javaFxVersion classifier "linux"
                )
            ),
        scalacOptions += "-Wconf:msg=Implicit parameters should be provided with a `using` clause:s"
    )
