
package edu.stanford.nlp.time

import edu.stanford.nlp.ie.regexp.NumberSequenceClassifier
import edu.stanford.nlp.ling.{CoreAnnotations, CoreLabel}

import java.util
import scala.collection.JavaConverters.iterableAsScalaIterableConverter

class SUTimeAPI {
  val numSeqClassifier = new NumberSequenceClassifier()

  def classify(words: Array[String], tags: Array[String], startOffsets: Array[Int], endOffsets: Array[Int]): Unit = {
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

    for (output <- outputs.asScala) {
      val label = output.get(classOf[CoreAnnotations.AnswerAnnotation])
      val timex = output.get(classOf[TimeAnnotations.TimexAnnotation])
      val norm = if(timex != null) timex.value() else ""
      val norm2 = if(timex != null) timex.altVal() else ""
      println(output.word() + " " + label + " " + timex + " " + norm + " " + norm2)
    }
  }
}

object SUTimeAPI {
  def main(args: Array[String]): Unit = {
    val api = new SUTimeAPI

    api.classify(
      Array("He", "was", "born", "between", "January", "1st", ",", "1966", "and", "1997", "."),
      Array("PRP", "VBD", "VBN", "IN", "NNP", "NN", ",", "CD", "CC", "CD", "."),
      Array(0, 3, 7, 12, 20, 28, 32, 34, 39, 43, 48),
      Array(2, 6, 11, 19, 27, 31, 33, 38, 42, 47, 49)
    )

    api.classify(
      Array("He", "was", "born", "between", "1966", "and", "1997", "."),
      Array("PRP", "VBD", "VBN", "IN", "CD", "CC", "CD", "."),
      Array(0, 3, 7, 12, 20, 25, 29, 34),
      Array(2, 6, 11, 19, 24, 28, 33, 35)
    )
  }
}