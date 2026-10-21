package com.demo.util;

import com.demo.domain.ProductPortraitEntity;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class SimilarityTest {

    @Test
    public void productPortraitScoreIsZeroWhenBothPortraitsAreEmpty() {
        assertEquals(0.0, Similarity.productPortraitScore(portrait(0, 0), portrait(0, 0)), 0.0);
    }

    @Test
    public void productPortraitScoreIsZeroWhenTagsDoNotOverlap() {
        assertEquals(0.0, Similarity.productPortraitScore(portrait(2, 0), portrait(0, 2)), 0.0);
    }

    @Test
    public void productPortraitScoreUsesSqrtOfDotOverSqrtOfTotals() {
        // totals are 3^2+4^2 = 25 each, dot is 25, score is 5 / sqrt(50)
        double expected = 5.0 / Math.sqrt(50.0);
        assertEquals(expected, Similarity.productPortraitScore(portrait(3, 4), portrait(3, 4)), 1e-9);
    }

    @Test
    public void itemCfScoreIsZeroWhenEitherSideIsEmpty() {
        assertEquals(0.0, Similarity.itemCfScore(Collections.emptyList(), Arrays.asList("u1")), 0.0);
        assertEquals(0.0, Similarity.itemCfScore(Arrays.asList("u1"), Collections.emptyList()), 0.0);
    }

    @Test
    public void itemCfScoreIsZeroWithoutSharedUsers() {
        assertEquals(0.0, Similarity.itemCfScore(Arrays.asList("u1", "u2"), Arrays.asList("u3")), 0.0);
    }

    @Test
    public void itemCfScoreDividesIntersectionBySqrtOfSizes() {
        // |intersection| = 1, |left| = |right| = 2, score = 1 / 2
        double score = Similarity.itemCfScore(Arrays.asList("u1", "u2"), Arrays.asList("u2", "u3"));
        assertEquals(0.5, score, 1e-9);
    }

    @Test
    public void itemCfScoreCountsDuplicateKeys() {
        // two matches against one right-hand key: 2 / sqrt(2)
        double score = Similarity.itemCfScore(Arrays.asList("a", "a"), Collections.singletonList("a"));
        assertEquals(2.0 / Math.sqrt(2.0), score, 1e-9);
    }

    private static ProductPortraitEntity portrait(int man, int woman) {
        ProductPortraitEntity entity = new ProductPortraitEntity();
        entity.setMan(man);
        entity.setWoman(woman);
        return entity;
    }
}
