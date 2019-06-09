package com.example.rss.ui;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.rss.R;
import com.example.rss.model.Reserva;
import com.example.rss.network.ApiTokenRestClient;
import com.example.rss.util.DatePickerFragment;
import com.example.rss.util.SharedPreferencesManager;
import com.example.rss.util.TimePickerFragment;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddActivity extends AppCompatActivity implements Callback<Reserva> {
    public static final int OK = 1;

    @BindView(R.id.fecha_reserva) EditText fecha_reserva;
    @BindView(R.id.hora_inicio) EditText hora_inicio;
    @BindView(R.id.hora_fin) EditText hora_fin;
    @BindView(R.id.accept) Button accept;
    @BindView(R.id.cancel) Button cancel;

    ProgressDialog progreso;
    SharedPreferencesManager preferences;

    @OnClick(R.id.accept)
    public void clickAccept(View view){
        String n, l, e;
        Reserva s;

        hideSoftKeyboard();
        n = fecha_reserva.getText().toString();
        l = hora_inicio.getText().toString();
        e = hora_fin.getText().toString();
        if (n.isEmpty() || l.isEmpty())
            Toast.makeText(this, "Please, fill the name and the link", Toast.LENGTH_SHORT).show();
        else {
            s = new Reserva(n, l , e);
            connection(s);
        }
    }

    @OnClick(R.id.cancel)
    public void clickCancel(View view){
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        ButterKnife.bind(this);
        preferences = new SharedPreferencesManager(this);
    }

    private void connection(Reserva s) {
        progreso = new ProgressDialog(this);
        progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progreso.setMessage("Connecting . . .");
        progreso.setCancelable(false);
        progreso.show();

        //Call<Site> call = ApiRestClient.getInstance().createReserva("Bearer " + preferences.getToken(), s);
        Call<Reserva> call = ApiTokenRestClient.getInstance(preferences.getToken()).createReserva(s);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<Reserva> call, Response<Reserva> response) {
        progreso.dismiss();
        if (response.isSuccessful()) {
            Reserva site = response.body();
            Intent i = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt("id", site.getId());
            bundle.putString("fecha", site.getFecha_reserva());
            bundle.putString("inicio", site.getHora_inicio());
            bundle.putString("fin", site.getHora_fin());
            i.putExtras(bundle);
            setResult(OK, i);
            finish();
            showMessage("Added site ok");
        } else {
            StringBuilder message = new StringBuilder();
            message.append("Download error: " + response.code());
            if (response.body() != null)
                message.append("\n" + response.body());
            if (response.errorBody() != null)
                try {
                    message.append("\n" + response.errorBody().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            showMessage(message.toString());
        }
    }

    @Override
    public void onFailure(Call<Reserva> call, Throwable t) {
        progreso.dismiss();
        if (t != null)
            showMessage("Failure in the communication\n" + t.getMessage());
    }

    private void showMessage(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @OnClick({R.id.fecha_reserva, R.id.hora_inicio,R.id.hora_fin})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.fecha_reserva:
                showDatePickerDialog();
                break;
            case R.id.hora_inicio:
                showTimePickerDialog(true);
                break;
            case R.id.hora_fin:
                showTimePickerDialog(false);
                break;
        }
    }

    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                //  year + "-" + (month+1) + "-" + day
                String selectedDate = ""+year;
                if (month<9){
                    selectedDate += "-0"+(month+1);
                }else {
                    selectedDate += "-"+(month+1);
                }

                if (day<10){
                    selectedDate+= "-0"+day;
                }else {
                    selectedDate += "-"+day;
                }

                fecha_reserva.setText(selectedDate);
            }
        });
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void showTimePickerDialog(final boolean inicio) {
        TimePickerFragment newFragment = TimePickerFragment.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String selectedDate;
                if (hourOfDay < 10){
                    selectedDate = "0"+hourOfDay;
                }else {
                    selectedDate = ""+hourOfDay;
                }

                if (minute<10){
                    selectedDate += ":0"+minute;
                }else {
                    selectedDate += ":"+minute;
                }
                if (inicio){
                    hora_inicio.setText(selectedDate);
                }else {
                    hora_fin.setText(selectedDate);
                }
            }
        });
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
}

