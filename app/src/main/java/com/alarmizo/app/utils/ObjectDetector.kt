package com.alarmizo.app.util

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import java.nio.ByteBuffer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ObjectDetector @Inject constructor(
    private val context: Context
) {

    companion object {
        const val MODEL_FILE = "mobilenet_v1_1.0_224_quant.tflite"
        const val LABELS_FILE = "labels_mobilenet_quant_v1_224.txt"
        const val INPUT_SIZE = 224
        const val CONFIDENCE_THRESHOLD = 0.3f  // 30% confidence minimum
    }

    private var interpreter: Interpreter? = null
    private var labels: List<String> = emptyList()

    init {
        setupInterpreter()
        loadLabels()
    }

    private fun setupInterpreter() {
        val model = FileUtil.loadMappedFile(context, MODEL_FILE)
        interpreter = Interpreter(model)
    }

    private fun loadLabels() {
        labels = FileUtil.loadLabels(context, LABELS_FILE)
    }

    fun detect(bitmap: Bitmap): DetectionResult {
//        val top5 = scores.indices
//            .sortedByDescending { scores[it].toInt() and 0xFF }
//            .take(5)
//
//        top5.forEach { index ->
//            val conf = (scores[index].toInt() and 0xFF) / 255f
//            val lbl = labels[index]
//            android.util.Log.d("TFLite", "→ $lbl: ${"%.0f".format(conf * 100)}%")
//        }
        return try {
            val interpreter = interpreter
                ?: return DetectionResult.Failure("Interpreter not initialized")

            // 1. preprocess image
            val imageProcessor = ImageProcessor.Builder()
                .add(ResizeOp(INPUT_SIZE, INPUT_SIZE, ResizeOp.ResizeMethod.BILINEAR))
                .build()

            val tensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmap))

            // 2. run inference
            val outputArray = Array(1) { ByteArray(labels.size) }
            interpreter.run(tensorImage.buffer, outputArray)

            // 3. find highest confidence label
            val scores = outputArray[0]
            val maxIndex = scores.indices.maxByOrNull { scores[it].toInt() and 0xFF } ?: -1

            if (maxIndex == -1) return DetectionResult.Failure("No result")

            val confidence = (scores[maxIndex].toInt() and 0xFF) / 255f
            val label = labels[maxIndex].lowercase().trim()

            // 4. check confidence threshold
            if (confidence >= CONFIDENCE_THRESHOLD) {
                DetectionResult.Success(label = label, confidence = confidence)
            } else {
                DetectionResult.Failure("Not confident enough: $label (${"%.0f".format(confidence * 100)}%)")
            }

        } catch (e: Exception) {
            DetectionResult.Failure("Detection failed: ${e.message}")
        }
    }

    private val synonyms = mapOf(
        "water bottle" to listOf(
            "bottle", "water jug", "jug", "container",
            "plastic bottle", "drinking bottle"
        ),
        "phone" to listOf(
            "mobile phone", "cellular phone", "smartphone", "iphone"
        ),
        "cup" to listOf(
            "coffee mug", "mug", "teacup", "beaker"
        ),
        "keyboard" to listOf(
            "computer keyboard", "typewriter keyboard"
        ),
        "book" to listOf(
            "novel", "textbook", "notebook", "comic book"
        ),
        "chair" to listOf(
            "seat", "armchair", "folding chair", "rocking chair"
        ),
        "laptop" to listOf(
            "notebook computer", "computer"
        ),
        "banana" to listOf("fruit", "yellow fruit"),
        "backpack" to listOf("bag", "school bag", "rucksack", "knapsack")
    )

    fun isMatch(detectedLabel: String, targetObject: String): Boolean {
        val detected = detectedLabel.lowercase().trim()
        val target = targetObject.lowercase().trim()

        // 1. exact match
        if (detected == target) return true

        // 2. contains match
        if (detected.contains(target) || target.contains(detected)) return true

        // 3. synonym match
        val targetSynonyms = synonyms[target] ?: emptyList()
        if (targetSynonyms.any { detected.contains(it) || it.contains(detected) }) return true

        // 4. word level match
        val targetWords = target.split(" ")
        val detectedWords = detected.split(" ")
        if (targetWords.any { tw -> detectedWords.any { dw -> tw == dw } }) return true

        return false
    }

    fun close() {
        interpreter?.close()
        interpreter = null
    }
}

sealed class DetectionResult {
    object Idle : DetectionResult()
    object Detecting : DetectionResult()
    data class Success(val label: String, val confidence: Float) : DetectionResult()
    data class Failure(val reason: String) : DetectionResult()
}