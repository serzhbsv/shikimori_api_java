package com.pyshiki;

import com.pyshiki.ShikimoriApi;
// НЕ ИСПОЛЬЗУЙ import com.pyshiki.Models.*;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        try {
            ShikimoriApi api = new ShikimoriApi();

            // Используй полное имя — Models.AnimeBasic
            List<Models.AnimeBasic> results = api.animes().list(Map.of(
                "search", "Наруто",
                "limit", 5
            ));

            for (Models.AnimeBasic anime : results) {
                System.out.println("  " + anime.getRussian() + " | Score: " + anime.getScore());
            }

            List<Models.AnimeBasic> ongoings = api.animes().list(Map.of(
                "status", "ongoing",
                "limit", 5
            ));

            for (Models.AnimeBasic anime : ongoings) {
                System.out.println("  " + anime.getRussian() + " | Episodes: " +
                    anime.getEpisodesAired() + "/" + anime.getEpisodes());
            }

            Models.Anime anime = api.animes().byId(1);
            System.out.println("  Name: " + anime.getRussian());
            System.out.println("  Status: " + anime.getStatusRussian());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}