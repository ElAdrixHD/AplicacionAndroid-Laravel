package com.example.rss.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.rss.R;
import com.example.rss.model.Reserva;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReservasAdapter extends RecyclerView.Adapter<ReservasAdapter.ReservasViewHolder> {

    ArrayList<Reserva> reservas;

    public ReservasAdapter() {
        this.reservas = new ArrayList<>();
    }

    public void setReservas(ArrayList<Reserva> reservas) {
        this.reservas = reservas;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReservasViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();

        // Inflate the custom layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View reservaView = inflater.inflate(R.layout.item_view, viewGroup, false);

        // Return a new holder instance
        return new ReservasViewHolder(reservaView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservasViewHolder reservasViewHolder, int i) {
        Reserva reserva = reservas.get(i);

        reservasViewHolder.fecha_reserva.setText(reserva.getFecha_reserva());
        reservasViewHolder.hora_inicio.setText(reserva.getHora_inicio());
        reservasViewHolder.hora_fin.setText(reserva.getHora_fin());
    }

    @Override
    public int getItemCount() {
        return reservas.size();
    }

    public int getId(int position){

        return this.reservas.get(position).getId();
    }

    public Reserva getAt(int position){
        Reserva reserva;
        reserva = this.reservas.get(position);
        return  reserva;
    }

    public void add(Reserva reserva) {
        this.reservas.add(reserva);
        notifyItemInserted(reservas.size() - 1);
        notifyItemRangeChanged(0, reservas.size() - 1);
    }

    public void modifyAt(Reserva reserva, int position) {
        this.reservas.set(position, reserva);
        notifyItemChanged(position);
    }

    public void removeAt(int position) {
        this.reservas.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(0, reservas.size() - 1);
    }

    class ReservasViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.textView1) TextView fecha_reserva;
        @BindView(R.id.textView2) TextView hora_inicio;
        @BindView(R.id.textView3) TextView hora_fin;

        public ReservasViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
