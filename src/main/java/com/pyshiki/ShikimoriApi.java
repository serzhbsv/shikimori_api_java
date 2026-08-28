package com.pyshiki;

import com.pyshiki.ShikimoriModels.*;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import okhttp3.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

class OAuth2Manager {
    private static final String TOKEN_URL = "https://shikimori.one/oauth/token";
    private final OkHttpClient client;
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;

    public OAuth2Manager(String clientId, String clientSecret) {
        this(clientId, clientSecret, "urn:ietf:wg:oauth:2.0:oob");
    }

    public OAuth2Manager(String clientId, String clientSecret, String redirectUri) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.client = new OkHttpClient();
    }

    public String getAuthorizationUrl() {
        return "https://shikimori.one/oauth/authorize?" +
                "client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&response_type=code";
    }

    public OAuthToken exchangeCode(String code) throws IOException {
        RequestBody body = new FormBody.Builder()
                .add("grant_type", "authorization_code")
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .add("code", code)
                .add("redirect_uri", redirectUri)
                .build();

        Request request = new Request.Builder()
                .url(TOKEN_URL)
                .post(body)
                .header("User-Agent", "ShikiApp-Java/1.0")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to exchange code: " + response.code());
            }
            String json = response.body().string();
            Gson gson = new Gson();
            return gson.fromJson(json, OAuthToken.class);
        }
    }

    public OAuthToken refreshToken(String refreshToken) throws IOException {
        RequestBody body = new FormBody.Builder()
                .add("grant_type", "refresh_token")
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .add("refresh_token", refreshToken)
                .build();

        Request request = new Request.Builder()
                .url(TOKEN_URL)
                .post(body)
                .header("User-Agent", "ShikiApp-Java/1.0")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Failed to refresh token: " + response.code());
            }
            String json = response.body().string();
            Gson gson = new Gson();
            return gson.fromJson(json, OAuthToken.class);
        }
    }

    public static class OAuthToken {
        @SerializedName("access_token") public String accessToken;
        @SerializedName("token_type") public String tokenType;
        @SerializedName("expires_in") public int expiresIn;
        @SerializedName("refresh_token") public String refreshToken;
        @SerializedName("scope") public String scope;
        @SerializedName("created_at") public long createdAt;
    }
}

class AnimeEndpoints {
    private final ShikimoriClient client;

    public AnimeEndpoints(ShikimoriClient client) { this.client = client; }

    public List<AnimeBasic> list(Map<String, Object> params) {
        return client.get("/api/animes", params, new TypeRef<List<AnimeBasic>>(){});
    }

    public Anime byId(int id) {
        return client.get("/api/animes/" + id, null, new TypeRef<Anime>(){});
    }

    public List<Role> roles(int id) {
        return client.get("/api/animes/" + id + "/roles", null, new TypeRef<List<Role>>(){});
    }

    public List<AnimeBasic> similar(int id) {
        return client.get("/api/animes/" + id + "/similar", null, new TypeRef<List<AnimeBasic>>(){});
    }

    public List<AnimeRelation> related(int id) {
        return client.get("/api/animes/" + id + "/related", null, new TypeRef<List<AnimeRelation>>(){});
    }

    public List<Screenshot> screenshots(int id) {
        return client.get("/api/animes/" + id + "/screenshots", null, new TypeRef<List<Screenshot>>(){});
    }

    public Franchise franchise(int id) {
        return client.get("/api/animes/" + id + "/franchise", null, new TypeRef<Franchise>(){});
    }

    public List<ExternalLink> externalLinks(int id) {
        return client.get("/api/animes/" + id + "/external_links", null, new TypeRef<List<ExternalLink>>(){});
    }

    public List<Topic<AnimeBasic>> topics(int id, Map<String, Object> params) {
        return client.get("/api/animes/" + id + "/topics", params, new TypeRef<List<Topic<AnimeBasic>>>(){});
    }
}

class MangaEndpoints {
    private final ShikimoriClient client;

    public MangaEndpoints(ShikimoriClient client) { this.client = client; }

