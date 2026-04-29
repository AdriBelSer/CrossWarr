package com.yinya.crosswarr.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yinya.crosswarr.R;
import com.yinya.crosswarr.databinding.CardListUsersEditionItemBinding;
import com.yinya.crosswarr.models.UserData;

import java.util.ArrayList;

/**
 * Adaptador para el RecyclerView que gestiona la lista de usuarios en la vista de administración.
 * Se encarga de inflar las tarjetas de perfil de los usuarios, mostrar sus datos básicos (nombre, email y rol),
 * y capturar las interacciones del administrador para editar o eliminar cuentas.
 */
public class UsersEditionViewAdapter extends RecyclerView.Adapter<UsersEditionViewAdapter.UsersEditionViewHolder> {
    private final ArrayList<UserData> user;
    private final Context context;
    private final OnUserAdminListener listener;

    /**
     * Constructor del adaptador de edición de usuarios.
     *
     * @param user     Lista de objetos {@link UserData} que contiene la información de los usuarios registrados.
     * @param context  Contexto de la aplicación o la actividad que aloja el RecyclerView.
     * @param listener Interfaz para capturar y gestionar los eventos de clic en las tarjetas (edición y borrado).
     */
    public UsersEditionViewAdapter(ArrayList<UserData> user, Context context, OnUserAdminListener listener) {
        this.user = user;
        this.context = context;
        this.listener = listener;
    }

    /**
     * Invocado por el RecyclerView cuando necesita crear un nuevo {@link UsersEditionViewHolder}.
     * Infla el diseño XML de la tarjeta de edición de usuarios utilizando View Binding.
     *
     * @param parent   El ViewGroup al que se adjuntará la nueva vista.
     * @param viewType El tipo de vista del nuevo elemento.
     * @return Un nuevo UsersEditionViewHolder que contiene la vista de la tarjeta del usuario.
     */
    @NonNull
    @Override
    public UsersEditionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardListUsersEditionItemBinding binding = CardListUsersEditionItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new UsersEditionViewHolder(binding);
    }

    /**
     * Invocado por el RecyclerView para mostrar los datos en una posición específica de la lista.
     * Vincula la información del usuario al ViewHolder y establece los eventos de clic para
     * la tarjeta entera (editar usuario) y para el botón de la papelera (eliminar usuario).
     *
     * @param holder   El ViewHolder que debe actualizarse con el contenido del elemento.
     * @param position La posición del usuario dentro de la lista de datos.
     */
    @Override
    public void onBindViewHolder(@NonNull UsersEditionViewAdapter.UsersEditionViewHolder holder, int position) {
        UserData currentUser = this.user.get(position);
        holder.bind(currentUser);

        // Listener para detectar el clic en toda la tarjeta (abrir detalles/edición de perfil)
        holder.itemView.setOnClickListener(view -> {
            if (listener != null) {
                listener.onUserClick(currentUser, view);
            }
        });

        // Listener específico para el botón de eliminar usuario
        holder.binding.btnDeleteUser.setOnClickListener(view -> {
            if (listener != null) {
                listener.onDeleteClick(currentUser);
            }
        });
    }

    /**
     * Devuelve el número total de usuarios que el adaptador está gestionando.
     *
     * @return El tamaño de la lista de usuarios.
     */
    @Override
    public int getItemCount() {
        return user.size();
    }

    /**
     * Clase interna estática que actúa como ViewHolder para los elementos del RecyclerView de usuarios.
     * Mantiene las referencias a las vistas de la tarjeta (nombre, email, rol) para mejorar el
     * rendimiento y evitar búsquedas repetitivas durante el scroll.
     */
    public static class UsersEditionViewHolder extends RecyclerView.ViewHolder {
        private final CardListUsersEditionItemBinding binding;

        /**
         * Constructor del ViewHolder.
         *
         * @param binding El objeto de View Binding con las referencias a los elementos visuales de la tarjeta.
         */
        public UsersEditionViewHolder(@NonNull CardListUsersEditionItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        /**
         * Asigna los datos de un objeto {@link UserData} a las vistas de la interfaz.
         * Muestra el nombre, el correo electrónico y traduce el rol interno de la base de datos
         * ("admin" o "user") a una cadena de texto localizada usando los recursos del sistema (strings.xml).
         *
         * @param user El objeto de datos que contiene la información del usuario a mostrar.
         */
        public void bind(UserData user) {
            binding.tvUserNameCardListUsersEditionItem.setText(user.getName());
            binding.tvUserEmailCardListUsersEditionItem.setText(user.getEmail());

            // Verificamos el rol y asignamos el texto correspondiente según el idioma del dispositivo
            if (user.getRole().equals("admin")) {
                binding.tvUserRoleCardListUsersEditionItem.setText(R.string.user_edition_view_adapter_bind_admin);
            } else {
                binding.tvUserRoleCardListUsersEditionItem.setText(R.string.user_edition_view_adapter_bind_user);
            }
        }
    }
}
