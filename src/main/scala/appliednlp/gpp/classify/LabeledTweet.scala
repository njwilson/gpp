package appliednlp.gpp.classify

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
