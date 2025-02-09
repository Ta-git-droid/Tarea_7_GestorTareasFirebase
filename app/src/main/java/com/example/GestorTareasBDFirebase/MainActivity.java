package com.example.GestorTareasBDFirebase;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tarea_7_gestortareas.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TareaAdapter.OnTareaClickListener {

    private RecyclerView recyclerView;  // Vista para mostrar una lista de tareas
    private TareaAdapter adaptador;     // Adaptador para conectar la lista de tareas con la vista
    private List<Tarea> listaTareas;    // Lista que contiene las tareas a mostrar
    private BaseDatosTareas baseDatosTareas; // Instancia de la clase que maneja la base de datos


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Configurar diseño sin bordes (opcional)
        setContentView(R.layout.activity_main);

        // Configurar la disposición de la actividad principal para adaptarse a los bordes del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_main), (vista, insets) -> {
            Insets bordesSistema = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            vista.setPadding(bordesSistema.left, bordesSistema.top, bordesSistema.right, bordesSistema.bottom);
            return insets;
        });

        // Inicializar vistas
        recyclerView = findViewById(R.id.recyclerView);
        FloatingActionButton botonFlotante = findViewById(R.id.fab);

        // Inicializar base de datos
        baseDatosTareas = new BaseDatosTareas();
        listaTareas = new ArrayList<>();
        adaptador = new TareaAdapter(listaTareas, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adaptador);

        botonFlotante.setOnClickListener(v -> mostrarDialogoTarea(null));

        cargarTareasDesdeBaseDeDatos();
    }

    private void cargarTareasDesdeBaseDeDatos() {
        baseDatosTareas.obtenerTareas(new BaseDatosTareas.BaseDatosCallback() {
            @Override
            public void onSuccess() {
                // Este método no se usa en este caso, ya que no se pasa lista de tareas
            }

            @Override
            public void onSuccess(List<Tarea> tareas) {
                listaTareas.clear();
                listaTareas.addAll(tareas);
                ordenarTareas();
            }

            @Override
            public void onFailure(String error) {
                Log.e("MainActivity", "Error al cargar tareas: " + error);
            }
        });
    }

    private void agregarTareaBD(Tarea tarea) {
        baseDatosTareas.agregarTarea(tarea, new BaseDatosTareas.BaseDatosCallback() {
            @Override
            public void onSuccess() {
                // Recargar la lista de tareas
                cargarTareasDesdeBaseDeDatos();
            }

            @Override
            public void onSuccess(List<Tarea> tareas) {

            }

            @Override
            public void onFailure(String error) {
                Log.e("Tarea", "Error al guardar la tarea: " + error);
                Toast.makeText(MainActivity.this, "Error al guardar la tarea", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void eliminarTareaBD(Tarea tarea) {
        baseDatosTareas.eliminarTarea(tarea.getId(), new BaseDatosTareas.BaseDatosCallback() {
            @Override
            public void onSuccess() {
                cargarTareasDesdeBaseDeDatos();
            }

            @Override
            public void onSuccess(List<Tarea> tareas) {
                // No debería ser necesario aquí
            }

            @Override
            public void onFailure(String error) {
                Log.e("MainActivity", "Error al eliminar tarea: " + error);
            }
        });
    }

    private void ordenarTareas() {
        listaTareas.sort((t1, t2) -> {
            int asignaturaComparison = t1.getAsignatura().compareTo(t2.getAsignatura());
            if (asignaturaComparison != 0) return asignaturaComparison;

            try {
                SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
                Date fecha1 = formatoFecha.parse(t1.getFechaEntrega());
                Date fecha2 = formatoFecha.parse(t2.getFechaEntrega());
                return fecha1.compareTo(fecha2);
            } catch (ParseException e) {
                Log.e("MainActivity", "Error al parsear fechas", e);
                return 0;
            }
        });
        adaptador.notifyDataSetChanged();
    }

    private void mostrarDialogoTarea(Tarea tareaAEditar) {
        // Crear el dialogo para agregar o editar tarea
        NuevaTareaDialogFragment dialogo = new NuevaTareaDialogFragment();

        // Si estamos editando una tarea, pasarla al dialogo
        if (tareaAEditar != null) {
            Bundle argumentos = new Bundle();
            argumentos.putParcelable("homework", tareaAEditar);
            dialogo.setArguments(argumentos);
        }

        // Configurar el listener para cuando la tarea es guardada (ya sea nueva o editada)
        dialogo.setOnTareaGuardadaListener(new NuevaTareaDialogFragment.OnTareaGuardadaListener() {
            @Override
            public void onTareaGuardada(Tarea tarea) {
                // Comprobamos si estamos editando una tarea o creando una nueva
                if (tareaAEditar != null) {
                    // Si estamos editando, actualizamos la tarea en la base de datos
                    actualizarTareaBD(tarea);
                } else {
                    // Si estamos creando una nueva tarea, la agregamos a la base de datos
                    agregarTareaBD(tarea);
                }
            }
        });

        // Mostrar el dialogo
        dialogo.show(getSupportFragmentManager(), "DialogoTarea");
    }

    private void marcarTareaCompletada(Tarea tarea) {
        baseDatosTareas.marcarTareaComoCompletada(tarea.getId(), new BaseDatosTareas.BaseDatosCallback() {
            @Override
            public void onSuccess() {
                tarea.setStability(1);
                int position = listaTareas.indexOf(tarea);
                adaptador.notifyItemChanged(position);
                Toast.makeText(MainActivity.this, "Tarea completada", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(List<Tarea> tareas) {
                listaTareas.clear();
                listaTareas.addAll(tareas);
                ordenarTareas();
            }


            @Override
            public void onFailure(String error) {
                Log.e("Firebase", "Error al marcar tarea como completada: " + error);
            }
        });
    }

    private void mostrarOpcionesTarea(Tarea tarea) {
        BottomSheetDialog dialogoOpciones = new BottomSheetDialog(this);
        View vistaOpciones = getLayoutInflater().inflate(R.layout.tareas_opciones, null);

        vistaOpciones.findViewById(R.id.editOption).setOnClickListener(v -> {
            dialogoOpciones.dismiss();
            mostrarDialogoTarea(tarea);
        });

        vistaOpciones.findViewById(R.id.deleteOption).setOnClickListener(v -> {
            dialogoOpciones.dismiss();
            confirmarEliminacion(tarea);
        });

        vistaOpciones.findViewById(R.id.completeOption).setOnClickListener(v -> {
            dialogoOpciones.dismiss();
            marcarTareaCompletada(tarea);
        });

        dialogoOpciones.setContentView(vistaOpciones);
        dialogoOpciones.show();
    }

    private void confirmarEliminacion(Tarea tarea) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que deseas eliminar esta tarea?")
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarTareaBD(tarea))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public void onTareaClick(Tarea tarea) {
        mostrarOpcionesTarea(tarea);
    }

    public void actualizarTareaBD(Tarea tarea) {
        baseDatosTareas.actualizarTarea(tarea, new BaseDatosTareas.BaseDatosCallback() {
            @Override
            public void onSuccess() {
                // Actualizar lista local y RecyclerView
                int position = listaTareas.indexOf(tarea);
                if (position >= 0) {
                    listaTareas.set(position, tarea);
                    ordenarTareas();
                }
            }

            @Override
            public void onSuccess(List<Tarea> tareas) {
                // Este método no es necesario aquí si usas onSuccess() sin parámetros
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(MainActivity.this, "Error al actualizar", Toast.LENGTH_SHORT).show();
            }
        });
    }
}