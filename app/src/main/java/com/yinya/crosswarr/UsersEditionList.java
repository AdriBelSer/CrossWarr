package com.yinya.crosswarr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.yinya.crosswarr.adapters.OnUserAdminListener;
import com.yinya.crosswarr.adapters.UsersEditionViewAdapter;
import com.yinya.crosswarr.databinding.FragmentUsersEditionListBinding;
import com.yinya.crosswarr.models.UserData;
import com.yinya.crosswarr.repository.Repository;

import java.util.ArrayList;

/**
 * Fragmento que muestra la lista completa de Usuarios registrados en la aplicación.
 * Pertenece a las herramientas exclusivas para perfiles con rol de "Administrador".
 * Permite visualizar a todos los atletas, acceder a la edición de sus perfiles
 * (para modificar sus roles, por ejemplo) y gestionar el borrado de cuentas
 * de forma segura mediante confirmación.
 */
public class UsersEditionList extends Fragment {
    private FragmentUsersEditionListBinding binding;
    private ArrayList<UserData> users;
    private UsersEditionViewAdapter adapter;

    /**
     * Invocado para inflar el diseño XML asociado a este fragmento utilizando View Binding.
     *
     * @param inflater           El objeto LayoutInflater utilizado para inflar las vistas en el contexto.
     * @param container          El ViewGroup padre en el que se insertará la vista.
     * @param savedInstanceState Si no es null, el fragmento está siendo reconstruido.
     * @return La vista raíz (View) del diseño inflado.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUsersEditionListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Invocado inmediatamente después de que la vista ha sido creada.
     * Configura el RecyclerView, el adaptador de usuarios, los eventos de clic (edición y borrado)
     * e inicia los observadores reactivos para la carga de datos.
     *
     * @param view               La Vista devuelta por onCreateView().
     * @param savedInstanceState Si no es null, el fragmento está siendo reconstruido.
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        users = new ArrayList<>();

        // Configuración del adaptador y manejo de eventos (Clic en tarjeta y Clic en papelera)
        adapter = new UsersEditionViewAdapter(users, getContext(), new OnUserAdminListener() {
            @Override
            public void onUserClick(UserData user, View view) {
                userAdminClicked(user, view);
            }

            @Override
            public void onDeleteClick(UserData user) {

                // Diálogo de confirmación de seguridad antes de borrar una cuenta
                new android.app.AlertDialog.Builder(requireContext()).setTitle(R.string.User_edition_list_delete_title).setMessage(requireContext().getString(R.string.User_edition_list_delete_message)).setPositiveButton(R.string.User_edition_list_delete_yes_btn, (dialog, which) -> {

                    // Petición de borrado a la base de datos a través del Repositorio
                    Repository.getInstance().deleteUser(user);

                    // Actualización: lo borramos de la lista visual inmediatamente
                    users.remove(user);
                    adapter.notifyDataSetChanged();
                    android.widget.Toast.makeText(requireContext(), R.string.User_edition_list_delete_success, android.widget.Toast.LENGTH_SHORT).show();
                }).setNegativeButton(R.string.User_edition_list_delete_cancel_btn, null).show();
            }
        });

        // Configuración del diseño y vinculación del adaptador a la lista visual
        binding.usersEditionRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.usersEditionRecyclerview.setAdapter(adapter);

        setupObservers();
        loadUsers();
    }

    /**
     * Configura la suscripción al LiveData de usuarios del Repositorio.
     * Reacciona automáticamente a los cambios en los datos, actualizando la lista
     * en pantalla y ocultando la animación de carga (Skeleton/Shimmer) cuando los
     * perfiles están listos para mostrarse.
     */
    private void setupObservers() {
        Repository.getInstance().getUsersLiveData().observe(getViewLifecycleOwner(), listFromFirebase -> {
            if (listFromFirebase != null) {
                users.clear();
                users.addAll(listFromFirebase);
                if (adapter != null) {
                    adapter.notifyDataSetChanged();

                    // Ocultamos el efecto visual de carga
                    com.yinya.crosswarr.SkeletonUtils.hideSkeleton(binding.shimmerViewContainer, binding.usersEditionRecyclerview);
                }
            }
        });
    }

    /**
     * Solicita al Repositorio que inicie la descarga de todos los perfiles de usuario
     * desde Firebase Firestore.
     */
    private void loadUsers() {
        Repository.getInstance().fetchUsersFromFirebase();
    }

    /**
     * Prepara los datos del usuario seleccionado y navega hacia el formulario de edición.
     * Desglosa el objeto de modelo y empaqueta de forma segura sus datos primarios como Strings
     * y sus estructuras complejas (Settings, Challenges) usando Serialización.
     *
     * @param user El objeto que contiene toda la información del usuario seleccionado.
     * @param view La vista de la tarjeta pulsada.
     */
    private void userAdminClicked(UserData user, View view) {
        Bundle bundle = new Bundle();
        bundle.putString("id", user.getUid());
        bundle.putString("name", user.getName());
        bundle.putString("email", user.getEmail());
        bundle.putString("photo", user.getPhoto());
        bundle.putString("role", user.getRole());
        bundle.putString("notificationPushToken", user.getNotificationPushToken());

        // Serializamos los mapas y listas complejas para no perderlos en el tránsito entre pantallas
        if (user.getSettings() != null) {
            bundle.putSerializable("settings", (java.io.Serializable) user.getSettings());
        }
        if (user.getChallenges() != null) {
            bundle.putSerializable("challenges", (java.io.Serializable) user.getChallenges());
        }

        // Formateo de la fecha de creación, aplicando un fallback para usuarios legacy
        if (user.getAccountCreationDate() != null) {
            bundle.putString("accountCreationDate", user.getAccountCreationDate().toString());
        } else {
            bundle.putString("accountCreationDate", "Fecha desconocida");
        }

        // Navegación hacia el fragmento de edición con el Bundle empaquetado
        Navigation.findNavController(view).navigate(R.id.usersEdition, bundle);
    }

    /**
     * Invocado cuando la jerarquía de vistas de este fragmento va a ser destruida.
     * Libera el adaptador del RecyclerView y anula el View Binding para permitir que
     * el recolector de basura libere memoria correctamente (prevención de memory leaks).
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (binding != null && binding.usersEditionRecyclerview != null) {
            binding.usersEditionRecyclerview.setAdapter(null);
        }
        adapter = null;
        binding = null;
    }
}
