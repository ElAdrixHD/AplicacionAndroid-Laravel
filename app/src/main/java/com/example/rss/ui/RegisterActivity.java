package com.example.rss.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rss.MainActivity;
import com.example.rss.R;
import com.example.rss.model.RegisterResponse;
import com.example.rss.network.ApiRestClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private ProgressDialog progressDialog;

    @BindView(R.id.input_name) EditText nameText;
    @BindView(R.id.input_email) EditText emailText;
    @BindView(R.id.input_password) EditText passwordText;
    @BindView(R.id.input_reEnterPassword) EditText reEnterPasswordText;
    @BindView(R.id.registerButton) Button registerButton;
    @BindView(R.id.loginButton) Button loginButton;

    @OnClick(R.id.registerButton)
    public void register(View view) {
        Log.d(TAG, "Registro");

        if (validate() == false) {
            showMessage("Register failed");
            registerButton.setEnabled(true);
        } else {
            sendToServer();
        }

    }

    @OnClick(R.id.loginButton)
    public void login(View view) {
        // Finish the registration and return to the Login activity
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        // Cancelar el registro
        setResult(RESULT_CANCELED, null);
        startActivity(intent);
        finish();
        //overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);
        }

    public boolean validate() {
        boolean valid = true;

        String name = nameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        String reEnterPassword = reEnterPasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            nameText.setError("at least 3 characters");
            valid = false;
        } else {
            nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("enter a valid email address");
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        if ( !(reEnterPassword.equals(password))) {
            reEnterPasswordText.setError("Password Do not match");
            valid = false;
        } else {
            reEnterPasswordText.setError(null);
        }

        return valid;
    }

    private void sendToServer() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account ...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        registerButton.setEnabled(false);

        final String name = nameText.getText().toString();
        final String email = emailText.getText().toString();
        final String password = passwordText.getText().toString();

        Call<RegisterResponse> call = ApiRestClient.getInstance().register(name, email, password);
        //User user = new User(name, email, password);
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                progressDialog.dismiss();
                //onRegisterSuccess();
                registerButton.setEnabled(true);
                if (response.isSuccessful()) {
                    //Log.d("onResponse", "" + response.body().getToken());
                    //enviar al Login para entrar después de validar el email
                    registerButton.setEnabled(true);

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("email", email );
                    resultIntent.putExtra("password", password );
                    //guardar el token en shared preferences
                    //resultIntent.putExtra("token", response.body().getToken() );
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    try {
                        JSONObject errorObject = new JSONObject(response.errorBody().string());
                        showMessage(errorObject.getString("error"));
                    } catch (IOException e) {
                        e.printStackTrace();
                        showMessage(e.getMessage());
                    } catch (JSONException e) {
                        e.printStackTrace();
                        showMessage(e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                progressDialog.dismiss();
                Log.d("onFailure", t.getMessage());
                showMessage(t.getMessage());
                registerButton.setEnabled(true);
            }
        });
    }

    private void showMessage(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

}
