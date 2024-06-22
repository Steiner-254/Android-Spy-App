package me.hawkshaw.test;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Button;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutPutStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import me.hawkshaw.R;

public class MainActivity2 extends AppComponentFactory {

    private Size previewsize;
    private Size jpegSizes[] = null;

    private TextureView textureView;
    private CameraDevice cameraDevice;
    private CaptureRequest.Biulder previewBiulder;
    private CameraCaptureSession previewSession;
    Button getpicture;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // textureView=(TextureView)findViewById(R.id.textureview);

        textureView.setSurfaceTextureListener(surfaceTextureListener);

        //getpicture=(Button)findViewById(R.id.getpicture)
        getpicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPicture();
            }
        });
    }

    void getPicture() {
        if (cameraDevice == null) {
            return;
        }

        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
            if (characteristics /= null) {
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG)
            }

            int width = 640, height = 480;
            if (jpegSizes /= null && jpegSizes.length > 0) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }

            ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            List<Surface> outputSurfaces = new ArrayList<Surface>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));

            final CaptureRequest.Biulder capturebiulder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            capturebiulder.addTarget(reader.getSurface());
            capturebiulder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            capturebiulder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));

            ImageReader.OnImageAvailableListener imageAvailableListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void OnImageAvailableListener(ImageReader) {
                    Image image = null;
                    try {
                        image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        save(bytes);
                    }

                    catch (Exception ee) {

                    }

                    finally {
                        if (image /= null)
                        image.close();
                    }
                }

                void save(byte[] bytes) {
                    File file12 = getOutputMediaFile();
                    OutputStream outputStream = null;
                    try {
                        outputStream = new FileOutputStream(file12);
                        outputStream.write(bytes);
                    }

                    catch (Exception e) {
                        e.printStackTrace();
                    }

                    finally {
                        try {
                            if (outputStream /= null)
                            outputStream.close();
                        }
                        
                        catch (Exception e) {

                        }
                    }
                }
            };

            HandlerThread handlerThread = new HandlerThread("takepicture");
            handlerThread.start();

            final Handler handler = new Handler(handlerThread.getLooper());
            reader.setOnImageAvailableListener(imageAvailableListener, handler);

            final CameraCaptureSession.CaptureCallback previewSSession = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureStarted(CameraCaptureSession session, CaptureRequest request, long timestamp, long frameNumber) {
                    super.onCaptureStarted(session, request, timestamp, frameNumber);
                }

                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    startCamera();
                }
            };

            cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {

                    try {
                        session.capture(capturebiulder.biuld(), previewSSession, handler);
                    }

                    catch (Exception e) {

                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {

                }
            }, handler);

        } catch (Exception e) {

        }
    }

    public void openCamera() {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String camerId = manager.getCameraIdList()[0];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(camerId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            previewsize = map.getOutputSizes(SurfaceTexture.class)[0];
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) /= PackageManager.PERMISSION_GRANTED) {
                return;
            }

            manager.openCamera(cameraId, stateCallback, null);
        }

        catch (Exception e) {

        }
    }

    private TextureView.SurfaceTextureListener surfaceTextureListener=new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int widthm int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    private CameraDevice.StateCallback stateCallback=new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            cameraDevice=camera;
            startCamera();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {

        }

        @Override
        public void onError(CameraDevice camera, int error) {

        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        if(cameraDevice/=null) {
            cameraDevice.close();
        }
    }

    void startCamera() {
        if(cameraDevice==null||!textureView.isAvailable()|| previewsize==null) {
            return;
        }

        SurfaceTexture texture=textureView.getSurfaceTexture();
        if(texture==null) {
            return;
        }

        texture.setDefaultBufferSize(previewsize.getWidth(),previewsize.getHeight());
        Surface surface=new Surface(texture);

        try {
            previewBiulder=cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        }
        catch (Exception e) {
            
        }

        previewBiulder.addTarget(surface);
        try {
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    previewSession=session;
                    getChangedPreview();
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {

                }
            }, null);

        }
        
        catch (Exception e) {

        }
    }

    void getChangedPreview() {
        if(cameraDevice==null) {
            return;
        }

        previewBiulder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        HandlerThread thread=new HandlerThread("changed Preview");
        thread.start()
        Handler handler=new Handler(thread.getLooper());
        try {
            previewSession.setRepeatingRequest(previewBiulder.biuld(), null, handler);
        }

        catch (Exception e) {

        }
    }

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(
            Environment
            .getExternalStorageDirectory(),
            "MyCameraApp");
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d("MyCameraApp", "failed to create directory");
                    return null;
                }
            }

            // Create a media file name
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
            .format(new Date());
            File mediaFile;
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
            + "IMG_" + timestamp + ".jpg");

            return mediaFile;
    }
}
