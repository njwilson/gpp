package appliednlp.gpp.app


case class LabeledTweet(
    label: String,
    target: String,
    tweetid: String,
    username: String,
    content: String) {
  def toXML =
    <item label={label} target={target} tweetid={tweetid} username={username}>
      <content>{content}</content>
    </item>
}

/**
 * A standalone object with a main method for converting the Stanford tweets
 * into an XML format.
 */
object ConvertStanford {
  import scala.xml.PrettyPrinter

  val polarityMap = Map("0" -> "negative", "2" -> "neutral", "4" -> "positive")

  def main(args: Array[String]) {
    val tweets = io.Source.fromFile(args(0)).getLines.map { line =>
      line.split(";;") match {
        case Array(polarity, id, _, query, user, text) => {
          LabeledTweet(polarityMap(polarity), query, id, user, text)
        }
      }
    }

    val dataset =
      <dataset>
        {tweets.map(_.toXML)}
      </dataset>

    println("<?xml version=\"1.0\"?>")
    println(new PrettyPrinter(1000, 2).format(dataset))
  }
}
