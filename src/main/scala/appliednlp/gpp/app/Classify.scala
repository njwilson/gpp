package appliednlp.gpp.app

import nak.NakContext
import nak.core.FeaturizedClassifier
import nak.data.{Featurizer, BowFeaturizer, Example}
import nak.liblinear
import nak.util.ConfusionMatrix

import appliednlp.gpp.classify._
import appliednlp.gpp.util.English


/**
 * A standalone object with a main method for doing classification experiments.
 */
object Classify {
  def main(args: Array[String]) {
    // Parse and get the command-line options
    val opts = ClassifyOpts(args)

    // Read the datasets
    val trainExamples = readExamples(opts.train())
    val evalExamples = readExamples(opts.eval())

    // Train the classifier
    val method = opts.method()
    val classifier = method match {
      case "majority" => new MajorityPolarityClassifier(trainExamples)
      case "lexicon" => LexiconPolarityClassifier
      case _ => {   // Assume Liblinear solver
        // Construct appropriate featurizer
        val featurizer = {
          if (opts.extended()) {
            ExtendedFeaturizer
          } else {
            new BowFeaturizer(English.stopwords)
          }
        }

        // Configure and train
        val solverType = liblinear.Solver(method)
        val config = liblinear.LiblinearConfig(
          solverType = solverType,
          cost = opts.cost(),
          showDebug = opts.verbose())
        NakContext.trainClassifier(config, featurizer, trainExamples.toList)
      }
    }

    // Predict the evaluation data
    val comparisons = {
      classifier match {
        case simpleClf: SimpleTweetClassifier => {
          for (ex <- evalExamples)
            yield (ex.label, simpleClf(ex.features), ex.features)
        }
        case nakClf: FeaturizedClassifier[String, String] => {
          val maxLabel = NakContext.maxLabel(nakClf.labels) _
          for (ex <- evalExamples)
            yield (ex.label, maxLabel(nakClf.evalRaw(ex.features)), ex.features)
        }
      }
    }

    // Print the confusion matrix
    val (goldLabels, predictions, inputs) = comparisons.toSeq.unzip3
    println(ConfusionMatrix(goldLabels, predictions, inputs))
  }

  def readExamples(filenames: List[String]): Iterator[Example[String, String]] = {
    val tweetSentiments = filenames.toIterator.flatMap { filename =>
      XMLTweetSentimentReader(filename)
    }
    tweetSentiments.map {
      case (tweet, sentiment) => Example(sentiment.label, tweet.content, tweet.id)
    }
  }
}


/**
 * An object that sets of the configuration for command-line options using
 * Scallop and returns the options, ready for use.
 */
object ClassifyOpts {
  import org.rogach.scallop._

  def apply(args: Array[String]) = new ScallopConf(args) {
    banner("""
Classification application.

For usage see below:
""")

    val cost = opt[Double](
      "cost",
      short = 'c',
      default = Some(1.0),
      descr = "The cost parameter C. Bigger values means less regularization (more fidelity to the training set).")
    val detailed = opt[Boolean](
      "detailed",
      short = 'd')
    val eval = opt[List[String]](
      "eval",
      short = 'e',
      descr = "The files containing evalualation events.")
    val extended = opt[Boolean](
      "extended",
      short = 'x',
      descr = "Use extended features.")
    val method = opt[String](
      "method",
      short = 'm',
      default = Some("L2R_LR"),
      descr = "The type of solver to use. Possible values: majority, lexicon, or any liblinear solver type.")
    val train = opt[List[String]](
      "train",
      short = 't',
      descr = "The files containing training events.")
    val help = opt[Boolean](
      "help",
      noshort = true,
      descr = "Show this message")
    val verbose = opt[Boolean](
      "verbose",
      short = 'v')
    val version = opt[Boolean](
      "version",
      noshort = true,
      descr = "Show version of this program")
  }
}