    public List<MangaBasic> list(Map<String, Object> params) {
        return client.get("/api/mangas", params, new TypeRef<List<MangaBasic>>(){});
    }

    public Manga byId(int id) {
        return client.get("/api/mangas/" + id, null, new TypeRef<Manga>(){});
    }

    public List<Role> roles(int id) {
        return client.get("/api/mangas/" + id + "/roles", null, new TypeRef<List<Role>>(){});
    }

    public List<MangaBasic> similar(int id) {
        return client.get("/api/mangas/" + id + "/similar", null, new TypeRef<List<MangaBasic>>(){});
    }

    public List<MangaRelation> related(int id) {
        return client.get("/api/mangas/" + id + "/related", null, new TypeRef<List<MangaRelation>>(){});
    }

    public List<ExternalLink> externalLinks(int id) {
        return client.get("/api/mangas/" + id + "/external_links", null, new TypeRef<List<ExternalLink>>(){});
    }

    public List<Topic<MangaBasic>> topics(int id, Map<String, Object> params) {
        return client.get("/api/mangas/" + id + "/topics", params, new TypeRef<List<Topic<MangaBasic>>>(){});
    }
}

class RanobeEndpoints {
    private final ShikimoriClient client;

    public RanobeEndpoints(ShikimoriClient client) { this.client = client; }

    public List<RanobeBasic> list(Map<String, Object> params) {
        return client.get("/api/ranobe", params, new TypeRef<List<RanobeBasic>>(){});
    }

    public Ranobe byId(int id) {
        return client.get("/api/ranobe/" + id, null, new TypeRef<Ranobe>(){});
    }

    public List<Role> roles(int id) {
        return client.get("/api/ranobe/" + id + "/roles", null, new TypeRef<List<Role>>(){});
    }

    public List<RanobeBasic> similar(int id) {
        return client.get("/api/ranobe/" + id + "/similar", null, new TypeRef<List<RanobeBasic>>(){});
    }

    public List<RanobeRelation> related(int id) {
        return client.get("/api/ranobe/" + id + "/related", null, new TypeRef<List<RanobeRelation>>(){});
    }

    public List<ExternalLink> externalLinks(int id) {
        return client.get("/api/ranobe/" + id + "/external_links", null, new TypeRef<List<ExternalLink>>(){});
    }

    public List<Topic<RanobeBasic>> topics(int id, Map<String, Object> params) {
        return client.get("/api/ranobe/" + id + "/topics", params, new TypeRef<List<Topic<RanobeBasic>>>(){});
    }
}

class UserEndpoints {
    private final ShikimoriClient client;

    public UserEndpoints(ShikimoriClient client) { this.client = client; }

    public User whoami() {
        return client.get("/api/users/whoami", null, new TypeRef<User>(){});
    }

    public User byId(int id) {
        return client.get("/api/users/" + id, null, new TypeRef<User>(){});
    }

    public User byNickname(String nickname) {
        return client.get("/api/users/" + nickname, null, new TypeRef<User>(){});
    }

    public List<UserRate> animeRates(int userId, Map<String, Object> params) {
        return client.get("/api/users/" + userId + "/anime_rates", params, new TypeRef<List<UserRate>>(){});
    }

    public List<UserRate> mangaRates(int userId, Map<String, Object> params) {
        return client.get("/api/users/" + userId + "/manga_rates", params, new TypeRef<List<UserRate>>(){});
    }

    public UserFavourites favorites(int userId) {
        return client.get("/api/users/" + userId + "/favorites", null, new TypeRef<UserFavourites>(){});
    }

    public List<UserHistoryRecord> history(int userId, Map<String, Object> params) {
        return client.get("/api/users/" + userId + "/history", params, new TypeRef<List<UserHistoryRecord>>(){});
    }

    public UserUnreadMessages unreadMessages() {
        return client.get("/api/users/unread_messages", null, new TypeRef<UserUnreadMessages>(){});
    }

    public void ignore(int userId) {
        client.post("/api/v2/users/" + userId + "/ignore", null, null);
    }

    public void unignore(int userId) {
        client.delete("/api/v2/users/" + userId + "/ignore");
    }
}

