ThisBuild / version := "0.1"
ThisBuild / scalaVersion := "2.12.10"

lazy val root = (project in file("."))
  .settings(
    name := "MyChiselProject",
    libraryDependencies ++= Seq(
      "edu.berkeley.cs" %% "chisel3" % "3.6.0", // Match Chipyard version
      "edu.berkeley.cs" %% "chiseltest" % "0.5.1"
    )
  )
