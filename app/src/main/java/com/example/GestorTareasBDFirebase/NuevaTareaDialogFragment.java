package com.example.GestorTareasBDFirebase;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.tarea_7_gestortareas.R;

import java.util.Calendar;

// Clase que representa un cuadro de diálogo para agregar o editar tareas
public class NuevaTareaDialogFragment extends DialogFragment {

    private EditText campoTitulo;
    private EditText campoDescripcion;
    private EditText campoFechaEntrega;
    private EditText campoHoraEntrega;
    private Spinner spinnerAsignatura;
    private OnTareaGuardadaListener listener;
    private Tarea tareaAEditar; // Tarea a editar, si aplica

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getActivity() == null) {
            return super.onCreateDialog(savedInstanceState);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = requireActivity().getLayoutInflater();
        View vista = layoutInflater.inflate(R.layout.agregar_tarea, null);

        campoTitulo = vista.findViewById(R.id.titulo);
        campoDescripcion = vista.findViewById(R.id.descripcion);
        campoFechaEntrega = vista.findViewById(R.id.fecha);
        campoHoraEntrega = vista.findViewById(R.id.hora);
        spinnerAsignatura = vista.findViewById(R.id.Spinner);

        campoFechaEntrega.setOnClickListener(v -> mostrarDatePickerDialog());
        campoHoraEntrega.setOnClickListener(v -> mostrarTimePickerDialog());

        if (getArguments() != null && getArguments().containsKey("homework")) {
            tareaAEditar = getArguments().getParcelable("homework");
            if (tareaAEditar != null) {
                campoTitulo.setText(tareaAEditar.getTitulo());
                campoDescripcion.setText(tareaAEditar.getDescripcion());
                campoFechaEntrega.setText(tareaAEditar.getFechaEntrega());
                campoHoraEntrega.setText(tareaAEditar.getHoraEntrega());
                spinnerAsignatura.setSelection(getIndice(spinnerAsignatura, tareaAEditar.getAsignatura()));
            }
        }

        Button botonGuardar = vista.findViewById(R.id.guardar);
        // Dentro del onClickListener del botón guardar:
        botonGuardar.setOnClickListener(v -> {
            if (validarEntradas()) {
                // Obtener valores actuales de los campos
                String asignatura = spinnerAsignatura.getSelectedItem().toString();
                String titulo = campoTitulo.getText().toString();
                String descripcion = campoDescripcion.getText().toString();
                String fechaEntrega = campoFechaEntrega.getText().toString();
                String horaEntrega = campoHoraEntrega.getText().toString();

                Tarea tarea;
                if (tareaAEditar != null) {
                    // Crear nueva tarea con el mismo ID pero nuevos valores
                    tarea = new Tarea(
                            tareaAEditar.getId(),
                            asignatura,
                            titulo,
                            descripcion,
                            fechaEntrega,
                            horaEntrega,
                            tareaAEditar.getStability() // Mantener el estado actual
                    );
                } else {
                    // Nueva tarea (ID se generará en Firebase)
                    tarea = new Tarea(
                            "",
                            asignatura,
                            titulo,
                            descripcion,
                            fechaEntrega,
                            horaEntrega,
                            0
                    );
                }

                if (listener != null) {
                    listener.onTareaGuardada(tarea);
                }
                dismiss();
            }
        });

        Button botonCancelar = vista.findViewById(R.id.cancelar);
        botonCancelar.setOnClickListener(v -> dismiss());

        builder.setView(vista);
        return builder.create();
    }

    private int getIndice(Spinner spinnerAsignatura, String asignatura) {
        for (int i = 0; i < spinnerAsignatura.getCount(); i++) {
            if (spinnerAsignatura.getItemAtPosition(i).toString().equalsIgnoreCase(asignatura)) {
                return i;
            }
        }
        return 0;
    }

    public interface OnTareaGuardadaListener {
        void onTareaGuardada(Tarea tarea);
    }

    public void setOnTareaGuardadaListener(OnTareaGuardadaListener listener) {
        this.listener = listener;
    }

    private void mostrarDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        if (getContext() == null) return;
        new DatePickerDialog(
                getContext(),
                (DatePicker view, int year, int month, int dayOfMonth) -> {
                    campoFechaEntrega.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void mostrarTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        if (getContext() == null) return;
        new TimePickerDialog(
                getContext(),
                (TimePicker view, int hourOfDay, int minute) -> {
                    campoHoraEntrega.setText(String.format("%02d:%02d", hourOfDay, minute));
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        ).show();
    }

    private boolean validarEntradas() {
        if (TextUtils.isEmpty(campoTitulo.getText())) {
            campoTitulo.setError("El título es obligatorio");
            return false;
        }
        if (TextUtils.isEmpty(campoDescripcion.getText())) {
            campoDescripcion.setError("La descripción es obligatoria");
            return false;
        }
        if (TextUtils.isEmpty(campoFechaEntrega.getText())) {
            campoFechaEntrega.setError("La fecha de entrega es obligatoria");
            return false;
        }
        if (TextUtils.isEmpty(campoHoraEntrega.getText())) {
            campoHoraEntrega.setError("La hora de entrega es obligatoria");
            return false;
        }
        return true;
    }
}