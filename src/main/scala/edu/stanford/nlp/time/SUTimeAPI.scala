
package edu.stanford.nlp.time

import edu.stanford.nlp.ie.regexp.NumberSequenceClassifier
import edu.stanford.nlp.ling.{CoreAnnotations, CoreLabel}

import java.util
import scala.collection.JavaConverters.iterableAsScalaIterableConverter
import scala.collection.mutable.ArrayBuffer

class SUTimeAPI {
  val numSeqClassifier = new NumberSequenceClassifier()

  def classify(words: Seq[String], tags: Seq[String], startOffsets: Seq[Int], endOffsets: Seq[Int]): (Seq[String], Seq[String]) = {
    assert(words != null && tags != null)
    assert(words.length == tags.length)
    assert(startOffsets != null && endOffsets != null)
    assert(words.length == startOffsets.length)
    assert(words.length == endOffsets.length)

    val coreLabels = new util.ArrayList[CoreLabel]()
    var tokenOffset = 0
    for(i <- words.indices) {
      val coreLabel = new CoreLabel()
      coreLabel.setWord(words(i))
      coreLabel.setValue(words(i))
      coreLabel.setTag(tags(i))
      coreLabel.setIndex(tokenOffset + 1) // Stanford counts tokens starting from 1
      coreLabel.setSentIndex(1) // only 1 sentence at a time here
      coreLabel.setBeginPosition(startOffsets(i))
      coreLabel.setEndPosition(endOffsets(i))

      coreLabels.add(coreLabel)
      tokenOffset += 1
    }

    val outputs = numSeqClassifier.classify(coreLabels)

    val labels = new ArrayBuffer[String]()
    val norms = new ArrayBuffer[String]()

    for (output <- outputs.asScala) {
      val label = output.get(classOf[CoreAnnotations.AnswerAnnotation])
      val timex = output.get(classOf[TimeAnnotations.TimexAnnotation])
      val norm = if(timex != null) timex.value() else ""
      val norm2 = if(timex != null) timex.altVal() else ""
      println(output.word() + " " + label + " " + timex + " " + norm + " " + norm2)

      labels += label
      norms += norm
    }

    (labels, norms)
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
      Array(2, 6, 11, 19, 24, 28, 33, 34)
    )
    println(labels.mkString(", "))
    println(norms.mkString(", "))
  }
}