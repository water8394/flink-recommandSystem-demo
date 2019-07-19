package com.demo;

import java.util.ArrayList;
import java.util.List;

public class ScoreSortTest {

    private static class Product{
        double rank;
        double score;

        public Product(double rank, double score) {
            this.rank = rank;
            this.score = score;
        }

        public double getRank() {
            return rank;
        }

        public void setRank(double rank) {
            this.rank = rank;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        @Override
        public String toString() {
            return "Product{" +
                    "rank=" + rank +
                    ", score=" + score +
                    '}';
        }
    }


    public static void main(String[] args) {
        List<Product> products = new ArrayList<>();
        products.add(new ScoreSortTest.Product(1, 0.3));
        products.add(new ScoreSortTest.Product(2, 0.6));
        products.add(new ScoreSortTest.Product(3, 0.2));
        products.forEach(System.out::println);

        products.sort((a, b) -> {
            if (a.getScore() > b.getScore()){
                return 1;
            }else if (a.getScore() == b.getScore()){
                return 0;
            }else {
                return -1;
            }
        });

//        products.sort(new Comparator<Product>() {
//            @Override
//            public int compare(Product o1, Product o2) {
//                return o1.getScore()>o2.getScore() ? 1 : -1;
//            }
//        });

        products.forEach(System.out::println);
    }


}
