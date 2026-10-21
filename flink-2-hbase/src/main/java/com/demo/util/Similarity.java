package com.demo.util;

import com.demo.domain.ProductPortraitEntity;

import java.util.List;

/**
 * Pure scoring helpers used by the offline relatedness jobs.
 * The formulas match the existing schedulers; they are not textbook cosine similarity.
 */
public final class Similarity {

    private Similarity() {
    }

    /**
     * Product-portrait relatedness from {@link com.demo.scheduler.ProductCoeff}.
     * {@code sqrt(dot(tags)) / sqrt(product.total + target.total)}, or 0 when the denominator is 0.
     */
    public static double productPortraitScore(ProductPortraitEntity product, ProductPortraitEntity target) {
        double sqrt = Math.sqrt(product.getTotal() + target.getTotal());
        if (sqrt == 0) {
            return 0.0;
        }
        int total = product.getMan() * target.getMan() + product.getWoman() * target.getWoman()
                + product.getAge_10() * target.getAge_10() + product.getAge_20() * target.getAge_20()
                + product.getAge_30() * target.getAge_30() + product.getAge_40() * target.getAge_40()
                + product.getAge_50() * target.getAge_50() + product.getAge_60() * target.getAge_60();
        return Math.sqrt(total) / sqrt;
    }

    /**
     * Item-CF coefficient from {@link com.demo.scheduler.ItemCfCoeff}:
     * {@code |intersection| / sqrt(|left| * |right|)}.
     * Pairwise {@code equals} counting is preserved, including duplicate keys.
     */
    public static double itemCfScore(List<?> leftKeys, List<?> rightKeys) {
        int n = leftKeys.size();
        int m = rightKeys.size();
        int sum = 0;
        double total = Math.sqrt(n * m);
        for (Object key : leftKeys) {
            for (Object other : rightKeys) {
                if (key.equals(other)) {
                    sum++;
                }
            }
        }
        if (total == 0) {
            return 0.0;
        }
        return sum / total;
    }
}
