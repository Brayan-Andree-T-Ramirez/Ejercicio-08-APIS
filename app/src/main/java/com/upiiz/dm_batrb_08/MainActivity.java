package com.upiiz.dm_batrb_08;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.upiiz.dm_batrb_08.adapters.ClienteAdapter;
import com.upiiz.dm_batrb_08.models.Cliente;
import com.upiiz.dm_batrb_08.models.Datos;
import com.upiiz.dm_batrb_08.models.RespuestaClientes;
import com.upiiz.dm_batrb_08.network.ClienteRetrofit;
import com.upiiz.dm_batrb_08.network.api.ApiCliente;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvClientes;
    private ProgressBar progressBar;
    private FloatingActionButton fabAgregar;
    private ClienteAdapter adapter;
    private ApiCliente apiCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rvClientes = findViewById(R.id.rvClientes);
        progressBar = findViewById(R.id.progressBarMain);
        fabAgregar = findViewById(R.id.fabAgregar);

        rvClientes.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ClienteAdapter(clienteSeleccionado -> {
            Intent intent = new Intent(MainActivity.this, DetalleFormularioActivity.class);
            intent.putExtra("cliente_data", clienteSeleccionado);
            startActivity(intent);
        });
        rvClientes.setAdapter(adapter);

        fabAgregar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DetalleFormularioActivity.class);
            startActivity(intent);
        });

        apiCliente = ClienteRetrofit.obtenerApiCliente();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recarga la lista siempre que volvemos de Crear, Actualizar o Eliminar
        obtenerClientes();
    }

    private void obtenerClientes() {
        progressBar.setVisibility(View.VISIBLE);
        apiCliente.obtenerClientes().enqueue(new Callback<RespuestaClientes>() {
            @Override
            public void onResponse(Call<RespuestaClientes> call, Response<RespuestaClientes> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().getDatos() != null) {
                    Datos datos = response.body().getDatos();
                    List<Cliente> lista = datos.getContent();
                    adapter.setClientes(lista);
                    if (lista == null || lista.isEmpty()) {
                        Toast.makeText(MainActivity.this, "No hay clientes registrados", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Error del servidor: Código " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<RespuestaClientes> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}