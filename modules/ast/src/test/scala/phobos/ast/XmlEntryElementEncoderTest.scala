package phobos.ast

import com.softwaremill.diffx.scalatest.DiffShouldMatcher
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import phobos.Namespace
import phobos.encoding.XmlEncoder

class XmlEntryElementEncoderTest extends AnyWordSpec with DiffShouldMatcher with Matchers {
  "XmlEntry encoder" should {
    "encodes simple Xml ast correctly" in {
      val ast = xml(attr("foo") := 5, node("bar") := "bazz")

      val result =
        XmlEncoder
          .fromElementEncoder[XmlEntry]("ast")
          .encode(ast)

      assert(result == Right("""<?xml version='1.0' encoding='UTF-8'?><ast foo="5"><bar>bazz</bar></ast>"""))
    }
    "encodes nested Xml ast correctly" in {
      case object example {
        type ns = example.type
        implicit val ns: Namespace[example.type] = Namespace.mkInstance("https://example.org")
      }

      val ast = xml(attr("foo") := 5)(
        node("bar") := "bazz",
        node("array") := xml(
          attr("foo2") := true,
          attr("foo3") := false,
        )(
          node("elem") := 11111111111111L,
          node("elem") := 11111111111112L,
        ),
        node("nested") := xml(
          node("scala")   := 2.13,
          node("dotty")   := 0.13,
          node("scala-4") := xml.empty,
        ),
      )

      val result =
        XmlEncoder
          .fromElementEncoderNs[XmlEntry, example.ns]("ast")
          .encode(ast)

      assert(
        result == Right(
          """<?xml version='1.0' encoding='UTF-8'?><ans1:ast xmlns:ans1="https://example.org" foo="5"><bar>bazz</bar><array foo2="true" foo3="false"><elem>11111111111111</elem><elem>11111111111112</elem></array><nested><scala>2.13</scala><dotty>0.13</dotty><scala-4/></nested></ans1:ast>""",
        ),
      )
    }
  }
}
