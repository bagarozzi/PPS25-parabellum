ThisBuild / scalaVersion := "3.8.4"

ThisBuild / assemblyMergeStrategy := {
    case path if path.contains("module-info.class") => MergeStrategy.discard
    case path if path.contains("META-INF/substrate/config") => MergeStrategy.discard
    case path if path.endsWith(".class") => MergeStrategy.first
    case path if path.contains("META-INF/substrate/config") => MergeStrategy.discard
    case path if path.endsWith(".class") => MergeStrategy.first
    case path if path.endsWith(".bss") => MergeStrategy.first
    case path if path.endsWith(".dylib") => MergeStrategy.first
    case path if path.endsWith(".so") => MergeStrategy.first
    case path if path.endsWith(".dll") => MergeStrategy.first
    case path if path.contains("javafx-swt.jar") => MergeStrategy.first
    case x => MergeStrategy.defaultMergeStrategy(x)
}

lazy val javaFXModules = Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
lazy val javaFxVersion = "21.0.1"
lazy val pekkoVersion = "1.7.0"
lazy val pekkoHttpVersion = "1.4.0"
lazy val pekkoPersistenceJdbcVersion = "1.3.0"

// Settings shared by all the modules
lazy val commonSettings: Seq[Def.Setting[?]] = Seq(
    idePackagePrefix := Some("it.unibo.parabellum"),
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.20" % "test",
    scalacOptions += "-Wconf:msg=Implicit parameters should be provided with a `using` clause:s"
)

// Game model and rules: no user interface, so it can run anywhere (client and server)
lazy val core = project.in(file("core"))
    .settings(commonSettings)
    .settings(
        name := "parabellum-core",
        libraryDependencies ++= Seq(
            "com.lihaoyi" %% "fastparse" % "3.1.1",
            "com.lihaoyi" %% "upickle" % "4.4.2",
            "it.unibo.alice.tuprolog" % "tuprolog" % "3.3.0"
        )
    )

// ScalaFX user interface, local game loop and entry point of the application
lazy val client = project.in(file("client"))
    .dependsOn(core)
    .settings(commonSettings)
    .settings(
        name := "parabellum",

        assembly / assemblyJarName := "Parabellum.jar",
        assembly / mainClass := Some("it.unibo.parabellum.main"),

        libraryDependencies ++= Seq(
            "org.scalafx" %% "scalafx" % "20.0.0-R31"
        ) ++ javaFXModules.flatMap(m =>
                Seq(
                    "org.openjfx" % s"javafx-$m" % javaFxVersion classifier "win",
                    "org.openjfx" % s"javafx-$m" % javaFxVersion classifier "mac",
                    "org.openjfx" % s"javafx-$m" % javaFxVersion classifier "mac-aarch64",
                    "org.openjfx" % s"javafx-$m" % javaFxVersion classifier "linux"
                )
            )
    )

// Headless server (work in progress): it hosts the matches in a Pekko cluster, using only `core`
lazy val server = project.in(file("server"))
    .dependsOn(core)
    .settings(commonSettings)
    .settings(
        name := "parabellum-server",

        // Pekko runs better in its own JVM, and sbt can pass the program arguments (the ports of the nodes)
        run / fork := true,

        libraryDependencies ++= Seq(
            "org.apache.pekko" %% "pekko-actor-typed" % pekkoVersion,
            "org.apache.pekko" %% "pekko-cluster-typed" % pekkoVersion,
            "org.apache.pekko" %% "pekko-cluster-sharding-typed" % pekkoVersion,
            "org.apache.pekko" %% "pekko-serialization-jackson" % pekkoVersion,
            "org.apache.pekko" %% "pekko-slf4j" % pekkoVersion,
            "org.apache.pekko" %% "pekko-stream" % pekkoVersion,
            "org.apache.pekko" %% "pekko-stream-typed" % pekkoVersion,
            "org.apache.pekko" %% "pekko-http" % pekkoHttpVersion,
            "org.apache.pekko" %% "pekko-persistence-typed" % pekkoVersion,
            "org.apache.pekko" %% "pekko-persistence-jdbc" % pekkoPersistenceJdbcVersion,
            "com.h2database" % "h2" % "2.2.224",
            "org.postgresql" % "postgresql" % "42.7.4",
            "com.auth0" % "java-jwt" % "4.4.0",
            "ch.qos.logback" % "logback-classic" % "1.5.18",
            "org.apache.pekko" %% "pekko-actor-testkit-typed" % pekkoVersion % "test",
            "org.apache.pekko" %% "pekko-stream-testkit" % pekkoVersion % "test",
            "org.apache.pekko" %% "pekko-persistence-testkit" % pekkoVersion % "test",
            "org.apache.pekko" %% "pekko-http-testkit" % pekkoHttpVersion % "test"
        )
    )

lazy val root = project.in(file("."))
    .aggregate(core, client, server)
    .settings(
        name := "parabellum-root"
    )
