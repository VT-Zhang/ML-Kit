package com.google.firebase.samples.apps.mlkit.kotlin.facedetection

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark
import com.google.firebase.samples.apps.mlkit.common.GraphicOverlay
import com.google.firebase.samples.apps.mlkit.common.GraphicOverlay.Graphic

/** Graphic instance for rendering face contours graphic overlay view.  */
class FaceContourGraphic(overlay: GraphicOverlay, private val firebaseVisionFace: FirebaseVisionFace?)
    : Graphic(overlay) {

    private val idPaint: Paint = Paint()
    private val boxPaint: Paint = Paint()
    private val facePositionPaint: Paint = Paint()
    private val faceContourPaint: Paint = Paint()
    private val eyebrowPaint: Paint = Paint()
    private val eyePaint: Paint = Paint()
    private val nosePaint: Paint = Paint()
    private val lipsPaint: Paint = Paint()

    init {

        idPaint.color = Color.WHITE
        idPaint.textSize = ID_TEXT_SIZE

        boxPaint.color = Color.RED
        boxPaint.style = Paint.Style.STROKE
        boxPaint.strokeWidth = BOX_STROKE_WIDTH

        facePositionPaint.color = Color.WHITE
        faceContourPaint.color = Color.BLUE
        eyebrowPaint.color = Color.GREEN
        eyePaint.color = Color.WHITE
        nosePaint.color = Color.MAGENTA
        lipsPaint.color = Color.RED

    }

    /** Draws the face annotations for position on the supplied canvas.  */
    override fun draw(canvas: Canvas) {
        val face = firebaseVisionFace ?: return

        // Draws a circle at the position of the detected face, with the face's track id below.
        val x = translateX(face.boundingBox.centerX().toFloat())
        val y = translateY(face.boundingBox.centerY().toFloat())
        canvas.drawCircle(x, y, FACE_POSITION_RADIUS, facePositionPaint)
        canvas.drawText("id: ${face.trackingId}", x + ID_X_OFFSET, y + ID_Y_OFFSET, idPaint)

        // Draws a bounding box around the face.
        val xOffset = scaleX(face.boundingBox.width() / 2.0f)
        val yOffset = scaleY(face.boundingBox.height() / 2.0f)
        val left = x - xOffset
        val top = y - yOffset
        val right = x + xOffset
        val bottom = y + yOffset
        canvas.drawRect(left, top, right, bottom, boxPaint)

        val faceContour = face.getContour(FirebaseVisionFaceContour.FACE)
        for (point in faceContour.points) {
            val px = translateX(point.x)
            val py = translateY(point.y)
            canvas.drawCircle(px, py, FACE_POSITION_RADIUS, faceContourPaint)
        }

        val leftEyebrowTopContour = face.getContour(FirebaseVisionFaceContour.LEFT_EYEBROW_TOP)
        for (point in leftEyebrowTopContour.points) {
            val px = translateX(point.x)
            val py = translateY(point.y)
            canvas.drawCircle(px, py, FACE_POSITION_RADIUS, eyebrowPaint)
        }

        val leftEyebrowBottomContour = face.getContour(FirebaseVisionFaceContour.LEFT_EYEBROW_BOTTOM)
        for (point in leftEyebrowBottomContour.points) {
            val px = translateX(point.x)
            val py = translateY(point.y)
            canvas.drawCircle(px, py, FACE_POSITION_RADIUS, eyebrowPaint)
        }

        val rightEyebrowTopContour = face.getContour(FirebaseVisionFaceContour.RIGHT_EYEBROW_TOP)
        for (point in rightEyebrowTopContour.points) {
            val px = translateX(point.x)
            val py = translateY(point.y)
            canvas.drawCircle(px, py, FACE_POSITION_RADIUS, eyebrowPaint)
        }

        val rightEyebrowBottomContour = face.getContour(FirebaseVisionFaceContour.RIGHT_EYEBROW_BOTTOM)
        for (point in rightEyebrowBottomContour.points) {
            val px = translateX(point.x)
            val py = translateY(point.y)
            canvas.drawCircle(px, py, FACE_POSITION_RADIUS, eyebrowPaint)
        }

        val leftEyeContour = face.getContour(FirebaseVisionFaceContour.LEFT_EYE)
        for (point in leftEyeContour.points) {
            val px = translateX(point.x)
            val py = translateY(point.y)
            canvas.drawCircle(px, py, FACE_POSITION_RADIUS, eyePaint)
        }

        val rightEyeContour = face.getContour(FirebaseVisionFaceContour.RIGHT_EYE)
        for (point in rightEyeContour.points) {
            val px = translateX(point.x)
            val py = translateY(point.y)
            canvas.drawCircle(px, py, FACE_POSITION_RADIUS, eyePaint)
        }

        val noseBridgeContour = face.getContour(FirebaseVisionFaceContour.NOSE_BRIDGE)
        for (point in noseBridgeContour.points) {
            val px = translateX(point.x)
            val py = translateY(point.y)
            canvas.drawCircle(px, py, FACE_POSITION_RADIUS, nosePaint)
        }

        val noseBottomContour = face.getContour(FirebaseVisionFaceContour.NOSE_BOTTOM)
        for (point in noseBottomContour.points) {
            val px = translateX(point.x)
            val py = translateY(point.y)
            canvas.drawCircle(px, py, FACE_POSITION_RADIUS, nosePaint)
        }

        val upperLipsTopContour = face.getContour(FirebaseVisionFaceContour.UPPER_LIP_TOP)
        for (point in upperLipsTopContour.points) {
            val px = translateX(point.x)
            val py = translateY(point.y)
            canvas.drawCircle(px, py, FACE_POSITION_RADIUS, lipsPaint)
        }

        val upperLipsBottomContour = face.getContour(FirebaseVisionFaceContour.UPPER_LIP_BOTTOM)
        for (point in upperLipsBottomContour.points) {
            val px = translateX(point.x)
            val py = translateY(point.y)
            canvas.drawCircle(px, py, FACE_POSITION_RADIUS, lipsPaint)
        }

        val lowerLipsTopContour = face.getContour(FirebaseVisionFaceContour.LOWER_LIP_TOP)
        for (point in lowerLipsTopContour.points) {
            val px = translateX(point.x)
            val py = translateY(point.y)
            canvas.drawCircle(px, py, FACE_POSITION_RADIUS, lipsPaint)
        }

        val lowerLipsBottomContour = face.getContour(FirebaseVisionFaceContour.LOWER_LIP_BOTTOM)
        for (point in lowerLipsBottomContour.points) {
            val px = translateX(point.x)
            val py = translateY(point.y)
            canvas.drawCircle(px, py, FACE_POSITION_RADIUS, lipsPaint)
        }


        if (face.smilingProbability >= 0) {
            canvas.drawText(
                    "Smile Index: ${String.format("%.2f", face.smilingProbability)}",
                    x + ID_X_OFFSET * 3,
                    y - ID_Y_OFFSET,
                    idPaint)
        }

        if (face.rightEyeOpenProbability >= 0) {
            canvas.drawText(
                    "Right Eye: ${String.format("%.2f", face.rightEyeOpenProbability)}",
                    x - ID_X_OFFSET,
                    y,
                    idPaint)
        }
        if (face.leftEyeOpenProbability >= 0) {
            canvas.drawText(
                    "Left Eye: ${String.format("%.2f", face.leftEyeOpenProbability)}",
                    x + ID_X_OFFSET * 6,
                    y,
                    idPaint)
        }

        val leftEye = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EYE)
        leftEye?.position?.let {
            canvas.drawCircle(translateX(it.x), translateY(it.y), FACE_POSITION_RADIUS, eyePaint)
        }
        val rightEye = face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EYE)
        rightEye?.position?.let {
            canvas.drawCircle(translateX(it.x), translateY(it.y), FACE_POSITION_RADIUS, eyePaint)
        }
        val leftCheek = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_CHEEK)
        leftCheek?.position?.let {
            canvas.drawCircle(translateX(it.x), translateY(it.y), FACE_POSITION_RADIUS, facePositionPaint)
        }

        val rightCheek = face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_CHEEK)
        rightCheek?.position?.let {
            canvas.drawCircle(translateX(it.x), translateY(it.y), FACE_POSITION_RADIUS, facePositionPaint)
        }
        val nose = face.getLandmark(FirebaseVisionFaceLandmark.NOSE_BASE)
        nose?.position?.let {
            canvas.drawCircle(translateX(it.x), translateY(it.y), FACE_POSITION_RADIUS, nosePaint)
        }
    }

    companion object {

        private const val FACE_POSITION_RADIUS = 5.0f
        private const val ID_TEXT_SIZE = 30.0f
        private const val ID_Y_OFFSET = 80.0f
        private const val ID_X_OFFSET = -70.0f
        private const val BOX_STROKE_WIDTH = 5.0f
    }
}
