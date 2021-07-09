import sbt._
import sbt.Keys._
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._

object Dependencies {

  object Ver {

    // Exported
    val scala2          = "2.13.6"
    val scala3          = "3.0.1"
    val scalaCollCompat = "2.4.4"
    val scalaz          = "7.2.32"
    val sourceCode      = "0.2.7"
    val univEq          = "1.4.0"

    // Internal
    val jamm            = "0.3.3"
    val kindProjector   = "0.13.0"
    val nyaya           = "0.10.0"
    val scalaJsJavaTime = "1.0.0"
    val utest           = "0.7.10"
  }

  object Dep {
    val jamm            = Def.setting("com.github.jbellis"              % "jamm"                    % Ver.jamm)
    val nyayaGen        = Def.setting("com.github.japgolly.nyaya"     %%% "nyaya-gen"               % Ver.nyaya)
    val nyayaProp       = Def.setting("com.github.japgolly.nyaya"     %%% "nyaya-prop"              % Ver.nyaya)
    val nyayaTest       = Def.setting("com.github.japgolly.nyaya"     %%% "nyaya-test"              % Ver.nyaya)
    val scalaCollCompat = Def.setting("org.scala-lang.modules"        %%% "scala-collection-compat" % Ver.scalaCollCompat)
    val scalaCompiler   = Def.setting("org.scala-lang"                  % "scala-compiler"          % scalaVersion.value)
    val scalaJsJavaTime = Def.setting("org.scala-js"                  %%% "scalajs-java-time"       % Ver.scalaJsJavaTime cross CrossVersion.for3Use2_13)
    val scalaz          = Def.setting("org.scalaz"                    %%% "scalaz-core"             % Ver.scalaz cross CrossVersion.for3Use2_13)
    val sourceCode      = Def.setting("com.lihaoyi"                   %%% "sourcecode"              % Ver.sourceCode)
    val univEq          = Def.setting("com.github.japgolly.univeq"    %%% "univeq"                  % Ver.univEq)
    val univEqScalaz    = Def.setting("com.github.japgolly.univeq"    %%% "univeq-scalaz"           % Ver.univEq)
    val utest           = Def.setting("com.lihaoyi"                   %%% "utest"                   % Ver.utest)

    // Compiler plugins
    val kindProjector = compilerPlugin("org.typelevel" %% "kind-projector" % Ver.kindProjector cross CrossVersion.full)
  }

}
