package com.example.GestorTareasBDFirebase;


import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class BaseDatosTareas {

    private DatabaseReference referenciaTareas;

    public BaseDatosTareas() {
        FirebaseDatabase baseDatos = FirebaseDatabase.getInstance();
        referenciaTareas = baseDatos.getReference("tareas");  // Ruta donde se almacenan las tareas
    }

    // Método para obtener todas las tareas
    public void obtenerTareas(final BaseDatosCallback callback) {
        referenciaTareas.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Tarea> tareas = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Tarea tarea = snapshot.getValue(Tarea.class);
                    tareas.add(tarea);
                }
                callback.onSuccess(tareas);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFailure(databaseError.getMessage());
            }
        });
    }

    // Método para agregar una nueva tarea
    public void agregarTarea(Tarea tarea, final BaseDatosCallback callback) {
        String tareaId = referenciaTareas.push().getKey();
        tarea.setId(tareaId);  // Establecemos un ID único para la tarea
        referenciaTareas.child(tareaId).setValue(tarea)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Método para eliminar una tarea
    public void eliminarTarea(String tareaId, final BaseDatosCallback callback) {
        referenciaTareas.child(tareaId).removeValue()
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Método para actualizar una tarea completamente (sobre escribiendo todos los campos)
    public void actualizarTarea(Tarea tarea, final BaseDatosCallback callback) {
        referenciaTareas.child(tarea.getId()).setValue(tarea)  // Sobrescribimos toda la tarea con los nuevos valores
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Método para marcar una tarea como completada (cambiamos el valor de stability a 1)
    public void marcarTareaComoCompletada(String tareaId, final BaseDatosCallback callback) {
        referenciaTareas.child(tareaId).child("stability").setValue(1) // Cambiamos el valor de stability a 1 (completada)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Método para marcar una tarea como pendiente (cambiamos el valor de stability a 0)
    public void marcarTareaComoPendiente(String tareaId, final BaseDatosCallback callback) {
        referenciaTareas.child(tareaId).child("stability").setValue(0) // Cambiamos el valor de stability a 0 (pendiente)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Método para actualizar el estado de la tarea
    // Aquí gestionamos directamente el valor de stability (pendiente o completada)
    public void actualizarEstadoTarea(String tareaId, int nuevoEstado, final BaseDatosCallback callback) {
        referenciaTareas.child(tareaId).child("stability").setValue(nuevoEstado)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Interfaz para manejar las respuestas de Firebase
    public interface BaseDatosCallback {
        void onSuccess();  // Llamado cuando la operación fue exitosa
        void onSuccess(List<Tarea> tareas);  // Llamado cuando se obtienen las tareas
        void onFailure(String error);  // Llamado cuando ocurre un error
    }
}