class UserRateEndpoints {
    private final ShikimoriClient client;

    public UserRateEndpoints(ShikimoriClient client) { this.client = client; }

    public List<UserRate> list(Map<String, Object> params) {
        return client.get("/api/v2/user_rates", params, new TypeRef<List<UserRate>>(){});
    }

    public UserRate byId(int id) {
        return client.get("/api/v2/user_rates/" + id, null, new TypeRef<UserRate>(){});
    }

    public UserRate create(UserRateTemplate body) {
        return client.post("/api/v2/user_rates", body, new TypeRef<UserRate>(){});
    }

    public UserRate update(int id, UserRateTemplate body) {
        return client.patch("/api/v2/user_rates/" + id, body, new TypeRef<UserRate>(){});
    }

    public void delete(int id) {
        client.delete("/api/v2/user_rates/" + id);
    }

    public UserRate increment(int id) {
        return client.post("/api/v2/user_rates/" + id + "/increment", null, new TypeRef<UserRate>(){});
    }
}

class TopicEndpoints {
    private final ShikimoriClient client;

    public TopicEndpoints(ShikimoriClient client) { this.client = client; }

    public List<Topic<?>> list(Map<String, Object> params) {
        return client.get("/api/topics", params, new TypeRef<List<Topic<?>>>(){});
    }

    public Topic<?> byId(int id) {
        return client.get("/api/topics/" + id, null, new TypeRef<Topic<?>>(){});
    }

    public Topic<?> create(Map<String, Object> body) {
        return client.post("/api/topics", body, new TypeRef<Topic<?>>(){});
    }

    public Topic<?> update(int id, Map<String, Object> body) {
        return client.patch("/api/topics/" + id, body, new TypeRef<Topic<?>>(){});
    }

    public void delete(int id) {
        client.delete("/api/topics/" + id);
    }

    public void ignore(int topicId) {
        client.post("/api/v2/topics/" + topicId + "/ignore", null, null);
    }

    public void unignore(int topicId) {
        client.delete("/api/v2/topics/" + topicId + "/ignore");
    }
}

class CommentEndpoints {
    private final ShikimoriClient client;

    public CommentEndpoints(ShikimoriClient client) { this.client = client; }

    public List<Comment> list(Map<String, Object> params) {
        return client.get("/api/comments", params, new TypeRef<List<Comment>>(){});
    }

    public Comment byId(int id) {
        return client.get("/api/comments/" + id, null, new TypeRef<Comment>(){});
    }

    public Comment create(CommentTemplate body) {
        return client.post("/api/comments", body, new TypeRef<Comment>(){});
    }

    public Comment update(int id, Map<String, Object> body) {
        return client.patch("/api/comments/" + id, body, new TypeRef<Comment>(){});
    }

    public void delete(int id) {
        client.delete("/api/comments/" + id);
    }
}

class CharacterEndpoints {
    private final ShikimoriClient client;

    public CharacterEndpoints(ShikimoriClient client) { this.client = client; }

    public ShikimoriModels.Character byId(int id) {
        return client.get("/api/characters/" + id, null, new TypeRef<ShikimoriModels.Character>(){});
    }

    public List<CharacterBasic> search(String query) {
        return client.get("/api/characters/search", Map.of("search", query), new TypeRef<List<CharacterBasic>>(){});
    }
}

class PersonEndpoints {
    private final ShikimoriClient client;

    public PersonEndpoints(ShikimoriClient client) { this.client = client; }

    public Person byId(int id) {
        return client.get("/api/people/" + id, null, new TypeRef<Person>(){});
    }

    public List<PersonBasic> search(String query) {
        return client.get("/api/people/search", Map.of("search", query), new TypeRef<List<PersonBasic>>(){});
    }
}

class ReviewEndpoints {
    private final ShikimoriClient client;

    public ReviewEndpoints(ShikimoriClient client) { this.client = client; }

    public List<Review> list(Map<String, Object> params) {
        return client.get("/api/reviews", params, new TypeRef<List<Review>>(){});
    }

