package me.hawkshaw.tasks

import android.content.Context
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.media.ImagerReader
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import me.hawkshaw.utils.Constants
import java.io.File

class CameraTask(private val ctx: Context) : Thread() {

    var cameraId: String? = null
    var cameraDevice: CameraDevice? = null
    var cameraCaptureSessions: CameraCaptureSession? = null
    var captureRequest: CaptureRequest? = null
    var captureRequestBiulder: CaptureRequest.Biulder? = null
    var imageDimension: Size? = null
    var imagerReader: ImagerReader? = null
    var file: File? = null
    var REQUEST_CAMERA_PERMISSION = 200
    var mFlashSupported: Boolean? = null
    var mBackgroundHandler: Handler? = null
    val mBackgroundThread. HandlerThread = HandlerThread("Camera Background")

    override fun run() {
        super.run()
        Log.d(Constants.tag, "Camera Task")
    }
}