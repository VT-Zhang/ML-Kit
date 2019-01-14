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
import java.lang.Float.parseFloat

/** Processor for the text recognition demo.  */
class TextRecognitionProcessor : VisionProcessorBase<FirebaseVisionText>() {

    private val detector: FirebaseVisionTextRecognizer = FirebaseVision.getInstance().onDeviceTextRecognizer
    private val resList: ArrayList<String> = arrayListOf()
    private val resMap = mutableMapOf<String, Int>()
    private var totalAmount: String = ""
    private var date: String = ""
    private val twoDigitDateRegex = "^((0|1)\\d{1})[- /.]((0|1|2)\\d{1})[- /.](\\d{2})"
    private val fourDigitDateRegex = "^(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.](19|20)\\d\\d\$"

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
                    val textGraphic = TextGraphic(graphicOverlay, elements[k], false)
                    graphicOverlay.add(textGraphic)
                }
            }
        }


        totalAmount = extractTotal(results)
        Log.d(TOTAL, totalAmount)

        for (i in blocks.indices) {
            val lines = blocks[i].lines
            for (j in lines.indices) {
                val elements = lines[j].elements
                for (k in elements.indices) {
                    if (elements[k].text == totalAmount) {
                        val targetTextGraphic = TextGraphic(graphicOverlay, elements[k], true)
                        graphicOverlay.add(targetTextGraphic)
                    }
                }
            }
        }

        graphicOverlay.postInvalidate()

        date = extractDate(results)
        Log.d(TOTAL, "The date is $date")

    }

    private fun extractDate(results: FirebaseVisionText): String {
        for (result in results.textBlocks) {
            if (result.text.matches(fourDigitDateRegex.toRegex()) ||
                    result.text.matches(twoDigitDateRegex.toRegex())) {
                return result.text
            }
        }
        return ""
    }

    private fun extractTotal(results: FirebaseVisionText): String {
        var isDollar: Boolean = false
        val list: ArrayList<Float> = arrayListOf()
        for (result in results.textBlocks) {
            // 先检测该字符串是否为纯数字字符假如是，加入集合中
            try {
                list.add(parseFloat(result.text))
            } catch (e: NumberFormatException) {
                Log.d(ERR, "Number formatting error")
            }

            // 在检测该该字符串是否含有$符号，假如是，用normalizeFloat把$和多余char去掉
            // 再把整理过之后的字符串加入集合
            if (result.text.contains('$')) {
                isDollar = true
                list.add(normalizeFloat(result.text))
            }
        }
        Log.d(LIST, "The list is: $list" )
        // 设置加油站/餐饮收据的合理最大值
        val reasonable = 100f
        var max = 0f
        // 历遍集合找出小于合理值的最大值
        for (number in list) {
            if (number > max && number < reasonable) {
                max = number
            }
        }
        if (isDollar) {
            return "$" + "%.2f".format(max)
        }
        return "%.2f".format(max)
    }

    private fun normalizeFloat(string: String): Float {
        var res = ""
        for (i in string.indices) {
            if (string[i].isDigit() || string[i] == '.') {
                res += string[i]
            }
        }
        try {
            return parseFloat(res)
        } catch (e: NumberFormatException) {
            Log.d(ERR, "Number formatting error")
        }
        return 0f
    }

    // 计算出现最多的结果在数组中的概率
    private fun getConfidence(map: MutableMap<String, Int>, listSize: Int, string: String): Float {
        for ((key, value) in map) {
            if (key == string) {
                return (value / listSize).toFloat()
            }
        }
        return 0f
    }

    // 返回整个数组中重复出现最多的结果
    private fun getMostPossibleNumber(list: ArrayList<String>, map: MutableMap<String, Int>): String {
        for (item in list) {
            if (map.containsKey(item)) {
                map[item]?.plus(1)
            } else {
                map[item] = 1
            }
        }
        val entry = map.maxBy { it.value }
        val res = entry?.key.toString()
        Log.d(MAX, "The map is $map")
        Log.d(MAX, "The max number is: $res")
        return res
    }

    override fun onFailure(e: Exception) {
        Log.w(TAG, "Text detection failed.$e")
    }

    companion object {
        private const val TAG = "TextRecProc"
        private const val TOTAL = "MaxTotalPrice"
        private const val ERR = "NumberFormattingError"
        private const val LIST = "List"
        private const val MAX = "MaxNumber"
    }
}
