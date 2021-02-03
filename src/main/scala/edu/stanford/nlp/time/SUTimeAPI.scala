
package edu.stanford.nlp.time

import edu.stanford.nlp.ie.{NERClassifierCombiner, QuantifiableEntityNormalizer}
import edu.stanford.nlp.ie.regexp.NumberSequenceClassifier
import edu.stanford.nlp.ling.{CoreAnnotations, CoreLabel}
import edu.stanford.nlp.util.{ArrayCoreMap, CoreMap}

import java.util
import scala.collection.JavaConverters.iterableAsScalaIterableConverter
import scala.collection.mutable.ArrayBuffer

class SUTimeAPI {
  val numSeqClassifier = new NumberSequenceClassifier()

  def classify(words: Seq[String],
               tags: Seq[String],
               startCharOffsets: Seq[Int],
               endCharOffsets: Seq[Int],
               docDateOpt: Option[String]): (Seq[String], Seq[String]) = {

    assert(words != null && tags != null)
    assert(words.length == tags.length)
    assert(startCharOffsets != null && endCharOffsets != null)
    assert(words.length == startCharOffsets.length)
    assert(words.length == endCharOffsets.length)

    //
    // based on NERClassifierCombiner.classifyWithGlobalInformation() (line 272)
    //

    // create the list of CoreLabels
    val coreLabels = mkCoreLabels(words, tags, startCharOffsets, endCharOffsets)

    // TODO: wrap with try catch

    // run the numeric classifier
    val outputs = recognizeNumberSequences(coreLabels, docDateOpt)

    // generate normalized values
    QuantifiableEntityNormalizer.addNormalizedQuantitiesToEntities(outputs, false, true)

    // TODO: ranges not normalized!

    // fetch labels and norms
    mkOutputs(outputs.asScala)
  }

  def mkOutputs(outputs: Iterable[CoreLabel]): (Seq[String], Seq[String]) = {
    val labels = new ArrayBuffer[String]()
    val norms = new ArrayBuffer[String]()

    for (output <- outputs) {
      val label = output.get(classOf[CoreAnnotations.NamedEntityTagAnnotation])
      val timex = output.get(classOf[TimeAnnotations.TimexAnnotation])
      val norm = if(timex != null) timex.value() else ""
      println(output.word() + " " + label + " " + timex + " " + norm)

      labels += label
      norms += norm
    }

    (labels, norms)
  }

  def recognizeNumberSequences(coreLabels: util.List[CoreLabel], docDateOpt: Option[String]): java.util.List[CoreLabel] = {

    //
    // sets AnswerAnnotation, TimexAnnotation
    //
    val outputs =
      if(docDateOpt.isEmpty) {
        numSeqClassifier.classify(coreLabels)
      } else {
        //
        // See: DocDateAnnotator line 116
        // annotation.set(classOf[CoreAnnotations.DocDateAnnotation], foundDocDate)
        //
        val doc = new ArrayCoreMap()
        doc.set(classOf[CoreAnnotations.DocDateAnnotation], docDateOpt.get)
        numSeqClassifier.classifyWithGlobalInformation(coreLabels, doc, null)
      }

    // AnswerAnnotation -> NERAnnotation
    NERClassifierCombiner.copyAnswerFieldsToNERField(outputs)

    outputs
  }

  def mkCoreLabels(words: Seq[String],
                   tags: Seq[String],
                   startCharOffsets: Seq[Int],
                   endCharOffsets: Seq[Int]): java.util.List[CoreLabel] = {
    val coreLabels = new util.ArrayList[CoreLabel]()

    var tokenOffset = 0
    for(i <- words.indices) {
      val coreLabel = new CoreLabel()
      coreLabel.setWord(words(i))
      coreLabel.setValue(words(i))
      coreLabel.setTag(tags(i))
      coreLabel.setIndex(tokenOffset + 1) // Stanford counts tokens starting from 1
      coreLabel.setSentIndex(1) // only 1 sentence at a time here
      coreLabel.setBeginPosition(startCharOffsets(i))
      coreLabel.setEndPosition(endCharOffsets(i))

      coreLabels.add(coreLabel)
      tokenOffset += 1
    }

    coreLabels
  }
}

object SUTimeAPI {
  def main(args: Array[String]): Unit = {
    val api = new SUTimeAPI

    /*
    api.classify(
      Array("He", "was", "born", "between", "January", "1st", ",", "1966", "and", "1997", "."),
      Array("PRP", "VBD", "VBN", "IN", "NNP", "NN", ",", "CD", "CC", "CD", "."),
      Array(0, 3, 7, 12, 20, 28, 32, 34, 39, 43, 48),
      Array(2, 6, 11, 19, 27, 31, 33, 38, 42, 47, 49)
    )
    */

    val (labels, norms) = api.classify(
      Array("He", "was", "born", "between", "1996", "and", "1997", "."), // He was born between 1996 and 1997 .
      Array("PRP", "VBD", "VBN", "IN", "CD", "CC", "CD", "."), // PRP VBD VBN IN CD CC CD .
      Array(0, 3, 7, 12, 20, 25, 29, 33),
      Array(2, 6, 11, 19, 24, 28, 33, 34),
      None
    )
    println(labels.mkString(", "))
    println(norms.mkString(", "))
  }
}