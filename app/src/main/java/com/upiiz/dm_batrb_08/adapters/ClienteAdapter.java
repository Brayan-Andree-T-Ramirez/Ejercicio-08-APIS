package com.upiiz.dm_batrb_08.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.upiiz.dm_batrb_08.R;
import com.upiiz.dm_batrb_08.models.Cliente;
import java.util.ArrayList;
import java.util.List;

public class ClienteAdapter extends RecyclerView.Adapter<ClienteAdapter.ClienteViewHolder> {

    private List<Cliente> listaClientes = new ArrayList<>();
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Cliente cliente);
    }

    public ClienteAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setClientes(List<Cliente> clientes) {
        this.listaClientes = clientes != null ? clientes : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ClienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cliente, parent, false);
        return new ClienteViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ClienteViewHolder holder, int position) {
        Cliente cliente = listaClientes.get(position);
        holder.bind(cliente, listener);
    }

    @Override
    public int getItemCount() {
        return listaClientes.size();
    }

    static class ClienteViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvNombre, tvCorreo, tvTelefono;

        public ClienteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvItemId);
            tvNombre = itemView.findViewById(R.id.tvItemNombre);
            tvCorreo = itemView.findViewById(R.id.tvItemCorreo);
            tvTelefono = itemView.findViewById(R.id.tvItemTelefono);
        }

        public void bind(final Cliente cliente, final OnItemClickListener listener) {
            tvId.setText("ID: #" + (cliente.getId() != null ? cliente.getId() : "N/A"));
            tvNombre.setText(cliente.getNombre());
            tvCorreo.setText("Correo: " + cliente.getCorreo());
            tvTelefono.setText("Tel: " + cliente.getTelefono());

            itemView.setOnClickListener(v -> listener.onItemClick(cliente));
        }
    }
}