package com.example.GestorTareasBDFirebase;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tarea_7_gestortareas.R;

import java.util.List;

public class TareaAdapter extends RecyclerView.Adapter<TareaAdapter.TareaViewHolder> {

    private final List<Tarea> listaTareas;
    private final OnTareaClickListener listener;

    // Constructor que ahora acepta un listener con el método actualizarTareaBD
    public TareaAdapter(List<Tarea> listaTareas, OnTareaClickListener listener) {
        this.listaTareas = listaTareas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TareaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vistaDeItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tarea, parent, false);
        return new TareaViewHolder(vistaDeItem);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TareaViewHolder holder, int position) {
        Tarea tareaActual = listaTareas.get(position);

        holder.asignaturaTextView.setText(tareaActual.getAsignatura());
        holder.tituloTextView.setText(tareaActual.getTitulo());
        holder.descripcionTextView.setText(tareaActual.getDescripcion());
        holder.fechaEntregaTextView.setText("Fecha de entrega: " + tareaActual.getFechaEntrega());
        holder.horaEntregaTextView.setText("Hora de entrega: " + tareaActual.getHoraEntrega());

        // Cambiar el estado de la tarea dependiendo de 'stability'
        holder.estadoTextView.setText(tareaActual.getStability() == 1 ? "Completada" : "Pendiente");

        // Cuando se hace clic en el item de la tarea
        holder.itemView.setOnClickListener(v -> listener.onTareaClick(tareaActual));

        // Cuando se hace clic en el estado de la tarea (completada o pendiente)
        holder.estadoTextView.setOnClickListener(v -> {
            // Cambiar el estado local de stability
            tareaActual.setStability(tareaActual.getStability() == 1 ? 0 : 1);  // Cambiar entre 1 y 0
            listener.actualizarTareaBD(tareaActual);  // Llamar al método para actualizar la base de datos
            Toast.makeText(holder.itemView.getContext(), "Tarea marcada como " + (tareaActual.getStability() == 1 ? "completada" : "pendiente"), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return listaTareas.size();
    }

    public static class TareaViewHolder extends RecyclerView.ViewHolder {
        TextView asignaturaTextView;
        TextView tituloTextView;
        TextView descripcionTextView;
        TextView fechaEntregaTextView;
        TextView horaEntregaTextView;
        TextView estadoTextView;

        public TareaViewHolder(@NonNull View itemView) {
            super(itemView);
            asignaturaTextView = itemView.findViewById(R.id.asignaturaTextView);
            tituloTextView = itemView.findViewById(R.id.tituloTextView);
            descripcionTextView = itemView.findViewById(R.id.descripcionTextView);
            fechaEntregaTextView = itemView.findViewById(R.id.fechaEntregaTextView);
            horaEntregaTextView = itemView.findViewById(R.id.horaEntregaTextView);
            estadoTextView = itemView.findViewById(R.id.estadoTextView);
        }
    }

    // Interfaz que incluye el método para actualizar la tarea en la base de datos
    public interface OnTareaClickListener {
        void onTareaClick(Tarea tarea); // Método para manejar el clic en una tarea
        void actualizarTareaBD(Tarea tarea);  // Método para actualizar la tarea en la base de datos
    }
}