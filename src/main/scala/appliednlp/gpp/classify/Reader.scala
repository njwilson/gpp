package appliednlp.gpp.classify

import scala.xml._


/**
 * A tweet.
 */
case class Tweet(id: String, username: String, content: String)

/**
 * The sentiment label for a tweet.
 */
case class TweetSentiment(label: String, target: String)

/**
 * Takes a filename and returns a list of TweetSentiment objects from the file.
 */
trait TweetSentimentReader extends (String => Iterator[(Tweet, TweetSentiment)])

/**
 * Produces TweetSentiment objects from an XML file.
 */
object XMLTweetSentimentReader extends TweetSentimentReader {
  def apply(filename: String): Iterator[(Tweet, TweetSentiment)] = {
    val items = (scala.xml.XML.loadFile(filename) \\ "item").toIterator
    for (tweetXml <- items)
      yield createTweetSentiment(tweetXml)
  }

  private def createTweetSentiment(tweetXml: NodeSeq) = {
    val tweet = Tweet(
      (tweetXml \ "@tweetid").text,
      (tweetXml \ "@username").text,
      (tweetXml \ "content").text)
    val tweetSentiment = new TweetSentiment(
      (tweetXml \ "@label").text,
      (tweetXml \ "@target").text)
    (tweet, tweetSentiment)
  }
}
