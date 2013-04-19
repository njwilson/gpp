package appliednlp.gpp.classify

import chalk.lang.eng.Twokenize
import nak.data.Example


/*
 * Takes the tweet content and turns it into a class label.
 */
trait SimpleTweetClassifier extends (String => String) {
  def apply(content: String): String
}

/*
 * Always predicts the majority class label from the training data.
 */
class MajorityPolarityClassifier(trainExamples: Iterator[Example[String, String]])
extends SimpleTweetClassifier {
  val majorityLabel = {
    val labels = trainExamples.map(_.label)
    labels.toSeq.groupBy(identity).maxBy(_._2.size)._1
  }

  def apply(content: String): String = majorityLabel
}

/*
 * Simple sentiment classifier based on counts of positive/negative words.
 */
object LexiconPolarityClassifier extends SimpleTweetClassifier {
  import appliednlp.gpp.util.Polarity

  def apply(content: String): String = {
    val tokens = Twokenize(content)
    val numPos = tokens.count(Polarity.posWords.contains(_))
    val numNeg = tokens.count(Polarity.negWords.contains(_))
    if (numPos == numNeg) {
      "neutral"
    } else {
      if (numPos > numNeg) "positive" else "negative"
    }
  }
}
