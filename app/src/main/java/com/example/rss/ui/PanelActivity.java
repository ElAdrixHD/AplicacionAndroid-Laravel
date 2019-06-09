package com.example.rss.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.rss.MainActivity;
import com.example.rss.R;
import com.example.rss.adapter.ClickListener;
import com.example.rss.adapter.RecyclerTouchListener;
import com.example.rss.adapter.ReservasAdapter;
import com.example.rss.model.LogoutResponse;
import com.example.rss.model.Reserva;
import com.example.rss.model.Site;
import com.example.rss.network.ApiTokenRestClient;
import com.example.rss.util.SharedPreferencesManager;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PanelActivity extends AppCompatActivity {

    public static final int ADD_CODE = 100;
    public static final int UPDATE_CODE = 200;
    public static final int OK = 1;
    public static final String MAIL = "mail";

   @BindView(R.id.floatingActionButton) FloatingActionButton fab;

    @BindView(R.id.recyclerView) RecyclerView recyclerView;

    int positionClicked;
    private ReservasAdapter adapter;

    ProgressDialog progreso;
    //ApiService apiService;
    SharedPreferencesManager preferences;

    @OnClick(R.id.floatingActionButton)
    public void Click(View view) {
        Intent i = new Intent(this, AddActivity.class);
        startActivityForResult(i, ADD_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panel);

        ButterKnife.bind(this);
        preferences = new SharedPreferencesManager(this);
        //showMessage("panel: " + preferences.getToken());

        //Initialize RecyclerView
        adapter = new ReservasAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        //manage click
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                showPopup(view, position);
            }
            @Override
            public void onLongClick(View view, int position) {
                /*Intent emailIntent = new Intent(getApplicationContext(), EmailActivity.class);
                emailIntent.putExtra(MAIL, adapter.getAt(position).getFecha_reserva());
                startActivity(emailIntent);*/
            }
        }));

        ApiTokenRestClient.deleteInstance();

        downloadReservas();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.refresh:
                //petición al servidor para descargar de nuevo los sitios
                downloadReservas();
                break;

            case R.id.exit:
                //petición al servidor para anular el token (a la ruta /api/logout)
                Call<LogoutResponse> call = ApiTokenRestClient.getInstance(preferences.getToken()).logout();
                call.enqueue(new Callback<LogoutResponse>() {
                    @Override
                    public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {

                    }

                    @Override
                    public void onFailure(Call<LogoutResponse> call, Throwable t) {

                    }
                });
                preferences.saveToken(null, null);
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                break;
        }
        return true;
    }

    private void downloadReservas() {
        progreso = new ProgressDialog(this);
        progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progreso.setMessage("Connecting . . .");
        progreso.setCancelable(false);
        progreso.show();

        //Call<ArrayList<Site>> call = ApiRestClient.getInstance().getReservas("Bearer " + preferences.getToken());
        Call<ArrayList<Reserva>> call = ApiTokenRestClient.getInstance(preferences.getToken()).getReservas();
        call.enqueue(new Callback<ArrayList<Reserva>>() {
            @Override
            public void onResponse(Call<ArrayList<Reserva>> call, Response<ArrayList<Reserva>> response) {
                progreso.dismiss();
                if (response.isSuccessful()) {
                    adapter.setReservas(response.body());
                    showMessage("Reservas downloaded ok");
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
            public void onFailure(Call<ArrayList<Reserva>> call, Throwable t) {
                progreso.dismiss();
                if (t != null)
                    showMessage("Failure in the communication\n" + t.getMessage());
            }
        });
    }

    private void showMessage(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Reserva reserva = new Reserva();

        if (requestCode == ADD_CODE)
            if (resultCode == OK) {
                reserva.setId(data.getIntExtra("id", 1));
                reserva.setFecha_reserva(data.getStringExtra("fecha"));
                reserva.setHora_inicio(data.getStringExtra("inicio"));
                reserva.setHora_fin(data.getStringExtra("fin"));
                adapter.add(reserva);
            }

        if (requestCode == UPDATE_CODE)
            if (resultCode == OK) {
                reserva.setId(data.getIntExtra("id", 1));
                reserva.setFecha_reserva(data.getStringExtra("fecha"));
                reserva.setHora_inicio(data.getStringExtra("inicio"));
                reserva.setHora_fin(data.getStringExtra("fin"));
                adapter.modifyAt(reserva, positionClicked);
            }
    }

    private void showPopup(View v, final int position) {
        PopupMenu popup = new PopupMenu(this, v);
        // Inflate the menu from xml
        popup.getMenuInflater().inflate(R.menu.popup_change, popup.getMenu());
        // Setup menu item selection
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.modify_site:
                        modify(adapter.getAt(position));
                        positionClicked = position;
                        return true;
                    case R.id.delete_site:
                        confirm(adapter.getAt(position).getId(), adapter.getAt(position).getFecha_reserva(), position);
                        return true;
                    default:
                        return false;
                }
            }
        });
        // Show the menu
        popup.show();
    }

    private void modify(Reserva r) {
        Intent i = new Intent(this, UpdateActivity.class);
        i.putExtra("reserva", r);
        startActivityForResult(i, UPDATE_CODE);
    }

    private void confirm(final int idReserva, String name, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(name + "\nDo you want to delete?")
                .setTitle("Delete")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        connection(position);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    private void connection(final int position) {
        //Call<ResponseBody> call = ApiRestClient.getInstance().deleteReserva("Bearer " + preferences.getToken(), adapter.getId(position));
        Call<ResponseBody> call = ApiTokenRestClient.getInstance(preferences.getToken()).deleteReserva(adapter.getId(position));
        progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progreso.setMessage("Connecting . . .");
        progreso.setCancelable(false);
        progreso.show();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progreso.dismiss();
                if (response.isSuccessful()) {
                    adapter.removeAt(position);
                    showMessage("Reserva deleted OK");
                } else {
                    StringBuilder message = new StringBuilder();
                    message.append("Error deleting a site: " + response.code());
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
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progreso.dismiss();
                if (t != null)
                    showMessage("Failure in the communication\n" + t.getMessage());
            }
        });
    }
}
