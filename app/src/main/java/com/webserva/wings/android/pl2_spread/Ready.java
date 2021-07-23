package com.webserva.wings.android.pl2_spread;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import static java.lang.System.exit;

public class Ready extends AppCompatActivity {
    private int ruleNumber = 1;
    private int ruleTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ready);

        Intent intent_from_ri = getIntent();
        int status_tag = intent_from_ri.getIntExtra("STATUS_TAG",0);
        ImageView rd_imageView_rule = findViewById(R.id.rd_imageView_rule);
        rd_imageView_rule.setImageResource(R.drawable.rule1);
        TextView rd_textView_rule = findViewById(R.id.rd_textView_rule);
        String rule1 = getString(R.string.rule1);
        String rule2 = getString(R.string.rule2);
        String rule3 = getString(R.string.rule3);
        rd_textView_rule.setText(rule1);
        TextView rd_textView_ruleNumber = findViewById(R.id.rd_textView_ruleNumber);
        TextView rd_textView_waiting = findViewById(R.id.rd_textView_waiting);
        String waiting = getString(R.string.rd_waiting);
        ImageButton rd_imageButton_left = findViewById(R.id.rd_imageButton_left);
        ImageButton rd_imageButton_right = findViewById(R.id.rd_imageButton_right);
        rd_imageButton_left.setEnabled(false);

        if(status_tag == 0) {
            ruleTotal = 3;
            rd_textView_ruleNumber.setText(ruleNumber + "/" + ruleTotal);
            rd_imageButton_left.setOnClickListener(v -> {
                ruleNumber -= 1;
                if(ruleNumber == 1) {
                    rd_imageButton_left.setEnabled(false);
                    rd_imageView_rule.setImageResource(R.drawable.rule1);
                    rd_textView_rule.setText(rule1);
                    rd_textView_ruleNumber.setText(ruleNumber + "/" + ruleTotal);
                }else if(ruleNumber == 2) {
                    rd_imageView_rule.setImageResource(R.drawable.rule2);
                    rd_textView_rule.setText(rule2);
                    rd_textView_ruleNumber.setText(ruleNumber + "/" + ruleTotal);
                    rd_imageButton_right.setEnabled(true);
                }
            });
            rd_imageButton_right.setOnClickListener(v -> {
                ruleNumber += 1;
                if(ruleNumber == 2) {
                    rd_imageView_rule.setImageResource(R.drawable.rule2);
                    rd_textView_rule.setText(rule2);
                    rd_textView_ruleNumber.setText(ruleNumber + "/" + ruleTotal);
                    rd_imageButton_left.setEnabled(true);
                }else if(ruleNumber == 3) {
                    rd_imageView_rule.setImageResource(R.drawable.rule3);
                    rd_textView_rule.setText(rule3);
                    rd_textView_ruleNumber.setText(ruleNumber + "/" + ruleTotal);
                    rd_imageButton_right.setEnabled(false);
                }
            });
        }else {
            ruleTotal = 2;
            rd_textView_ruleNumber.setText(ruleNumber + "/" + ruleTotal);
            rd_imageButton_left.setOnClickListener(v -> {
                ruleNumber -= 1;
                if(ruleNumber == 1) {
                    rd_imageButton_left.setEnabled(false);
                    rd_imageView_rule.setImageResource(R.drawable.rule1);
                    rd_textView_rule.setText(rule1);
                    rd_textView_ruleNumber.setText(ruleNumber + "/" + ruleTotal);
                    rd_imageButton_right.setEnabled(true);
                }
            });
            rd_imageButton_right.setOnClickListener(v -> {
                ruleNumber += 1;
                if(ruleNumber == 2) {
                    rd_imageButton_right.setEnabled(false);
                    rd_imageView_rule.setImageResource(R.drawable.rule2);
                    rd_textView_rule.setText(rule2);
                    rd_textView_ruleNumber.setText(ruleNumber + "/" + ruleTotal);
                    rd_imageButton_left.setEnabled(true);
                }
            });
        }


        ImageButton rd_imageButton_ready = findViewById(R.id.rd_imageButton_ready);
        rd_imageButton_ready.setOnClickListener(v -> {
            Client.sendMessage("ready");
            rd_imageButton_ready.setEnabled(false);
            rd_textView_waiting.setText(waiting);
        });

        //位置情報関係
        if (ActivityCompat.checkSelfPermission(
                Ready.this, Manifest.permission.ACTIVITY_RECOGNITION) ==
                PackageManager.PERMISSION_GRANTED) {
            // No Problem
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACTIVITY_RECOGNITION)) {
            // In an educational UI, explain to the user why your app requires this
            // permission for a specific feature to behave as expected. In this UI,
            // include a "cancel" or "no thanks" button that allows the user to
            // continue using your app without granting the permission.
            if (Build.VERSION.SDK_INT >= 29) {
                new AlertDialog.Builder(Client.context)
                        .setTitle("R.string.general_message")
                        .setMessage("このゲームを遊ぶには、歩行を検知するために身体活動の権限を許可する必要があります。")
                        .setPositiveButton("OK", null)
                        .show();
                requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION);
            }
        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            if (Build.VERSION.SDK_INT >= 29) {
                requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION);
            }
        }

        if (ActivityCompat.checkSelfPermission(
                Ready.this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            //action();
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(Client.context)
                    .setTitle("R.string.general_message")
                    .setMessage("このゲームを遊ぶには、位置情報の権限を許可する必要があります。")
                    .setPositiveButton("OK", null)
                    .show();
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    static void receiveMessage(String message) {
        String[] s = message.split("\\$");
        switch (s[0]) {
            case "start":
                Client.finishActivity();
                Intent intent_to_gm = new Intent(Client.context, Game.class);
                Client.startActivity(intent_to_gm);
                break;
        }
    }

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    //action();
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.general_message)
                            .setMessage("このゲームは位置情報と身体活動の取得を利用しないとプレイすることができません。")
                            .setPositiveButton("許可する", (dialog, which) -> {
                                Ready.this.requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                            })
                            .setNegativeButton("キャンセル", (dialog, which) -> {
                                exit(0);
                            })
                            .show();
                }
            });
}