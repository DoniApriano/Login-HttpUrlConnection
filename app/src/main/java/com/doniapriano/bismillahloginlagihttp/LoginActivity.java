package com.doniapriano.bismillahloginlagihttp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;
    String email, password;
    LocalStorage localStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        localStorage = new LocalStorage(LoginActivity.this);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);

        etEmail.setText("iclarricoates3@clickbank.net");
        etPassword.setText("fTa9aI71rEm");

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLogin();
            }
        });
    }

    private void checkLogin() {
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
        if (email.isEmpty() || password.isEmpty()) {
            alert("Isi Semua Field");
        } else {
            sendLogin();
        }
    }

    private void sendLogin() {
        JSONObject params = new JSONObject();
        try {
            params.put("email",email);
            params.put("password",password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String data = params.toString();
        String url = getString(R.string.api_server) + "/Auth";

        new Thread(new Runnable() {
            @Override
            public void run() {
                Http http = new Http(LoginActivity.this,url);
                http.setMethod("post");
                http.setData(data);
                http.send();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Integer code = http.getStatusCode();
                        System.out.println(code);
                        if (code == 200) {
                            try {
                                JSONObject response = new JSONObject(http.getResponse());
                                String token = response.getString("token");
                                localStorage.setToken(token);
                                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else  {
                            Toast.makeText(LoginActivity.this, "Gagal Melakukan Login", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }

    private void alert(String message){
        new AlertDialog.Builder(this)
                .setTitle("Failed")
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }
}