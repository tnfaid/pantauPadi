package aplikasiku.padi.tnfaid.pantaupadi.User;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import aplikasiku.padi.tnfaid.pantaupadi.R;

public class Profil extends AppCompatActivity {
    Button btn_logout;
    String id, email;
    SharedPreferences sharedpreferences;

    public static final String TAG_ID = "id";
    public static final String TAG_EMAIL = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profil);

        btn_logout = (Button) findViewById(R.id.btn_logout);
        sharedpreferences = this.getSharedPreferences(Login.my_shared_preferences, Context.MODE_PRIVATE);
        btn_logout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // update login session ke FALSE dan mengosongkan nilai id dan username
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean(Login.session_status, false);
                editor.putString(TAG_ID, null);
                editor.putString(TAG_EMAIL, null);
                editor.commit();

                Intent intent = new Intent(Profil.this, Login.class);
                finish();
                startActivity(intent);
            }
        });
    }
}
