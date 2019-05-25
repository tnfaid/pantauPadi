package aplikasiku.padi.tnfaid.pantaupadi.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.zip.Inflater;

import aplikasiku.padi.tnfaid.pantaupadi.R;
import aplikasiku.padi.tnfaid.pantaupadi.User.Login;

public class Profil extends Fragment {
    Button btn_logout;
    String id, email;
    SharedPreferences sharedpreferences;

    public static final String TAG_ID = "id";
    public static final String TAG_EMAIL = "email";

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_profil, container,false);

        btn_logout = (Button) view.findViewById(R.id.btn_logout);
        sharedpreferences = getActivity().getSharedPreferences(Login.my_shared_preferences, Context.MODE_PRIVATE);
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

                Intent intent = new Intent(getContext(), Login.class);
                startActivity(intent);
            }
        });
        return view;
    }

}
