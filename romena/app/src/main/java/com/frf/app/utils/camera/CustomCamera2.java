package com.frf.app.utils.camera;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
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
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;

import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CustomCamera2 {
    private CameraDevice camera;
    private CameraManager manager;
    private Activity context;
    Listener listener;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    public CustomCamera2(final Activity context, final Listener listener) {
        this.context = context;
        this.listener = listener;
        this.manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        open(1);
    }

    @SuppressLint("MissingPermission")
    public void open(final int cameraId) {
        try {
            String[] cameraIds = manager.getCameraIdList();
            if(permissionCheck()){
                manager.openCamera(cameraIds[cameraId], new CameraDevice.StateCallback() {
                    @Override
                    public void onOpened(CameraDevice camera) {
                        CustomCamera2.this.camera = camera;
                        listener.OnOpened(camera);
                    }

                    @Override
                    public void onDisconnected(CameraDevice camera) {
                        CustomCamera2.this.camera = camera;
                        listener.OnDisconnected(camera);

                    }

                    @Override
                    public void onError(CameraDevice camera, int error) {
                        CustomCamera2.this.camera = camera;
                        listener.OnError(camera, error);
                    }
                }, null);
            }else{

            }

        } catch (Exception e) {
            // TODO handle
        }
    }

    boolean permissionCheck (){
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }else{
            return false;
        }
    }

    public void close (){
        if(camera != null)
            camera.close();
    }

    public int getOrientation(final int cameraId) {
        try {
            String[] cameraIds = manager.getCameraIdList();
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraIds[cameraId]);
            return characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        } catch (CameraAccessException e) {
            // TODO handle
            return 0;
        }
    }

    public StreamConfigurationMap getStreamConfig(final int cameraId) {
        try {
            String[] cameraIds = manager.getCameraIdList();
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraIds[cameraId]);
            return characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        } catch (CameraAccessException e) {
            // TODO handle
            return null;
        }
    }

    public void changeCamera (int ID){
        camera.close();
        open(ID);
    }



    public  void takePicture(Uri photoUri, TextureView textureView, final Handler mBackgroundHandler) {
        if(null == camera) {
            return;
        }

        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(camera.getId());
            Size[] jpegSizes = null;

            if (characteristics != null) {
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
            }

            int width = 640;
            int height = 480;
            if (jpegSizes != null && 0 < jpegSizes.length) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }

            ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            List<Surface> outputSurfaces = new ArrayList<Surface>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));
            final CaptureRequest.Builder captureBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

            // Orientation
            int rotation = context.getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));

            final File file = getOutputMediaFile(photoUri);//new File(Environment.getExternalStorageDirectory()+"/pic.jpg");

            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = null;
                    try {
                        image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        save(bytes);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (image != null) {
                            image.close();
                        }
                    }
                }
                private void save(byte[] bytes) throws IOException {
                    OutputStream output = null;
                    try {
                        output = new FileOutputStream(file);
                        output.write(bytes);
                    } finally {
                        if (null != output) {
                            output.close();
                        }
                    }
                }
            };
            reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);

            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    listener.TakePicture(file);
                }
            };


            camera.createCaptureSession(outputSurfaces,
                    new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        session.capture(captureBuilder.build(), captureListener, mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                }
            },
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private File getOutputMediaFile(Uri photoURI){
        File output = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if(photoURI != null){
            if(!output.exists()){
                output.mkdirs();
            }

            File put = new File(photoURI.getPath());
            output = put;
        }else{
            if(!output.exists()){
                output.mkdirs();
            }

            File put = newFile(output);
            output = put;
        }

        return output;
    }

    public File newFile(File mFilePath) {
        Calendar cal = Calendar.getInstance();
        long timeInMillis = cal.getTime().getTime();
        String mFileName = timeInMillis + ".jpeg";

        try {
            File newFile = new File(mFilePath.getAbsolutePath(), mFileName);
            newFile.createNewFile();
            return newFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface Listener{
        void OnOpened(CameraDevice camera);
        void OnDisconnected(CameraDevice camera);
        void OnError(CameraDevice camera, int error);
        void TakePicture(File file);
    }
}
