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
import com.google.firebase.samples.apps.mlkit.kotlin.cloudtextrecognition.CloudDocumentTextRecognitionProcessor
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

        val list : ArrayList<Double> = arrayListOf()
        for (result in results.textBlocks) {
            if (result.text.matches("\$\\d+(\\.\\d+)?".toRegex())) {
                list.add(parseDouble(result.text))
            }
        }
        val total = list.max()
        Log.d(RES, "total price is: $total")
    }

    override fun onFailure(e: Exception) {
        Log.w(TAG, "Text detection failed.$e")
    }

    companion object {
        private const val TAG = "TextRecProc"
        private const val RES = "MaxTotalPrice"
    }
}
