package com.webserva.wings.android.pl2_spread;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class InitialLaunch extends Activity {
    private static final String TAG = "InitialLaunch(il)";
    Button startButton, idCheckButton;
    EditText idText;
    Boolean flag = false;
    Intent i;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_launch);
        i = new Intent(getApplication(), Title.class);

        idText = findViewById(R.id.il_editText_id);
        startButton = findViewById(R.id.il_button_start);
        startButton.setOnClickListener(v -> {
            if (checkId(idText.getText().toString(), false)) {
                ok();
            } else {
                Toast.makeText(getApplication(),R.string.ini_reg_fail, Toast.LENGTH_SHORT).show();
            }
        });
        idCheckButton = findViewById(R.id.il_button_checkID);
        idCheckButton.setOnClickListener(v -> {
            checkId(idText.getText().toString(), true);
        });
    }

    boolean checkId(String id, boolean showToast) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (id.isEmpty()) {
            Toast.makeText(getApplication(),R.string.ini_nullid_fail, Toast.LENGTH_SHORT).show();
        } else {
            db.collection("memberList").document(id).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "Document Exists: " + document.getData());
                        if (showToast)
                            Toast.makeText(getApplication(),R.string.ini_id_no_use, Toast.LENGTH_SHORT).show();
                        flag = false;
                    } else {
                        if (showToast)
                            Toast.makeText(getApplication(),R.string.ini_id_can_use, Toast.LENGTH_SHORT).show();
                        flag = true;
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            });
        }
        return flag;
    }

    private void ok() {
        Client.init(idText.getText().toString(), true);
        Client.startActivity(i);
    }
}