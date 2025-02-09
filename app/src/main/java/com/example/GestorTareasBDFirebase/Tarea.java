package com.example.GestorTareasBDFirebase;

import android.os.Parcel;
import android.os.Parcelable;

// Clase Tarea: representa una tarea que puede ser asignada y completada.
// Implementa Parcelable para permitir que se envíen objetos Tarea entre componentes de Android (como Activities y Fragments).
// Clase Tarea: representa una tarea que puede ser asignada y completada.
// Implementa Parcelable para permitir que se envíen objetos Tarea entre componentes de Android (como Activities y Fragments).
public class Tarea implements Parcelable {

    // Atributos de la clase Tarea:
    private String id;
    private String asignatura;
    private String titulo;
    private String descripcion;
    private String fechaEntrega;
    private String horaEntrega;
    private int stability;  // Ahora el estado de la tarea lo gestionamos con 'stability' (0: pendiente, 1: completada)

    // Constructor con ID
    public Tarea(String id, String asignatura, String titulo, String descripcion, String fechaEntrega, String horaEntrega, int stability) {
        this.id = id;
        this.asignatura = asignatura;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaEntrega = fechaEntrega;
        this.horaEntrega = horaEntrega;
        this.stability = stability;
    }

    // Constructor sin ID (para insertar tareas)
    public Tarea(String asignatura, String titulo, String descripcion, String fechaEntrega, String horaEntrega, int stability) {
        this.asignatura = asignatura;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaEntrega = fechaEntrega;
        this.horaEntrega = horaEntrega;
        this.stability = stability;
    }

    // Constructor sin argumentos (requerido por Firebase)
    public Tarea() {
        // Constructor vacío necesario para deserialización
    }

    // Métodos "getter" y "setter" para cada atributo.
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAsignatura() {
        return asignatura;
    }

    public void setAsignatura(String asignatura) {
        this.asignatura = asignatura;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(String fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public String getHoraEntrega() {
        return horaEntrega;
    }

    public void setHoraEntrega(String horaEntrega) {
        this.horaEntrega = horaEntrega;
    }

    public int getStability() {
        return stability;
    }

    public void setStability(int stability) {
        this.stability = stability;
    }

    // Parcelable: permite "empaquetar" objetos Tarea para enviarlos entre Activities o Fragments.

    // Constructor utilizado al leer el objeto desde un Parcel (esto se usa para enviar la tarea entre componentes).
    protected Tarea(Parcel in) {
        id = in.readString();  // Cambiado a String
        asignatura = in.readString();
        titulo = in.readString();
        descripcion = in.readString();
        fechaEntrega = in.readString();
        horaEntrega = in.readString();
        stability = in.readInt();  // Leemos el valor de stability (0 o 1).
    }

    // Este es el creador de Parcelable que facilita la creación del objeto desde un Parcel.
    public static final Creator<Tarea> CREATOR = new Creator<Tarea>() {
        @Override
        public Tarea createFromParcel(Parcel in) {
            return new Tarea(in);  // Llamamos al constructor que lee del Parcel.
        }

        @Override
        public Tarea[] newArray(int size) {
            return new Tarea[size];  // Crear un array de Tarea con el tamaño dado.
        }
    };

    // El método describeContents() devuelve los contenidos del Parcel, generalmente se usa para los objetos con tipos complejos.
    @Override
    public int describeContents() {
        return 0;
    }

    // El método writeToParcel escribe el objeto Tarea en un Parcel, permitiendo que se pueda enviar entre componentes.
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);  // Cambiado a String
        dest.writeString(asignatura);
        dest.writeString(titulo);
        dest.writeString(descripcion);
        dest.writeString(fechaEntrega);
        dest.writeString(horaEntrega);
        dest.writeInt(stability);  // Escribe el valor de stability (0 o 1).
    }
}