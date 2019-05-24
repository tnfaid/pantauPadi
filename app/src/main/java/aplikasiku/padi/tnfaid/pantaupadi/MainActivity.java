package aplikasiku.padi.tnfaid.pantaupadi;


import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.SurfaceView;
import android.widget.Button;

import aplikasiku.padi.tnfaid.pantaupadi.Fragment.ambilGambar;
import aplikasiku.padi.tnfaid.pantaupadi.Fragment.keluar;
import aplikasiku.padi.tnfaid.pantaupadi.Fragment.profil;
import aplikasiku.padi.tnfaid.pantaupadi.Fragment.tambahInfo;
import aplikasiku.padi.tnfaid.pantaupadi.Fragment.tentang;
import aplikasiku.padi.tnfaid.pantaupadi.User.Login;

public class MainActivity extends AppCompatActivity {
    private static final String  TAG              = "MainActivity";

    Button btn_logout;
    SharedPreferences sharedpreferences;
    public MainActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.color_blob_detection_surface_view);
        btn_logout = (Button) findViewById(R.id.btn_logout);

        sharedpreferences = getSharedPreferences(Login.my_shared_preferences, Context.MODE_PRIVATE);

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // update login session ke FALSE dan mengosongkan nilai id dan username
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean(Login.session_status, false);
                editor.commit();

                Intent intent = new Intent(MainActivity.this, Login.class);
                finish();
                startActivity(intent);
            }
        });

//        fragment penempatan yang pertam muncul
        getFragmentPage(new ambilGambar());


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment = null;

//                menentukan halaman fragment yang akan tampil
                switch (menuItem.getItemId()){
                    case R.id.ambil_gambar:
                        fragment = new ambilGambar();
                        break;
                    case R.id.tambah_info:
                        fragment = new tambahInfo();
                        break;
                    case R.id.profil:
                        fragment = new profil();
                        break;
                    case R.id.tentang:
                        fragment = new tentang();
                        break;
                    case R.id.keluar:
                        fragment = new keluar();
                        break;
                }
                return getFragmentPage(fragment);
            }
        });
    }

    private boolean getFragmentPage(Fragment fragment) {
        if (fragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }


}