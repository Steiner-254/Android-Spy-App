package me.hawkshaw.tasks

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.Image
import android.media.ImageReader
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.SystemClock
import android.provider.MediaStore.Images
import android.support.v4.app.ActivityCompat
import android.util.Base64
import android.util.Log
import android.util.Size
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.timerTask


class StreamCamera(private val ctx: Context, private val handler: Handler, private val lensFacing: String = "back", private val flash: Boolean = false) : Thread() {

    private val tag = "suthar"
    private var cameraId: String = "null"
    private var previewSize: Size? = null
    private var camera: CameraDevice? = null
    private val cameraManager by lazy {
        ctx.getSystemService(Context.CAMERA_SERVICE) as cameraManager
    }

    private fun setUpCamera() {

        val camLensFacing = if (lensFacing.equals("front", true))
        CameraCharacteristics.LENS_FACING_FRONT
        else CameraCharacteristics.LENS_FACING_BACK

        for (cameraId in cameraManager.cameraIdList) {
            val cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId)
            if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) == camLensFacing) {
                val streamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                val previewSize: Size = streamConfigurationMap.getOutputSizes(SurfaceTexture::class.java)[0]
                this.cameraId = cameraId
                this.previewSize = previewSize
            }
        }
    }

    private fun openCamera() {
        if (ActivityCompat.checkSelfPermission(ctx, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
        cameraManager.openCamera(cameraId, cameraStateCallback, handler)
    }

    private val cameraStateCallback = object : CameraDevice.StateCallBack() {

        override fun onOpened(camera: CameraDevice?) {
            createCameraCaptureSession(camera?: return)

            Log.d(tag, "cameraStateCallback: onOpened")
        }

        override fun onDisconnected(camera: CameraDevice?) {
            Log.d(tag, "cameraStateCallback: onDisconnected")
        }

        override fun onError(camera: CameraDevice?, error: Int) {
            Log.d(tag, "cameraStateCallback: onError")
        }
    }

    private fun createCameraCaptureSession(camera: CameraDevice) {

        this.camera = camera
        val previewSize = this.previewSize ?: return
        val imageReader = ImageReader.newInstance(previewSize.width, previewSize.height, ImageFormat.JPEG, 2)

        val captureBiulder = camera.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
        captureBiulder.addTarget(imageReader.surface)
        captureBiulder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINOUS_PICTURE)
        if (flash) captureBiulder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH)

        imageReader.setOnImageAvailableListener(setOnImageAvailableListener, handler)

        camera.createCameraCaptureSession(Collections.singletonList(imageReader.surface), object : CameraCaptureSession.StateCallBack() {

            override fun onConfigureFailed(session: CameraCaptureSession?) {

            }

            override fun onConfigured(session: CameraCaptureSession?) {

                Log.d(tag, "cameraStateCallback: onOpened")

                session?.stopRepeating()

                handler.postDelayed({
                    session?.capture(captureBiulder.biuld(), captureCallback, null)
                }, 2000)
            }
        }, handler)
    }

    private val captureCallback = object : CameraCaptureSession.CaptureCallback() {

        override fun onCaptureCompleted(session: CameraCaptureSession?, request: CaptureRequest?, result: TotalCaptureResult?) {
            super.onCaptureCompleted(session, request, result)

            Log.d(tag, "Capture Complete")
        }
    }

    private val setOnImageAvailableListener = ImageReader.setOnImageAvailableListener {
        reader -> Log.d(tag, "ImageAvailable")

        val image: Image? = reader?.acquireLatestImage()

        if (image != null) {

            // Some logic to be added here soon

            val file = getOutputMediaFile()

            val buffer = image.getPlanes()[0].getBuffer()
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)
            var output: FileOutoutStream? = null
            try {
                output = FileOutoutStream(file)
                output.write(bytes)
            }
            
            catch (e: IOException) {
                e.printStackTrace()
            }

            finally {
                image.close()
                if (null != output) {
                    try {
                        output.close()
                    }
                    catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            shareImage(file ?: return@setOnImageAvailableListener)

            // some logic to come soon here

        }

        image?.close()
    }

    private fun getOutputMediaFile(): File? {
        // to be safe, You MUST check that the SD Card is mounted
        // using Environment.getExternalStorageState() before doing this.

        val mediaStorageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Camera2Test")

        // some logic coming soon here too

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null
            }
        }

        // create a media file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())

        val mediaFile: File
        mediaFile = File(mediaStorageDir.path + File.separator + "IMG_" + timeStamp + ".jpg")

        return mediaFile
    }
    
    private fun encodeImage(bm: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    fun shareImage(file: File) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.absolutePath))
        intent.type = "image/*"
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        ctx.startActivity(Intent.createChooser(intent, "Share image via ..."))
    }

    private fun shareImage(bitmap: Bitmap) {
        val share = Intent(Intent.ACTION_ATTACH_DATA)
        share.type = "image/jpeg"

        val values = ContentValues()
        values.put(Images.Media.TITLE, "title")
        values.put(Images.Media.MIME_TYPE, "image/jpeg")
        val uri = ctx.contentResolver.insert(Images.Media.EXTERNAL_CONTENT_URI, values)

        val outStream: OutputStream = ctx.contentResolver.openOutputStream(uri)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
        outStream.close()

        share.putExtra(Intent.EXTRA_STREAM, uri)
        ctx.startActivity(Intent.createChooser(share, "Share Image"))
    }

    override fun run() {
        super.run()
        
        setUpCamera()
        openCamera()
    }
}
