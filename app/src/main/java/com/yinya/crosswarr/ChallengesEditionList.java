package com.yinya.crosswarr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.yinya.crosswarr.adapters.ChallengesEditionViewAdapter;
import com.yinya.crosswarr.adapters.OnChallengeAdminListener;
import com.yinya.crosswarr.databinding.FragmentChallengesEditionListBinding;
import com.yinya.crosswarr.models.ChallengeData;
import com.yinya.crosswarr.repository.Repository;

import java.util.ArrayList;

/**
 * Fragmento que muestra la lista completa de Desafíos (Challenges) en el modo administrador.
 * Permite visualizar los retos existentes, navegar a sus detalles, acceder al formulario
 * de creación de nuevos retos y gestionar el borrado de retos aún no publicados.
 */
public class ChallengesEditionList extends Fragment {
    private FragmentChallengesEditionListBinding binding;
    private ArrayList<ChallengeData> challenges;
    private ChallengesEditionViewAdapter adapter;

    /**
     * Invocado para inflar el diseño XML asociado a este fragmento utilizando View Binding.
     *
     * @param inflater           El objeto LayoutInflater utilizado para inflar las vistas en el contexto.
     * @param container          El ViewGroup padre en el que se insertará la vista del fragmento.
     * @param savedInstanceState Si no es null, este fragmento está siendo reconstruido.
     * @return La vista raíz (View) del diseño inflado.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChallengesEditionListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Invocado inmediatamente después de que onCreateView ha retornado.
     * Aquí se configura el RecyclerView, el adaptador, los listeners de clics (incluyendo
     * la lógica de validación para el borrado) y se inician los observadores reactivos.
     *
     * @param view               La Vista devuelta por onCreateView().
     * @param savedInstanceState Si no es null, el fragmento está siendo reconstruido.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        challenges = new ArrayList<>();

        // Configuración del adaptador y sus interfaces de comunicación
        adapter = new ChallengesEditionViewAdapter(challenges, getContext(), new OnChallengeAdminListener() {
            @Override
            public void onChallengeClick(ChallengeData challenge, View view) {
                challengeAdminClicked(challenge, view);
            }

            @Override
            public void onDeleteClick(ChallengeData challenge) {
                // Validación de seguridad: Comprobamos que el reto tenga una fecha válida
                if (challenge.getActivationDate() == null) {
                    android.widget.Toast.makeText(requireContext(), R.string.challenges_edition_list_toast_delete_error_date, android.widget.Toast.LENGTH_SHORT).show();
                    return;
                }

                // Convertimos el Timestamp de Firebase a Date de Java para comparar con el día de hoy
                java.util.Date activationDate = challenge.getActivationDate().toDate();
                java.util.Date currentDate = new java.util.Date();

                // LÓGICA DE NEGOCIO: Proteger el historial de los usuarios
                if (activationDate.before(currentDate)) {
                    // Si el reto ya se publicó en el pasado, impedimos su borrado para no romper las estadísticas de los usuarios
                    android.widget.Toast.makeText(requireContext(), R.string.challenges_edition_list_toast_delete_error_challenge_published, android.widget.Toast.LENGTH_LONG).show();
                } else {
                    // Es un reto futuro: pedimos confirmación explícita mediante un AlertDialog antes de borrar
                    new android.app.AlertDialog.Builder(requireContext()).setTitle(R.string.challenges_edition_list_alertDialog_title).setMessage(requireContext().getString(R.string.challenges_edition_list_alertDialog_message, challenge.getTitle())).setPositiveButton(R.string.challenges_edition_list_alertDialog_btnYes, (dialog, which) -> {

                        // Eliminación en base de datos y actualización de la lista local
                        Repository.getInstance().deleteChallenge(challenge);
                        challenges.remove(challenge);
                        adapter.notifyDataSetChanged();

                        android.widget.Toast.makeText(requireContext(), R.string.challenges_edition_list_alertDialog_success, android.widget.Toast.LENGTH_SHORT).show();
                    }).setNegativeButton(R.string.challenges_edition_list_alertDialog_btnCancel, null).show();
                }
            }
        });

        // Configuración visual de la lista
        binding.challengesEditionRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.challengesEditionRecyclerview.setAdapter(adapter);

        // Botón flotante para crear un nuevo desafío
        if (binding.btnNewChallenge != null) {
            binding.btnNewChallenge.setOnClickListener(v -> {
                // Navegamos a la vista de edición sin pasarle datos (modo creación)
                Navigation.findNavController(v).navigate(R.id.challengesEdition);
            });
        }
        setupObservers();
        loadChallenge();
    }

    /**
     * Configura la suscripción al LiveData del Repositorio.
     * Reacciona automáticamente a los cambios en la lista de desafíos descargada de Firebase.
     * Una vez recibidos los datos, actualiza el RecyclerView y oculta la animación de carga (Skeleton).
     */
    private void setupObservers() {
        Repository.getInstance().getChallengesLiveData().observe(getViewLifecycleOwner(), listFromFirebase -> {
            if (listFromFirebase != null) {
                challenges.clear();
                challenges.addAll(listFromFirebase);
                if (adapter != null) {
                    adapter.notifyDataSetChanged();

                    // Ocultamos el efecto de carga visual (Shimmer/Skeleton)
                    com.yinya.crosswarr.SkeletonUtils.hideSkeleton(binding.shimmerViewContainer, binding.challengesEditionRecyclerview);
                }
            }
        });
    }

    /**
     * Solicita al Repositorio que inicie la descarga asíncrona de los desafíos desde Firebase.
     */
    private void loadChallenge() {
        Repository.getInstance().fetchChallengesFromFirebase();
    }

    /**
     * Navega a la vista de detalle de un desafío específico.
     * Empaqueta todos los datos del objeto {@link ChallengeData} en un Bundle (como Strings
     * para evitar excepciones de nulos o de serialización) y los envía a la siguiente pantalla.
     *
     * @param challenge El objeto con la información del desafío seleccionado.
     * @param view      La vista (tarjeta) que recibió el clic.
     */
    public void challengeAdminClicked(ChallengeData challenge, View view) {
        Bundle bundle = new Bundle();
        bundle.putString("id", challenge.getId());
        bundle.putString("title", challenge.getTitle());
        // Convertimos los números a String para evitar NullPointerExceptions en el destino
        bundle.putString("time", String.valueOf(challenge.getChallengeTime()));
        bundle.putString("exerciseSup", challenge.getExerciseSup());
        bundle.putString("exerciseInf", challenge.getExerciseInf());
        bundle.putString("exerciseCore", challenge.getExerciseCore());
        bundle.putString("state", String.valueOf(challenge.isState()));
        bundle.putString("repetitionSup", String.valueOf(challenge.getRepetitionSup()));
        bundle.putString("repetitionInf", String.valueOf(challenge.getRepetitionInf()));
        bundle.putString("repetitionCore", String.valueOf(challenge.getRepetitionCore()));
        bundle.putString("type", challenge.getType());

        // Navegar al ChallengeDetailFragment con el Bundle
        Navigation.findNavController(view).navigate(R.id.challengeDetail, bundle);
    }


    /**
     * Invocado cuando la vista del fragmento va a ser destruida.
     * Es una práctica recomendada desenlazar el adaptador del RecyclerView y anular
     * el objeto de View Binding para prevenir fugas de memoria (memory leaks).
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (binding != null && binding.challengesEditionRecyclerview != null) {
            binding.challengesEditionRecyclerview.setAdapter(null);
        }
        adapter = null;
        binding = null;
    }
}
