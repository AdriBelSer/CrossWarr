package com.yinya.crosswarr;

import android.view.View;

import com.facebook.shimmer.ShimmerFrameLayout;

/**
 * Clase de utilidad (Helper) para gestionar las animaciones de carga visuales (Skeletons/Shimmers)
 * en la interfaz de usuario.
 * Centraliza la lógica para alternar fluidamente entre el estado de "cargando" y el estado de
 * "datos listos", utilizando la librería Shimmer de Facebook.
 */
public class SkeletonUtils {

    /**
     * Detiene la animación de carga y oculta el esqueleto visual (Shimmer),
     * mostrando en su lugar la vista principal con los datos ya cargados.
     * Incluye comprobaciones de seguridad (null checks) para evitar caídas de la app
     * si la vista ya no existe.
     *
     * @param shimmerContainer El contenedor de la animación Shimmer que se va a detener y ocultar.
     * @param dataView         La vista principal (por ejemplo, un RecyclerView o ScrollView)
     *                         que contiene los datos reales y que pasará a ser visible.
     */
    public static void hideSkeleton(ShimmerFrameLayout shimmerContainer, View dataView) {
        if (shimmerContainer != null) {
            shimmerContainer.stopShimmer();
            shimmerContainer.setVisibility(View.GONE);
        }

        if (dataView != null) {
            dataView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Oculta la vista de datos y vuelve a iniciar la animación de carga (Skeleton).
     * Ideal para gestionar eventos de recarga manual, como cuando el usuario hace
     * un "pull-to-refresh" (deslizar para actualizar) o cuando se vuelve a consultar la base de datos.
     *
     * @param shimmerContainer El contenedor de la animación Shimmer que se va a mostrar y reproducir.
     * @param dataView         La vista principal de datos que se va a ocultar temporalmente
     *                         mientras dura la carga.
     */
    public static void showSkeleton(ShimmerFrameLayout shimmerContainer, View dataView) {
        if (shimmerContainer != null) {
            shimmerContainer.setVisibility(View.VISIBLE);
            shimmerContainer.startShimmer();
        }

        if (dataView != null) {
            dataView.setVisibility(View.GONE);
        }
    }
}
