package com.yinya.crosswarr;

import android.view.View;

import com.facebook.shimmer.ShimmerFrameLayout;

public class SkeletonUtils {
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
     * (Opcional) Método para volver a mostrar el skeleton por si
     * el usuario recarga la página o haces pull-to-refresh
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
