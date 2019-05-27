package aplikasiku.padi.tnfaid.pantaupadi;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;

import aplikasiku.padi.tnfaid.pantaupadi.Fragment.AmbilGambar;
import aplikasiku.padi.tnfaid.pantaupadi.Fragment.AmbilGambarSetHsv;
import aplikasiku.padi.tnfaid.pantaupadi.Fragment.Profil;
import aplikasiku.padi.tnfaid.pantaupadi.Fragment.TambahInfo;
import aplikasiku.padi.tnfaid.pantaupadi.Fragment.Tentang;

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
//        fragment penempatan yang pertam muncul
        getFragmentPage(new AmbilGambar());


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment = null;

//                menentukan halaman fragment yang akan tampil
                switch (menuItem.getItemId()){
                    case R.id.ambil_gambar:
                        fragment = new AmbilGambar();
                        break;
                    case R.id.tambah_info:
                        fragment = new TambahInfo();
                        break;
                    case R.id.profil:
                        fragment = new Profil();
                        break;
                    case R.id.tentang:
                        fragment = new Tentang();
                        break;
                    case R.id.ambil_gambar_set_hsv:
                        fragment = new AmbilGambarSetHsv();
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