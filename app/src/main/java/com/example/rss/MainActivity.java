package com.example.rss;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.rss.model.LoginResponse;
import com.example.rss.network.ApiRestClient;
import com.example.rss.ui.PanelActivity;
import com.example.rss.ui.RegisterActivity;
import com.example.rss.util.SharedPreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public static final String APP = "MyApp";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String TOKEN = "token";
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_REGISTER = 1;
    SharedPreferencesManager preferences;
    @BindView(R.id.input_email)
    EditText emailText;
    @BindView(R.id.input_password)
    EditText passwordText;
    @BindView(R.id.loginButton)
    Button loginButton;
    @BindView(R.id.registerButton)
    Button registerButton;
    private ProgressDialog progressDialog;

    @OnClick(R.id.loginButton)
    public void login(View view) {
        if (validate() == false) {
            showMessage("Error al validar los datos");
        } else {
            loginByServer();
        }
    }

    @OnClick(R.id.registerButton)
    public void register(View view) {
        // Start the Register activity
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivityForResult(intent, REQUEST_REGISTER);
        //finish();
        //overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        preferences = new SharedPreferencesManager(this);
        emailText.setText(preferences.getEmail());
        passwordText.setText(preferences.getPassword());
    }

    private void loginByServer() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Login ...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        loginButton.setEnabled(false);

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        Call<LoginResponse> call = ApiRestClient.getInstance().login(email, password);
        //User user = new User(name, email, password);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                progressDialog.dismiss();
                //onRegisterSuccess();
                if (response.isSuccessful()) {
                    Log.d("onResponse", "" + response.body());
                    //showMessage(response.body().getToken());
                    //guardar token en shared preferences
                    preferences.save(emailText.getText().toString(), passwordText.getText().toString(), response.body().getToken());
                    startActivity(new Intent(getApplicationContext(), PanelActivity.class));
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
                    loginButton.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                progressDialog.dismiss();
                Log.d("onFailure", t.getMessage());
                showMessage(t.getMessage());
                loginButton.setEnabled(true);
            }
        });
    }

    private void showMessage(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_REGISTER) {
            if (resultCode == RESULT_OK) {
                // TODO: Implement successful signup logic here
                // Por defecto se hace login automáticamente después del registro
                // Habría que validar el email antes de realizar login

                //Guardar token y lanzar Panel
                preferences.save(
                        data.getExtras().getString("email"),
                        data.getExtras().getString("password"));
                        //data.getExtras().getString("token"));
                //startActivity(new Intent(this, PanelActivity.class));
                //finish();
                emailText.setText(data.getExtras().getString("email"));
                passwordText.setText(data.getExtras().getString("password"));
            } else if (requestCode == RESULT_CANCELED) {
                //no hacer nada, volver al login
                showMessage("Registro cancelado");
            }
        }
    }

    public boolean validate() {
        boolean valid = true;

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("enter a valid email address");
            requestFocus(emailText);
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty()) {
            passwordText.setError("Password is empty");
            requestFocus(passwordText);
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}

