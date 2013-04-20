package appliednlp.gpp.app

import appliednlp.gpp.classify.LabeledTweet


/**
 * A standalone object with a main method for converting the emoticon tweets
 * into an XML format.
 */
object ConvertEmoticon {
  import scala.xml.PrettyPrinter

  def main(args: Array[String]) {
    val emoticonDir = args(0)

    val fileInfo = Seq(("happy", "positive"), ("sad", "negative"), ("neutral", "neutral"))
    val tweets = fileInfo.flatMap {
      case (filename, polarity) => readFile(emoticonDir + "/" + filename + ".txt", polarity)
    }

    val dataset =
      <dataset>
        {tweets.map(_.toXML)}
      </dataset>

    println("<?xml version=\"1.0\"?>")
    println(new PrettyPrinter(1000, 2).format(dataset))
  }

  def readFile(path: String, polarity: String): Iterator[LabeledTweet] = {
    io.Source.fromFile(path).getLines.map { line =>
      line.split("\t", 3) match {
        case Array(id, user, text) => {
          LabeledTweet(polarity, "unknown", id, user, text)
        }
      }
    }
  }
}
