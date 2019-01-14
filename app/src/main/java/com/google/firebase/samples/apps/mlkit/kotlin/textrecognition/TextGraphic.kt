package com.google.firebase.samples.apps.mlkit.kotlin.textrecognition

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.firebase.samples.apps.mlkit.common.GraphicOverlay

/**
 * Graphic instance for rendering TextBlock position, size, and ID within an associated graphic
 * overlay view.
 */
class TextGraphic(overlay: GraphicOverlay, private val text: FirebaseVisionText.Element?,
                  private val isTarget: Boolean) : GraphicOverlay.Graphic(overlay) {

    private val rectPaint = Paint().apply {
        color = TEXT_COLOR
        style = Paint.Style.STROKE
        strokeWidth = STROKE_WIDTH
    }

    private val textPaint = Paint().apply {
        color = TEXT_COLOR
        textSize = TEXT_SIZE
    }

    private val targetRectPaint = Paint().apply {
        color = TARGET_TEXT_COLOR
        style = Paint.Style.STROKE
        strokeWidth = TARGET_STROKE_WIDTH
    }

    private val targeTextPaint = Paint().apply {
        color = TARGET_TEXT_COLOR
        textSize = TARGET_TEXT_SIZE
    }

    /** Draws the text block annotations for position, size, and raw value on the supplied canvas.  */
    override fun draw(canvas: Canvas) {
        if (isTarget) {
            text?.let { txt ->
                // Draws the bounding box around the TextBlock.
                val rect = RectF(txt.boundingBox)
                rect.left = translateX(rect.left)
                rect.top = translateY(rect.top)
                rect.right = translateX(rect.right)
                rect.bottom = translateY(rect.bottom)
                canvas.drawRect(rect, targetRectPaint)

                // Renders the text at the bottom of the box.
//                canvas.drawText(txt.text, rect.left, rect.bottom, targeTextPaint)
            } ?: kotlin.run { throw IllegalStateException("Attempting to draw a null text.") }
        }

        else {
            text?.let { txt ->
                // Draws the bounding box around the TextBlock.
                val rect = RectF(txt.boundingBox)
                rect.left = translateX(rect.left)
                rect.top = translateY(rect.top)
                rect.right = translateX(rect.right)
                rect.bottom = translateY(rect.bottom)
                canvas.drawRect(rect, rectPaint)

                // Renders the text at the bottom of the box.
                canvas.drawText(txt.text, rect.left, rect.bottom, textPaint)
            } ?: kotlin.run { throw IllegalStateException("Attempting to draw a null text.") }
        }

    }

    companion object {
        private const val TEXT_COLOR = Color.GREEN
        private const val TEXT_SIZE = 54.0f
        private const val STROKE_WIDTH = 4.0f

        private const val TARGET_TEXT_COLOR = Color.RED
        private const val TARGET_TEXT_SIZE = 64.0f
        private const val TARGET_STROKE_WIDTH = 10.0f

    }
}
