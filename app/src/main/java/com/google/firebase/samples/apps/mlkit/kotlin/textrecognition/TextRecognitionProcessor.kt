package com.google.firebase.samples.apps.mlkit.kotlin.textrecognition

import android.graphics.Bitmap
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer
import com.google.firebase.samples.apps.mlkit.common.CameraImageGraphic
import com.google.firebase.samples.apps.mlkit.common.FrameMetadata
import com.google.firebase.samples.apps.mlkit.common.GraphicOverlay
import com.google.firebase.samples.apps.mlkit.kotlin.VisionProcessorBase
import java.io.IOException
import java.lang.Double.parseDouble

/** Processor for the text recognition demo.  */
class TextRecognitionProcessor : VisionProcessorBase<FirebaseVisionText>() {

    private val detector: FirebaseVisionTextRecognizer = FirebaseVision.getInstance().onDeviceTextRecognizer

    override fun stop() {
        try {
            detector.close()
        } catch (e: IOException) {
            Log.e(TAG, "Exception thrown while trying to close Text Detector: $e")
        }
    }

    override fun detectInImage(image: FirebaseVisionImage): Task<FirebaseVisionText> {
        return detector.processImage(image)
    }

    override fun onSuccess(originalCameraImage: Bitmap?, results: FirebaseVisionText,
        frameMetadata: FrameMetadata, graphicOverlay: GraphicOverlay) {

        graphicOverlay.clear()

        originalCameraImage.let { image ->
            val imageGraphic = CameraImageGraphic(graphicOverlay, image)
            graphicOverlay.add(imageGraphic)
        }

        Log.d(TAG, "detected text is: ${results.text}")
        val blocks = results.textBlocks
        for (i in blocks.indices) {
            val lines = blocks[i].lines
            for (j in lines.indices) {
                val elements = lines[j].elements
                for (k in elements.indices) {
                    val textGraphic = TextGraphic(graphicOverlay, elements[k])
                    graphicOverlay.add(textGraphic)
                }
            }
        }
        graphicOverlay.postInvalidate()

        val total = extractTotal(results)
        Log.d(TOTAL, "total price is: $total")
    }

    private fun extractTotal(results: FirebaseVisionText): Double? {
        val list: ArrayList<Double> = arrayListOf()
        for (result in results.textBlocks) {
            // 先检测该字符串是否为纯数字字符串，假如是，加入集合中
            if (result.text.matches("\\d+(\\.\\d+)?".toRegex())) {
                list.add(parseDouble(result.text))
            } else {
                // 在检测该该字符串是否含有$符号，假如是，用normalizeDouble把$和多余char去掉
                // 再把整理过之后的字符串加入集合
                if (result.text.contains('$')) {
                    list.add(normalizeDouble(result.text))
                }
            }
        }
        // 设置加油站收据的合理最大值
        val maxValue = 200.0
        var max = 0.0
        // 历遍集合找出小于合理最大值的最大值
        for (number in list) {
            if (number > max && number < maxValue) {
                max = number
            }
        }
        return max
    }

    private fun normalizeDouble(string: String): Double {
        var res = ""
        for (i in string.indices) {
            if (string[i].isDigit() || string[i] == '.') {
                res += string[i]
            }
        }
        return parseDouble(res)
    }

    override fun onFailure(e: Exception) {
        Log.w(TAG, "Text detection failed.$e")
    }

    companion object {
        private const val TAG = "TextRecProc"
        private const val TOTAL = "MaxTotalPrice"
    }
}
