package aplikasiku.padi.tnfaid.pantaupadi.User;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import aplikasiku.padi.tnfaid.pantaupadi.R;
import aplikasiku.padi.tnfaid.pantaupadi.app.MySingleton;
import aplikasiku.padi.tnfaid.pantaupadi.util.Server;

public class SignUp extends AppCompatActivity {

    ProgressDialog pDialog;
    Button btn_sign_up, btn_login;
    EditText txt_firstName, txt_lastName, txt_email,  txt_password, txt_confirm_password;
    Intent intent;

    int success;
    ConnectivityManager conMgr;

    private String url = Server.URL + "ApiRegister.php";

    private static final String TAG = SignUp.class.getSimpleName();

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection",
                        Toast.LENGTH_LONG).show();
            }
        }

        btn_login = (Button) findViewById(R.id.btn_login);
        btn_sign_up = (Button) findViewById(R.id.btn_sign_up);
        txt_firstName = (EditText) findViewById(R.id.txt_firstName);
        txt_lastName = (EditText) findViewById(R.id.txt_lastName);
        txt_email = (EditText) findViewById(R.id.txt_email);
//        txt_mobile = (EditText)findViewById(R.id.txt_mobile);
//        txt_country = (EditText)findViewById(R.id.txt_country);
        txt_password = (EditText) findViewById(R.id.txt_password);
        txt_confirm_password = (EditText) findViewById(R.id.txt_confirm_password);

        btn_login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                intent = new Intent(SignUp.this, Login.class);
                finish();
                startActivity(intent);
            }
        });

        btn_sign_up.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String first_name = txt_firstName.getText().toString();
                String last_name = txt_lastName.getText().toString();
                String email = txt_email.getText().toString();
//                String mobile = txt_mobile.getText().toString();
//                String country = txt_country.getText().toString();
                String password = txt_password.getText().toString();
                String confirm_password = txt_confirm_password.getText().toString();

                if (conMgr.getActiveNetworkInfo() != null
                        && conMgr.getActiveNetworkInfo().isAvailable()
                        && conMgr.getActiveNetworkInfo().isConnected()) {
                    checkRegister(first_name,last_name, email, password, confirm_password);
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void checkRegister(final String first_name, final String last_name, final String email, final String password, final String confirm_password) {
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Register ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Check for error node in json
                    if (success == 1) {

                        Log.e("Successfully Register!", jObj.toString());

                        Toast.makeText(getApplicationContext(),
                                jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                        txt_firstName.setText("");
                        txt_lastName.setText("");
                        txt_email.setText("");
//                        txt_mobile.setText("");
//                        txt_country.setText("");
                        txt_password.setText("");
                        txt_confirm_password.setText("");

                    } else {
                        Toast.makeText(getApplicationContext(),
                                jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

                hideDialog();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("first_name", first_name);
                params.put("last_name", last_name);
                params.put("email", email);
//                params.put("mobile", mobile);
//                params.put("country", country);
                params.put("password", password);
                params.put("confirm_password", confirm_password);

                return params;
            }

        };

        // Adding request to request queue
        MySingleton.getInstance(this).addToRequestQueue(strReq);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        intent = new Intent(SignUp.this, SignUp.class);
        finish();
        startActivity(intent);
    }
}
