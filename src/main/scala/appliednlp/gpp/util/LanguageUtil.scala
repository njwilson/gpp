package appliednlp.gpp.util

/* Copied and modified from http://github.com/utcompling/tshrdlu/ */

/**
 * A helper object for creating Scala Source instances given a
 * the location of a resource in the classpath, which includes
 * files in the src/main/resources directory.
 */
object Resource {
  import java.util.zip.GZIPInputStream
  import java.io.DataInputStream

  /**
   * Read in a file as a Source, ensuring that the right thing
   * is done for gzipped files.
   */
  def asSource(location: String) = {
    val stream = this.getClass.getResourceAsStream(location)
    if (location.endsWith(".gz"))
      io.Source.fromInputStream(new GZIPInputStream(stream))
    else
      io.Source.fromInputStream(stream)
  }

  def asStream(location: String) = {
    val stream = this.getClass.getResourceAsStream(location)
    val stream2 = if (location.endsWith(".gz")) new GZIPInputStream(stream) else stream
    new DataInputStream(stream2)
  }
}

/**
 * A parent class for specific languages. Handles some common
 * functions.
 */
abstract class Language(code: String) {
  def stopwords: Set[String]

  lazy val resourceDir = "/lang/" + code
  def appendPath(subdir: String) = resourceDir + subdir
  def getLexicon(filename: String) =
    Resource.asSource(appendPath("/lexicon/"+filename))
      .getLines
      .filterNot(_.startsWith(";")) // filter out comments
      .toSet
}

/**
 * English information.
 */
object English extends Language("eng") {
  lazy val stopwords = getLexicon("stopwords.english")

  def removeNonLanguage(text: String) =
    text.replaceAll("[@#][A-Za-z_]+","")
      .replaceAll("""http[^\s]+""","")
      .replaceAll("\\s+"," ")
}

abstract class OtherLexica(code: String) {
  lazy val resourceDir = "/lang/" + code
  def appendPath(subdir: String) = resourceDir + subdir
  def getLexicon(filename: String) =
    Resource.asSource(appendPath("/lexicon/"+filename))
      .getLines
      .filterNot(_.startsWith(";")) // filter out comments
      .toSet
}

object Polarity extends OtherLexica("eng") {
  lazy val posWordsLiu = getLexicon("positive-words-liu.txt.gz")
  lazy val posWords = posWordsLiu
  lazy val negWordsLiu = getLexicon("negative-words-liu.txt.gz")
  lazy val negWords = negWordsLiu
}