    public Review byId(int id) {
        return client.get("/api/reviews/" + id, null, new TypeRef<Review>(){});
    }
}

class GenreEndpoints {
    private final ShikimoriClient client;

    public GenreEndpoints(ShikimoriClient client) { this.client = client; }

    public List<Genre<?>> list() {
        return client.get("/api/genres", null, new TypeRef<List<Genre<?>>>(){});
    }
}

class StudioEndpoints {
    private final ShikimoriClient client;

    public StudioEndpoints(ShikimoriClient client) { this.client = client; }

    public List<Studio> list() {
        return client.get("/api/studios", null, new TypeRef<List<Studio>>(){});
    }
}

class CalendarEndpoints {
    private final ShikimoriClient client;

    public CalendarEndpoints(ShikimoriClient client) { this.client = client; }

    public List<Episode> list(boolean censored) {
        return client.get("/api/calendar", Map.of("censored", censored), new TypeRef<List<Episode>>(){});
    }
}

class AbuseRequestEndpoints {
    private final ShikimoriClient client;

    public AbuseRequestEndpoints(ShikimoriClient client) { this.client = client; }

    public void offtopic(int commentId) {
        client.post("/api/v2/abuse_requests/offtopic", Map.of("comment_id", commentId), null);
    }

    public void review(Map<String, Object> params) {
        client.post("/api/v2/abuse_requests/review", params, null);
    }

    public void abuse(Map<String, Object> params) {
        client.post("/api/v2/abuse_requests/abuse", params, null);
    }

    public void spoiler(Map<String, Object> params) {
        client.post("/api/v2/abuse_requests/spoiler", params, null);
    }
}

class FriendEndpoints {
    private final ShikimoriClient client;

    public FriendEndpoints(ShikimoriClient client) { this.client = client; }

    public void add(int userId) {
        client.post("/api/friends/" + userId, null, null);
    }

    public void remove(int userId) {
        client.delete("/api/friends/" + userId);
    }
}

class FavoriteEndpoints {
    private final ShikimoriClient client;

    public FavoriteEndpoints(ShikimoriClient client) { this.client = client; }

    public void add(int linkedId, String linkedType, String kind) {
        String path = "/api/favorites/" + linkedType + "/" + linkedId;
        if (kind != null && !kind.isEmpty()) {
            path += "/" + kind;
        }
        client.post(path, null, null);
    }

    public void remove(int linkedId, String linkedType) {
        client.delete("/api/favorites/" + linkedType + "/" + linkedId);
    }

    public void reorder(int id, int newIndex) {
        client.post("/api/favorites/" + id + "/reorder", Map.of("new_index", newIndex), null);
    }
}

class BanEndpoints {
    private final ShikimoriClient client;

    public BanEndpoints(ShikimoriClient client) { this.client = client; }

    public List<Ban> list() {
        return client.get("/api/bans", null, new TypeRef<List<Ban>>(){});
    }
}

class ForumEndpoints {
    private final ShikimoriClient client;

    public ForumEndpoints(ShikimoriClient client) { this.client = client; }

    public List<Forum> list() {
        return client.get("/api/forums", null, new TypeRef<List<Forum>>(){});
    }
}

class AchievementEndpoints {
    private final ShikimoriClient client;

    public AchievementEndpoints(ShikimoriClient client) { this.client = client; }

    public List<Achievement> list(int userId) {
        return client.get("/api/achievements", Map.of("user_id", userId), new TypeRef<List<Achievement>>(){});
    }
}

class EpisodeNotificationEndpoints {
    private final ShikimoriClient client;

    public EpisodeNotificationEndpoints(ShikimoriClient client) { this.client = client; }

    public EpisodeNotification create(EpisodeNotificationTemplate body) {
        return client.post("/api/v2/episode_notifications", body, new TypeRef<EpisodeNotification>(){});
    }
}

class GraphQLEndpoints {
    private final ShikimoriClient client;

    public GraphQLEndpoints(ShikimoriClient client) { this.client = client; }

    public GraphQLResponse query(GraphQLRequest request) {
        return client.post("/api/graphql", request, new TypeRef<GraphQLResponse>(){});
    }
}

