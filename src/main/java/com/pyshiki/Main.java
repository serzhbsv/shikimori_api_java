package com.pyshiki.examples;

import com.pyshiki.*;
import com.pyshiki.Models.*;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Shikimori API Java Client Examples ===\n");

        try {
            ShikimoriApi api = new ShikimoriApi();

            // Пример 1: Поиск аниме
            System.out.println("1. Search Anime 'Naruto':");
            List<AnimeBasic> results = api.animes().list(Map.of(
                "search", "Наруто",
                "limit", 5
            ));

            for (AnimeBasic anime : results) {
                System.out.println("  " + anime.getRussian() + " | Score: " + anime.getScore());
            }

            // Пример 2: Онгоинги
            System.out.println("\n2. Ongoings:");
            List<AnimeBasic> ongoings = api.animes().list(Map.of(
                "status", "ongoing",
                "limit", 5
            ));

            for (AnimeBasic anime : ongoings) {
                System.out.println("  " + anime.getRussian() + " | Episodes: " + 
                    anime.getEpisodesAired() + "/" + anime.getEpisodes());
            }

            // Пример 3: Получить аниме по ID
            System.out.println("\n3. Get Anime by ID (1):");
            Anime anime = api.animes().byId(1);
            System.out.println("  Name: " + anime.getRussian());
            System.out.println("  Status: " + anime.getStatusRussian());
            System.out.println("  Kind: " + anime.getKindRussian());
            System.out.println("  Score: " + anime.getScore());

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}