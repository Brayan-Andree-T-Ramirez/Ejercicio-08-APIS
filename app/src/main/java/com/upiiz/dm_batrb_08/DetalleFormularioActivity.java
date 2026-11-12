package com.upiiz.dm_batrb_08;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.upiiz.dm_batrb_08.models.Cliente;
import com.upiiz.dm_batrb_08.models.Link;
import com.upiiz.dm_batrb_08.models.PeticionCliente;
import com.upiiz.dm_batrb_08.models.RespuestaCliente;
import com.upiiz.dm_batrb_08.models.RespuestaEliminarCliente;
import com.upiiz.dm_batrb_08.network.ClienteRetrofit;
import com.upiiz.dm_batrb_08.network.api.ApiCliente;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetalleFormularioActivity extends AppCompatActivity {

    private TextView tvTitulo, tvId, tvLinks;
    private TextInputEditText etNombre, etCorreo, etTelefono, etDireccion;
    private SwitchMaterial swRegistrado;
    private Button btnGuardar, btnEliminar;

    private ApiCliente apiCliente;
    private Cliente clienteActual;
    private boolean esEdicion = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_formulario);

        tvTitulo = findViewById(R.id.tvTituloFormulario);
        tvId = findViewById(R.id.tvDetalleId);
        tvLinks = findViewById(R.id.tvLinksInfo);
        etNombre = findViewById(R.id.etNombre);
        etCorreo = findViewById(R.id.etCorreo);
        etTelefono = findViewById(R.id.etTelefono);
        etDireccion = findViewById(R.id.etDireccion);
        swRegistrado = findViewById(R.id.swRegistrado);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnEliminar = findViewById(R.id.btnEliminar);

        apiCliente = ClienteRetrofit.obtenerApiCliente();

        if (getIntent().hasExtra("cliente_data")) {
            clienteActual = (Cliente) getIntent().getSerializableExtra("cliente_data");
            if (clienteActual != null && clienteActual.getId() != null) {
                esEdicion = true;
                configurarModoEdicion(clienteActual);
                consultarDetalleActualizado(clienteActual.getId());
            }
        } else {
            tvTitulo.setText("Registrar Nuevo Cliente");
            tvId.setText("ID: (Asignado por el servidor)");
            btnGuardar.setText("Registrar Cliente");
            btnEliminar.setVisibility(View.GONE);
        }

        btnGuardar.setOnClickListener(v -> procesarGuardado());
        btnEliminar.setOnClickListener(v -> confirmarEliminacion());
    }

    private void configurarModoEdicion(Cliente c) {
        tvTitulo.setText("Detalle / Editar Cliente");
        tvId.setText("ID Cliente: #" + c.getId());
        etNombre.setText(c.getNombre());
        etCorreo.setText(c.getCorreo());
        etTelefono.setText(c.getTelefono());
        etDireccion.setText(c.getDireccion());
        swRegistrado.setChecked(c.isRegistrado());
        btnGuardar.setText("Actualizar Cambios");
        btnEliminar.setVisibility(View.VISIBLE);

        if (c.getLinks() != null && !c.getLinks().isEmpty()) {
            StringBuilder sb = new StringBuilder("Links HATEOAS:\n");
            for (Link l : c.getLinks()) {
                sb.append("- ").append(l.getRel()).append(": ").append(l.getHref()).append("\n");
            }
            tvLinks.setText(sb.toString());
            tvLinks.setVisibility(View.VISIBLE);
        }
    }

    private void consultarDetalleActualizado(Long id) {
        apiCliente.obtenerCliente(id).enqueue(new Callback<RespuestaCliente>() {
            @Override
            public void onResponse(Call<RespuestaCliente> call, Response<RespuestaCliente> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getDatos() != null) {
                    clienteActual = response.body().getDatos();
                    configurarModoEdicion(clienteActual);
                }
            }

            @Override
            public void onFailure(Call<RespuestaCliente> call, Throwable t) {
                // Mantiene los datos pasados por el Intent si hay parpadeo de red
            }
        });
    }

    private boolean validarFormulario() {
        String nombre = etNombre.getText() != null ? etNombre.getText().toString().trim() : "";
        String correo = etCorreo.getText() != null ? etCorreo.getText().toString().trim() : "";
        String telefono = etTelefono.getText() != null ? etTelefono.getText().toString().trim() : "";
        String direccion = etDireccion.getText() != null ? etDireccion.getText().toString().trim() : "";

        if (nombre.isEmpty()) {
            etNombre.setError("El nombre es obligatorio");
            etNombre.requestFocus();
            return false;
        }
        if (correo.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            etCorreo.setError("Ingresa un correo electrónico válido");
            etCorreo.requestFocus();
            return false;
        }
        if (telefono.isEmpty() || !telefono.matches("^[0-9+()\\-\\s]{7,15}$")) {
            etTelefono.setError("El teléfono solo debe contener números válidos (7 a 15 dígitos)");
            etTelefono.requestFocus();
            return false;
        }
        if (direccion.isEmpty()) {
            etDireccion.setError("La dirección es obligatoria");
            etDireccion.requestFocus();
            return false;
        }
        return true;
    }

    private void procesarGuardado() {
        if (!validarFormulario()) return;

        String nombre = etNombre.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();
        boolean registrado = swRegistrado.isChecked();

        btnGuardar.setEnabled(false);

        if (!esEdicion) {
            // CREATE (POST)
            PeticionCliente nuevo = new PeticionCliente(nombre, correo, telefono, direccion, registrado);
            apiCliente.crearCliente(nuevo).enqueue(new Callback<RespuestaCliente>() {
                @Override
                public void onResponse(Call<RespuestaCliente> call, Response<RespuestaCliente> response) {
                    btnGuardar.setEnabled(true);
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(DetalleFormularioActivity.this, "Cliente registrado exitosamente", Toast.LENGTH_SHORT).show();
                        finish(); // Regresa al listado y dispara onResume()
                    } else {
                        Toast.makeText(DetalleFormularioActivity.this, "Error al registrar (Código " + response.code() + ")", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<RespuestaCliente> call, Throwable t) {
                    btnGuardar.setEnabled(true);
                    Toast.makeText(DetalleFormularioActivity.this, "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            // UPDATE (PUT)
            Long id = clienteActual.getId();
            PeticionCliente actualizado = new PeticionCliente(id, nombre, correo, telefono, direccion, registrado);
            apiCliente.actualizarCliente(id, actualizado).enqueue(new Callback<RespuestaCliente>() {
                @Override
                public void onResponse(Call<RespuestaCliente> call, Response<RespuestaCliente> response) {
                    btnGuardar.setEnabled(true);
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(DetalleFormularioActivity.this, "Cliente actualizado correctamente", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(DetalleFormularioActivity.this, "No se pudo actualizar (Código " + response.code() + ")", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<RespuestaCliente> call, Throwable t) {
                    btnGuardar.setEnabled(true);
                    Toast.makeText(DetalleFormularioActivity.this, "Fallo de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void confirmarEliminacion() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que deseas eliminar permanentemente a " + clienteActual.getNombre() + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> ejecutarEliminacion())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void ejecutarEliminacion() {
        btnEliminar.setEnabled(false);
        apiCliente.eliminarCliente(clienteActual.getId()).enqueue(new Callback<RespuestaEliminarCliente>() {
            @Override
            public void onResponse(Call<RespuestaEliminarCliente> call, Response<RespuestaEliminarCliente> response) {
                btnEliminar.setEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(DetalleFormularioActivity.this, "Cliente eliminado con éxito", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(DetalleFormularioActivity.this, "Error al eliminar (Código " + response.code() + ")", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RespuestaEliminarCliente> call, Throwable t) {
                btnEliminar.setEnabled(true);
                Toast.makeText(DetalleFormularioActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}