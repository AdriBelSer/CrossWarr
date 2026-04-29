package com.yinya.crosswarr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.Timestamp;
import com.yinya.crosswarr.databinding.FragmentUsersEditionBinding;
import com.yinya.crosswarr.models.UserData;
import com.yinya.crosswarr.repository.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Fragmento destinado al modo administrador para la edición o creación de Usuarios.
 * Presenta un formulario donde se pueden modificar los datos básicos de un usuario
 * (nombre, email) y cambiar su rol (Usuario / Administrador).
 * Está diseñado para conservar intactos los datos complejos del usuario (historial, ajustes, tokens)
 * cuando solo se están actualizando sus permisos.
 */
public class UsersEdition extends Fragment {
    private FragmentUsersEditionBinding binding;
    private String currentUid = null;
    private String currentPhoto = null;
    private Timestamp currentCreationDate = null;
    private String currentPushToken = null;
    private Map<String, Object> currentSettings = new HashMap<>();
    private List<Map<String, Object>> currentChallenges = new java.util.ArrayList<>();

    /**
     * Invocado para inflar el diseño XML asociado a este fragmento utilizando View Binding.
     *
     * @param inflater           El objeto LayoutInflater utilizado para inflar las vistas en el contexto.
     * @param container          El ViewGroup padre en el que se insertará la vista.
     * @param savedInstanceState Si no es null, el fragmento está siendo reconstruido.
     * @return La vista raíz (View) del diseño inflado.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @androidx.annotation.Nullable ViewGroup container, @androidx.annotation.Nullable Bundle savedInstanceState) {
        binding = FragmentUsersEditionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Invocado inmediatamente después de que la vista ha sido creada.
     * Comprueba si el fragmento ha recibido argumentos (lo que indicaría que estamos en
     * modo "Edición" de un usuario existente) y puebla los campos del formulario.
     *
     * @param view               La Vista devuelta por onCreateView().
     * @param savedInstanceState Si no es null, el fragmento está siendo reconstruido.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Comprobamos si recibimos un Bundle con datos (Modo Edición)
        if (getArguments() != null && getArguments().containsKey("id")) {
            currentUid = getArguments().getString("id");
            String name = getArguments().getString("name");
            String email = getArguments().getString("email");
            String role = getArguments().getString("role");
            currentPhoto = getArguments().getString("photo");

            // Rellenar campos de texto
            binding.etNameFragmentUsersEdition.setText(name);
            binding.etEmailFragmentUsersEdition.setText(email);

            // Ajustar el interruptor según el rol actual
            binding.switchRoleFragmentUsersEdition.setChecked("admin".equals(role));

            // Recuperar Mapas de datos complejos para no sobrescribirlos con null al guardar
            if (getArguments().containsKey("settings")) {
                currentSettings = (Map<String, Object>) getArguments().getSerializable("settings");
            }
            if (getArguments().containsKey("challenges")) {
                currentChallenges = (List<Map<String, Object>>) getArguments().getSerializable("challenges");
            }
        }

        // Listener para el botón de guardado
        binding.btnCreateUser.setOnClickListener(v -> saveUser());
    }

    /**
     * Recoge, valida y procesa los datos introducidos en el formulario.
     * Si los datos son válidos, construye el objeto {@link UserData} manteniendo
     * los valores previos de configuraciones e historial, y ordena su guardado o actualización
     * en Firebase a través del Repositorio. Tras guardar, regresa a la pantalla anterior.
     */
    private void saveUser() {
        String name = binding.etNameFragmentUsersEdition.getText().toString().trim();
        String email = binding.etEmailFragmentUsersEdition.getText().toString().trim();

        // Determinar el rol en base al interruptor
        String role = binding.switchRoleFragmentUsersEdition.isChecked() ? "admin" : "user";

        // Validación de campos obligatorios
        if (name.isEmpty() || email.isEmpty()) {
            android.widget.Toast.makeText(requireContext(), R.string.users_edition_toast_error_empty_data, android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        // Lógica para Modo Creación: Si no hay UID previo, generamos uno nuevo y asignamos fecha
        if (currentUid == null) {
            currentUid = UUID.randomUUID().toString();
            currentCreationDate = Timestamp.now();
        }

        // Empaquetar el usuario con los datos nuevos y los datos preservados
        UserData savedUser = new UserData(currentUid, email, name, currentPhoto, role, currentCreationDate, currentPushToken, currentSettings, currentChallenges);

        // Enviar a Firestore (Actualiza si el UID existe, crea si es nuevo)
        Repository.getInstance().createUser(savedUser);

        android.widget.Toast.makeText(requireContext(), R.string.users_edition_toast_save_successfull, android.widget.Toast.LENGTH_SHORT).show();
        goBackToUsers();
    }

    /**
     * Navega hacia la pantalla anterior (la lista de usuarios) extrayendo el fragmento actual
     * de la pila de navegación (BackStack).
     */
    private void goBackToUsers() {
        Navigation.findNavController(requireView()).popBackStack();
    }

    /**
     * Invocado cuando la vista del fragmento va a ser destruida.
     * Se anula el objeto de View Binding para liberar la memoria y evitar fugas (memory leaks).
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

