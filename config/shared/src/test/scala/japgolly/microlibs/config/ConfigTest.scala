package japgolly.microlibs.config

import japgolly.microlibs.testutil.TestUtil._
import scalaz.{-\/, \/-}
import scalaz.std.AllInstances._
import scalaz.syntax.applicative._
import scalaz.Scalaz.Id
import utest._
import ValueReader.Y._
import ValueReader.N._

object ConfigTest extends TestSuite {

  implicit def equalResultX[A] = scalaz.Equal.equalA[ResultX[A]]

  val src1 = Source.manual[Id]("S1")("i" -> "3", "s" -> "hey")
  val src2 = Source.manual[Id]("S2")("i" -> "X300", "i2" -> "22", "s2" -> "ah")

  val srcs: Sources[Id] =
     src1 > src2

  val srcE = Source.point[Id]("SE", new ConfigStore[Id] {
    def apply(key: Key) = ConfigValue.Error("This source is fake!", None)
  })

  implicit class ResultXExt[A](private val self: ResultX[A]) extends AnyVal {
    def get_! : A = self match {
      case ResultX.Success(a) => a
      case x => fail(s"Expected success, got: $x")
    }
  }

  override def tests = TestSuite {

    'findFirst -
      assertEq(Config.need[String]("s").run(srcs), ResultX.Success("hey"))

    'findSecond -
      assertEq(Config.need[Int]("i2").run(srcs), ResultX.Success(22))

    'notFound -
      assertEq(Config.get[Int]("notfound").run(srcs), ResultX.Success(Option.empty[Int]))

    'missing1 -
      assertEq(Config.need[Int]("missing").run(srcs), ResultX.QueryFailure(Map(Key("missing") -> None)))

    'missing2 -
      assertEq(
        (Config.need[Int]("no1") tuple Config.need[Int]("no2")).run(srcs),
        ResultX.QueryFailure(Map(Key("no1") -> None, Key("no2") -> None)))

    'valueFail1 -
      assertEq(
        Config.need[Int]("s").run(srcs),
        ResultX.QueryFailure(Map(Key("s") -> Some((src1.name, ConfigValue.Error("Int expected.", Some("hey")))))))

    'valueFail2 -
      assertEq(
        Config.need[Int]("s2").run(srcs),
        ResultX.QueryFailure(Map(Key("s2") -> Some((src2.name, ConfigValue.Error("Int expected.", Some("ah")))))))

    'errorMsg {
      'notFound - assertEq(Config.need[Int]("QQ").run(srcs).toDisjunction, -\/(
        """
          |1 error:
          |  - No value for key [QQ]
        """.stripMargin.trim))

      'notFound2 - {
        val c = Config.need[Int]("QQ") tuple Config.get[Int]("X") tuple Config.need[Int]("i") tuple Config.need[Int]("M")
        assertEq(c.run(srcs).toDisjunction, -\/(
          """
            |2 errors:
            |  - No value for key [M]
            |  - No value for key [QQ]
          """.stripMargin.trim))
      }

      'errors2 - {
        val c = Config.need[Int]("s") tuple Config.get[Int]("X")
        assertEq(c.run(srcs > srcE).toDisjunction, -\/(
          """
            |2 errors:
            |  - Error reading key [s] from source [S1] with value [hey]: Int expected.
            |  - Error reading key [X] from source [SE]: This source is fake!
          """.stripMargin.trim))
      }
    }

    'report {
      val si: Config[(String, Int)] = Config.need[String]("s") tuple Config.need[Int]("i")
      val expectedReport =
        s"""
           |+-----+-----+------+
           || Key | S1  | S2   |
           |+-----+-----+------+
           || i   | 3   | X300 |
           || s   | hey |      |
           |+-----+-----+------+
         """.stripMargin.trim + "\n"
      "*>" - {
        val k: KeyReport = (si *> Config.keyReport).run(srcs).get_!
        assertEq(k.report, expectedReport)
      }
      "*> <*" - {
        val k: KeyReport = (si *> Config.keyReport <* Config.need[Int]("i2")).run(srcs).get_!
        assertEq(k.report, expectedReport)
      }
      'with {
        val (_, k: KeyReport) = si.withKeyReport.run(srcs).get_!
        assertEq(k.report, expectedReport)
      }

    }
  }
}
