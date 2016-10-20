package com.hunglo.bkflash;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.IOException;

public class MainActivity extends Activity implements SurfaceHolder.Callback {
    Camera camera1;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    boolean previewing = false;
    LayoutInflater controlInflater = null;

    ImageButton btnSwitch;

    boolean check = true;

    //    private Camera camera;
    private boolean isFlashOn;
    private boolean hasFlash;
    Camera.Parameters params;
    MediaPlayer mp;

    Bitmap bitmap;

    private AdView mAdView;
    private Button btnFullscreenAd;

    ImageButton btnFlash ;


    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Log.e("Hung", "onCreatrrrrrrrrrrrrrrrr");

        setContentView(R.layout.activity_main);

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);

        getWindow().setFormat(PixelFormat.UNKNOWN);
        surfaceView = (SurfaceView) findViewById(R.id.camerapreview);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        controlInflater = LayoutInflater.from(getBaseContext());
        View viewControl = controlInflater.inflate(R.layout.custom, null);
        ViewGroup.LayoutParams layoutParamsControl = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        this.addContentView(viewControl, layoutParamsControl);

        btnSwitch = (ImageButton) findViewById(R.id.btnSwitch1);
        // Star Button Switch

        // First check if device is supporting flashlight or not
        hasFlash = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!hasFlash) {
            AlertDialog alert = new AlertDialog.Builder(MainActivity.this)
                    .create();
            alert.setTitle("Error");
            alert.setMessage("Sorry, your device doesn't support flash light!");
            alert.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // closing the application
                    finish();
                }
            });
            alert.show();
            return;
        }

        btnSwitch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isFlashOn) {
                    // turn off flash
                    turnOffFlash();
                } else turnOnFlash();
            }
        });

        btnFlash = (ImageButton) findViewById(R.id.btnSwitch2);

        toggleButtonImage();

        btnFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Hung", "Button FlashBlink" + check);
                playSound();

                if (check == true) {
                    Log.e("Hung", "if check == true button");
                    check = false;
                } else {
                    Log.e("Hung", "if check == false button");
                    check = true;
                }

                blink(100, 10);
                Log.e("Hung", "Pass Blink");
            }
        });

        //End Button Switch

    }

    private void blink(final int delay, final int times) {
        Log.e("Hung", "Blink");
        if (check == false) {
            Log.e("Hung", "if == true");

            Thread t = new Thread() {
                public void run() {
                    try {
                        while (check == false) {
                            Log.e("Hung", "While Blink");
                            if (isFlashOn) {
                                turnOffFlashBlink();
                            } else {
                                turnOnFlashBlink();
                            }
                            sleep(delay);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("Hung", "Exception Blink");
                    }

                }
            };
            t.start();
        }
    }


    // Turning On flash
    private void turnOnFlash() {
        Log.e("Hung", "TurnonFlash");

        if (!isFlashOn) {
            if (camera1 == null || params == null) {
                return;
            }
            // play sound
            playSound();

            params = camera1.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera1.setParameters(params);
            camera1.startPreview();
            camera1.setDisplayOrientation(90);
            isFlashOn = true;

            // changing button/switch image
            toggleButtonImage();
        }

    }

    private void turnOnFlashBlink() {
        Log.e("Hung", "TurnonFLashBilnk");

        if (!isFlashOn) {
            if (camera1 == null || params == null) {
                return;
            }

            params = camera1.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera1.setParameters(params);
            camera1.startPreview();
            camera1.setDisplayOrientation(90);
            isFlashOn = true;

            // changing button/switch image
//            toggleButtonImage();
        }

    }


    // Turning Off flash
    private void turnOffFlash() {
        Log.e("Hung", "TurnOffFlash");

        if (isFlashOn) {
            if (camera1 == null || params == null) {
                return;
            }
            playSound();
            params = camera1.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera1.setParameters(params);
            camera1.stopPreview();
            isFlashOn = false;

            // changing button/switch image
            toggleButtonImage();
        }
    }

    private void turnOffFlashBlink() {
        Log.e("Hung", "Turnoff FlashBlink");

        if (isFlashOn) {
            if (camera1 == null || params == null) {
                return;
            }

            params = camera1.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera1.setParameters(params);
            isFlashOn = false;

            // changing button/switch image
//            toggleButtonImage();
        }
    }

    private void playSound() {
        Log.e("Hung", "Play sound");

        if (isFlashOn) {
            mp = MediaPlayer.create(MainActivity.this, R.raw.one);
        } else {
            mp = MediaPlayer.create(MainActivity.this, R.raw.one);
        }
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                mp.release();
            }
        });
        mp.start();
    }

    private void toggleButtonImage() {
        Log.e("Hung", "ToggleButtonImage");

        Bitmap biImage1, biImage2;

        Bitmap bitButton1;

        int width = 180;
        int height = 180;

        biImage1 = BitmapFactory.decodeResource(getResources(), R.drawable.flashlightonn);
        biImage2 = BitmapFactory.decodeResource(getResources(), R.drawable.flashlightoff);

        bitButton1 = BitmapFactory.decodeResource(getResources(), R.drawable.flashblink);

        biImage1 = Bitmap.createScaledBitmap(biImage1, width, height, true);
        biImage2 = Bitmap.createScaledBitmap(biImage2, width, height, true);

        bitButton1 = Bitmap.createScaledBitmap(bitButton1, width, height, true);


        btnFlash.setImageBitmap(bitButton1);

//        Bitmap biImage12 = Bitmap.createBitmap(biImage1, 0, 0, biImage1.getWidth(), biImage1.getHeight(), matrix, true);
//        Bitmap biImage22 = Bitmap.createBitmap(biImage2, 0, 0, biImage2.getWidth(), biImage2.getHeight(), matrix, true);

        if (isFlashOn) {
            btnSwitch.setImageBitmap(biImage1);
        } else {
            btnSwitch.setImageBitmap(biImage2);
        }
    }

    @Override
    protected void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();

        // on pause turn off the flash
        turnOffFlash();
        Log.e("Hung", "onPause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("Hung", "OnREstart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }

        // on resume turn on the flash
        if (hasFlash)
            turnOnFlash();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Hung", "OnStart");

        // on starting the app get the camera params
//        getCamera();
        if (camera1 == null) {
            try {
                camera1 = Camera.open();
                params = camera1.getParameters();
            } catch (RuntimeException e) {
                Log.e("Camera error:", e.getMessage());
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        turnOffFlash();
        Log.d("Hung", "ONStop");

        // on stop release the camera
        if (camera1 != null) {
            camera1.release();
            camera1 = null;
        }
    }


    // End FLash

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d("Hung", "SurfaceChanged");
        if (previewing) {
            camera1.stopPreview();
            previewing = false;
        }

        if (camera1 != null) {
            try {
                camera1.setPreviewDisplay(surfaceHolder);
                camera1.startPreview();
                camera1.setDisplayOrientation(90);
                previewing = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e("Hung", "SurfaceCreated");
//        camera1 = Camera.open();
//        params = camera1.getParameters();
        try {
            camera1 = Camera.open();
            params = camera1.getParameters();
        } catch (RuntimeException e) {
            Log.e("Camera error:", e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        try {
            camera1.stopPreview();
            camera1.release();
            camera1 = null;
            previewing = false;
            Log.e("Hung", "Hung ong SurfaceDestroy");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