class AppearsEndpoints {
    private final ShikimoriClient client;

    public AppearsEndpoints(ShikimoriClient client) { this.client = client; }

    public void markAsRead(String ids) {
        client.post("/api/appears", Map.of("ids", ids), null);
    }
}

public class ShikimoriApi {
    private final ShikimoriClient client;

    private final AnimeEndpoints animes;
    private final MangaEndpoints mangas;
    private final RanobeEndpoints ranobe;
    private final UserEndpoints users;
    private final UserRateEndpoints userRates;
    private final TopicEndpoints topics;
    private final CommentEndpoints comments;
    private final CharacterEndpoints characters;
    private final PersonEndpoints persons;
    private final ReviewEndpoints reviews;
    private final GenreEndpoints genres;
    private final StudioEndpoints studios;
    private final CalendarEndpoints calendars;
    private final AbuseRequestEndpoints abuseRequests;
    private final FriendEndpoints friends;
    private final FavoriteEndpoints favorites;
    private final BanEndpoints bans;
    private final ForumEndpoints forums;
    private final AchievementEndpoints achievements;
    private final EpisodeNotificationEndpoints episodeNotifications;
    private final GraphQLEndpoints graphql;
    private final AppearsEndpoints appears;

    public ShikimoriApi() {
        this((String) null);
    }

    public ShikimoriApi(String accessToken) {
        this.client = new ShikimoriClient(accessToken);

        this.animes = new AnimeEndpoints(client);
        this.mangas = new MangaEndpoints(client);
        this.ranobe = new RanobeEndpoints(client);
        this.users = new UserEndpoints(client);
        this.userRates = new UserRateEndpoints(client);
        this.topics = new TopicEndpoints(client);
        this.comments = new CommentEndpoints(client);
        this.characters = new CharacterEndpoints(client);
        this.persons = new PersonEndpoints(client);
        this.reviews = new ReviewEndpoints(client);
        this.genres = new GenreEndpoints(client);
        this.studios = new StudioEndpoints(client);
        this.calendars = new CalendarEndpoints(client);
        this.abuseRequests = new AbuseRequestEndpoints(client);
        this.friends = new FriendEndpoints(client);
        this.favorites = new FavoriteEndpoints(client);
        this.bans = new BanEndpoints(client);
        this.forums = new ForumEndpoints(client);
        this.achievements = new AchievementEndpoints(client);
        this.episodeNotifications = new EpisodeNotificationEndpoints(client);
        this.graphql = new GraphQLEndpoints(client);
        this.appears = new AppearsEndpoints(client);
    }

    public void setAccessToken(String token) {
        client.setAccessToken(token);
    }

    public String getAccessToken() {
        return client.getAccessToken();
    }

    public ShikimoriClient getClient() { return client; }

    public AnimeEndpoints animes() { return animes; }
    public MangaEndpoints mangas() { return mangas; }
    public RanobeEndpoints ranobe() { return ranobe; }
    public UserEndpoints users() { return users; }
    public UserRateEndpoints userRates() { return userRates; }
    public TopicEndpoints topics() { return topics; }
    public CommentEndpoints comments() { return comments; }
    public CharacterEndpoints characters() { return characters; }
    public PersonEndpoints persons() { return persons; }
    public ReviewEndpoints reviews() { return reviews; }
    public GenreEndpoints genres() { return genres; }
    public StudioEndpoints studios() { return studios; }
    public CalendarEndpoints calendars() { return calendars; }
    public AbuseRequestEndpoints abuseRequests() { return abuseRequests; }
    public FriendEndpoints friends() { return friends; }
    public FavoriteEndpoints favorites() { return favorites; }
    public BanEndpoints bans() { return bans; }
    public ForumEndpoints forums() { return forums; }
    public AchievementEndpoints achievements() { return achievements; }
    public EpisodeNotificationEndpoints episodeNotifications() { return episodeNotifications; }
    public GraphQLEndpoints graphql() { return graphql; }
    public AppearsEndpoints appears() { return appears; }
}