package com.pyshiki;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Shikimori API Java Client ===\n");

        try {
            ShikimoriApi api = new ShikimoriApi();

            System.out.println("1. Search Anime 'Naruto':");
            List<ShikimoriModels.AnimeBasic> results = api.animes().list(Map.of(
                "search", "Наруто",
                "limit", 5
            ));

            for (ShikimoriModels.AnimeBasic anime : results) {
                System.out.println("  " + anime.getRussian() + " | Score: " + anime.getScore());
            }

            System.out.println("\n2. Ongoings:");
            List<ShikimoriModels.AnimeBasic> ongoings = api.animes().list(Map.of(
                "status", "ongoing",
                "limit", 5
            ));

            for (ShikimoriModels.AnimeBasic anime : ongoings) {
                System.out.println("  " + anime.getRussian() + " | Episodes: " +
                    anime.getEpisodesAired() + "/" + anime.getEpisodes());
            }

            System.out.println("\n3. Get Anime by ID (1):");
            ShikimoriModels.Anime anime = api.animes().byId(1);
            System.out.println("  Name: " + anime.getRussian());
            System.out.println("  Status: " + anime.getStatusRussian());
            System.out.println("  Score: " + anime.getScore());

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}