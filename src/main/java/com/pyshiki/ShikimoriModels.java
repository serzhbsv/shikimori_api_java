package com.pyshiki;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

class ApiException extends RuntimeException {
    private final int code;
    private final String responseBody;

    public ApiException(String message) {
        this(message, -1, null);
    }

    public ApiException(String message, int code, String responseBody) {
        super(message);
        this.code = code;
        this.responseBody = responseBody;
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
        this.code = -1;
        this.responseBody = null;
    }

    public int getCode() { return code; }
    public String getResponseBody() { return responseBody; }
}

class RateLimitException extends ApiException {
    public RateLimitException(String message) {
        super(message);
    }
}

class RateLimiter {
    private static final int MAX_PER_SECOND = 5;
    private static final int MAX_PER_MINUTE = 90;

    private final AtomicInteger requestsSecond = new AtomicInteger(0);
    private final AtomicInteger requestsMinute = new AtomicInteger(0);
    private long resetSecond = System.currentTimeMillis();
    private long resetMinute = System.currentTimeMillis();

    public synchronized void waitIfNeeded() throws InterruptedException {
        long now = System.currentTimeMillis();

        if (now - resetSecond >= 1000) {
            requestsSecond.set(0);
            resetSecond = now;
        }
        if (now - resetMinute >= 60000) {
            requestsMinute.set(0);
            resetMinute = now;
        }

        if (requestsSecond.get() >= MAX_PER_SECOND) {
            long wait = 1000 - (now - resetSecond);
            if (wait > 0) Thread.sleep(wait);
            requestsSecond.set(0);
            resetSecond = System.currentTimeMillis();
        }
        if (requestsMinute.get() >= MAX_PER_MINUTE) {
            long wait = 60000 - (now - resetMinute);
            if (wait > 0) Thread.sleep(wait);
            requestsMinute.set(0);
            resetMinute = System.currentTimeMillis();
        }

        requestsSecond.incrementAndGet();
        requestsMinute.incrementAndGet();
    }
}

class Linkable {}

class Id<T> {
    private T id;
    public Id(T id) { this.id = id; }
    public T getId() { return id; }
    public void setId(T id) { this.id = id; }
}

public class ShikimoriModels {

    public static class ImageSet {
        private Map<String, String> images;
        public String getOriginal() { return get("original"); }
        public String getMain() { return get("main"); }
        public String getPreview() { return get("preview"); }
        public String get(String size) {
            return images != null ? images.get(size) : null;
        }
        public Map<String, String> getAll() { return images; }
        public void setImages(Map<String, String> images) { this.images = images; }
    }

    public static class Image {
        private int id;
        @SerializedName("original_url") private String originalUrl;
        @SerializedName("main_url") private String mainUrl;
        @SerializedName("preview_url") private String previewUrl;
        @SerializedName("can_destroy") private boolean canDestroy;
        @SerializedName("user_id") private int userId;

        public int getId() { return id; }
        public String getOriginalUrl() { return originalUrl; }
        public String getMainUrl() { return mainUrl; }
        public String getPreviewUrl() { return previewUrl; }
        public boolean isCanDestroy() { return canDestroy; }
        public int getUserId() { return userId; }
        public void setId(int id) { this.id = id; }
        public void setOriginalUrl(String originalUrl) { this.originalUrl = originalUrl; }
        public void setMainUrl(String mainUrl) { this.mainUrl = mainUrl; }
        public void setPreviewUrl(String previewUrl) { this.previewUrl = previewUrl; }
        public void setCanDestroy(boolean canDestroy) { this.canDestroy = canDestroy; }
        public void setUserId(int userId) { this.userId = userId; }
    }

    public static class Screenshot {
        private String original;
        private String preview;
        public String getOriginal() { return original; }
        public String getPreview() { return preview; }
        public void setOriginal(String original) { this.original = original; }
        public void setPreview(String preview) { this.preview = preview; }
    }

    public static abstract class Content {
        protected String name;
        protected String russian;
        protected ImageSet image;
        protected String url;
        protected String score;
        @SerializedName("aired_on") protected String airedOn;
        @SerializedName("released_on") protected String releasedOn;
        protected List<String> english;
        protected List<String> japanese;
        protected List<String> synonims;
        @SerializedName("license_name_ru") protected String licenseNameRu;
        protected String description;
        @SerializedName("description_html") protected String descriptionHtml;
        @SerializedName("description_source") protected String descriptionSource;
        protected String franchise;
        protected boolean favoured;
        protected boolean anons;
        protected boolean ongoing;
        @SerializedName("thread_id") protected int threadId;
        @SerializedName("topic_id") protected int topicId;
        @SerializedName("myanimelist_id") protected int myanimelistId;
        @SerializedName("rates_scores_stats") protected List<RateScoreStat> ratesScoresStats;
        @SerializedName("rates_statuses_stats") protected List<RateStatusStat> ratesStatusesStats;
        protected List<String> licensors;
        protected List<Publisher> publishers;
        @SerializedName("user_rate") protected UserRateBasic userRate;

        public String getName() { return name; }
        public String getRussian() { return russian; }
        public ImageSet getImage() { return image; }
        public String getUrl() { return url; }
        public String getScore() { return score; }
        public String getAiredOn() { return airedOn; }
        public String getReleasedOn() { return releasedOn; }
        public List<String> getEnglish() { return english; }
        public List<String> getJapanese() { return japanese; }
        public List<String> getSynonims() { return synonims; }
        public String getLicenseNameRu() { return licenseNameRu; }
        public String getDescription() { return description; }
        public String getDescriptionHtml() { return descriptionHtml; }
        public String getDescriptionSource() { return descriptionSource; }
        public String getFranchise() { return franchise; }
        public boolean isFavoured() { return favoured; }
        public boolean isAnons() { return anons; }
        public boolean isOngoing() { return ongoing; }
        public int getThreadId() { return threadId; }
        public int getTopicId() { return topicId; }
        public int getMyanimelistId() { return myanimelistId; }
        public List<RateScoreStat> getRatesScoresStats() { return ratesScoresStats; }
        public List<RateStatusStat> getRatesStatusesStats() { return ratesStatusesStats; }
        public List<String> getLicensors() { return licensors; }
        public List<Publisher> getPublishers() { return publishers; }
        public UserRateBasic getUserRate() { return userRate; }
    }

    public static class RateScoreStat {
        private String name;
        private int value;
        public String getName() { return name; }
        public int getValue() { return value; }
    }

    public static class RateStatusStat {
        private String name;
        private int value;
        public String getName() { return name; }
        public int getValue() { return value; }
    }

    public static class Publisher {
        private int id;
        private String name;
        public int getId() { return id; }
        public String getName() { return name; }
        public void setId(int id) { this.id = id; }
        public void setName(String name) { this.name = name; }
    }

    public static class Genre<T> {
        private int id;
        private String name;
        private String russian;
        private String kind;
        @SerializedName("entry_type") private String entryType;

        public int getId() { return id; }
        public String getName() { return name; }
        public String getRussian() { return russian; }
        public String getKind() { return kind; }
        public String getEntryType() { return entryType; }
        public void setId(int id) { this.id = id; }
        public void setName(String name) { this.name = name; }
        public void setRussian(String russian) { this.russian = russian; }
        public void setKind(String kind) { this.kind = kind; }
        public void setEntryType(String entryType) { this.entryType = entryType; }
    }

    public static class Studio {
        private int id;
        private String name;
        @SerializedName("filtered_name") private String filteredName;
        private boolean real;
        private String image;

        public int getId() { return id; }
        public String getName() { return name; }
        public String getFilteredName() { return filteredName; }
        public boolean isReal() { return real; }
        public String getImage() { return image; }
        public void setId(int id) { this.id = id; }
        public void setName(String name) { this.name = name; }
        public void setFilteredName(String filteredName) { this.filteredName = filteredName; }
        public void setReal(boolean real) { this.real = real; }
        public void setImage(String image) { this.image = image; }
    }

    public static class Video {
        private int id;
        private String url;
        @SerializedName("image_url") private String imageUrl;
        @SerializedName("player_url") private String playerUrl;
        private String name;
        private String kind;
        private String hosting;

        public int getId() { return id; }
        public String getUrl() { return url; }
        public String getImageUrl() { return imageUrl; }
        public String getPlayerUrl() { return playerUrl; }
        public String getName() { return name; }
        public String getKind() { return kind; }
        public String getHosting() { return hosting; }
        public void setId(int id) { this.id = id; }
        public void setUrl(String url) { this.url = url; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public void setPlayerUrl(String playerUrl) { this.playerUrl = playerUrl; }
        public void setName(String name) { this.name = name; }
        public void setKind(String kind) { this.kind = kind; }
        public void setHosting(String hosting) { this.hosting = hosting; }
    }

    public static class Anime extends Content {
        private int id;
        private String kind;
        private String status;
        private int episodes;
        @SerializedName("episodes_aired") private int episodesAired;
        private String rating;
        private List<Genre> genres;
        private int duration;
        @SerializedName("updated_at") private String updatedAt;
        @SerializedName("next_episode_at") private String nextEpisodeAt;
        private List<String> fansubbers;
        private List<String> fandubbers;
        private List<Studio> studios;
        private List<Video> videos;
        private List<ImageSet> screenshots;

        public int getId() { return id; }
        public String getKind() { return kind; }
        public String getStatus() { return status; }
        public int getEpisodes() { return episodes; }
        public int getEpisodesAired() { return episodesAired; }
        public String getRating() { return rating; }
        public List<Genre> getGenres() { return genres; }
        public int getDuration() { return duration; }
        public String getUpdatedAt() { return updatedAt; }
        public String getNextEpisodeAt() { return nextEpisodeAt; }
        public List<String> getFansubbers() { return fansubbers; }
        public List<String> getFandubbers() { return fandubbers; }
        public List<Studio> getStudios() { return studios; }
        public List<Video> getVideos() { return videos; }
        public List<ImageSet> getScreenshots() { return screenshots; }

        public void setId(int id) { this.id = id; }
        public void setKind(String kind) { this.kind = kind; }
        public void setStatus(String status) { this.status = status; }
        public void setEpisodes(int episodes) { this.episodes = episodes; }
        public void setEpisodesAired(int episodesAired) { this.episodesAired = episodesAired; }
        public void setRating(String rating) { this.rating = rating; }
        public void setGenres(List<Genre> genres) { this.genres = genres; }
        public void setDuration(int duration) { this.duration = duration; }
        public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
        public void setNextEpisodeAt(String nextEpisodeAt) { this.nextEpisodeAt = nextEpisodeAt; }
        public void setFansubbers(List<String> fansubbers) { this.fansubbers = fansubbers; }
        public void setFandubbers(List<String> fandubbers) { this.fandubbers = fandubbers; }
        public void setStudios(List<Studio> studios) { this.studios = studios; }
        public void setVideos(List<Video> videos) { this.videos = videos; }
        public void setScreenshots(List<ImageSet> screenshots) { this.screenshots = screenshots; }

        public String getStatusRussian() {
            if (status == null) return "Unknown";
            switch (status) {
                case "anons": return "Anons";
                case "ongoing": return "Ongoing";
                case "released": return "Released";
                default: return status;
            }
        }

        public String getKindRussian() {
            if (kind == null) return "Unknown";
            switch (kind) {
                case "tv": return "TV";
                case "movie": return "Movie";
                case "ova": return "OVA";
                case "ona": return "ONA";
                case "special": return "Special";
                default: return kind;
            }
        }
    }

    public static class AnimeBasic extends Content {
        private int id;
        private String kind;
        private String status;
        private int episodes;
        @SerializedName("episodes_aired") private int episodesAired;

        public int getId() { return id; }
        public String getKind() { return kind; }
        public String getStatus() { return status; }
        public int getEpisodes() { return episodes; }
        public int getEpisodesAired() { return episodesAired; }
        public void setId(int id) { this.id = id; }
        public void setKind(String kind) { this.kind = kind; }
        public void setStatus(String status) { this.status = status; }
        public void setEpisodes(int episodes) { this.episodes = episodes; }
        public void setEpisodesAired(int episodesAired) { this.episodesAired = episodesAired; }
    }

    public static class AnimeRelation {
        private String relation;
        @SerializedName("relation_string") private String relationString;
        private AnimeBasic anime;

        public String getRelation() { return relation; }
        public String getRelationString() { return relationString; }
        public AnimeBasic getAnime() { return anime; }
        public void setRelation(String relation) { this.relation = relation; }
        public void setRelationString(String relationString) { this.relationString = relationString; }
        public void setAnime(AnimeBasic anime) { this.anime = anime; }
    }

    public static class Manga extends Content {
        private int id;
        private String kind;
        private String status;
        private List<Genre> genres;
        private int volumes;
        private int chapters;
        private List<Publisher> publishers;

        public int getId() { return id; }
        public String getKind() { return kind; }
        public String getStatus() { return status; }
        public List<Genre> getGenres() { return genres; }
        public int getVolumes() { return volumes; }
        public int getChapters() { return chapters; }
        public List<Publisher> getPublishers() { return publishers; }
        public void setId(int id) { this.id = id; }
        public void setKind(String kind) { this.kind = kind; }
        public void setStatus(String status) { this.status = status; }
        public void setGenres(List<Genre> genres) { this.genres = genres; }
        public void setVolumes(int volumes) { this.volumes = volumes; }
        public void setChapters(int chapters) { this.chapters = chapters; }
        public void setPublishers(List<Publisher> publishers) { this.publishers = publishers; }
    }

    public static class MangaBasic extends Content {
        private int id;
        private String kind;
        private String status;
        private int volumes;
        private int chapters;

        public int getId() { return id; }
        public String getKind() { return kind; }
        public String getStatus() { return status; }
        public int getVolumes() { return volumes; }
        public int getChapters() { return chapters; }
        public void setId(int id) { this.id = id; }
        public void setKind(String kind) { this.kind = kind; }
        public void setStatus(String status) { this.status = status; }
        public void setVolumes(int volumes) { this.volumes = volumes; }
        public void setChapters(int chapters) { this.chapters = chapters; }
    }

    public static class MangaRelation {
        private String relation;
        @SerializedName("relation_string") private String relationString;
        private MangaBasic manga;

        public String getRelation() { return relation; }
        public String getRelationString() { return relationString; }
        public MangaBasic getManga() { return manga; }
        public void setRelation(String relation) { this.relation = relation; }
        public void setRelationString(String relationString) { this.relationString = relationString; }
        public void setManga(MangaBasic manga) { this.manga = manga; }
    }

    public static class Ranobe extends Content {
        private int id;
        private String kind;
        private String status;
        private List<Genre> genres;
        private int volumes;
        private int chapters;
        private List<Publisher> publishers;

        public int getId() { return id; }
        public String getKind() { return kind; }
        public String getStatus() { return status; }
        public List<Genre> getGenres() { return genres; }
        public int getVolumes() { return volumes; }
        public int getChapters() { return chapters; }
        public List<Publisher> getPublishers() { return publishers; }
        public void setId(int id) { this.id = id; }
        public void setKind(String kind) { this.kind = kind; }
        public void setStatus(String status) { this.status = status; }
        public void setGenres(List<Genre> genres) { this.genres = genres; }
        public void setVolumes(int volumes) { this.volumes = volumes; }
        public void setChapters(int chapters) { this.chapters = chapters; }
        public void setPublishers(List<Publisher> publishers) { this.publishers = publishers; }
    }

    public static class RanobeBasic extends Content {
        private int id;
        private String kind;
        private String status;
        private int volumes;
        private int chapters;

        public int getId() { return id; }
        public String getKind() { return kind; }
        public String getStatus() { return status; }
        public int getVolumes() { return volumes; }
        public int getChapters() { return chapters; }
        public void setId(int id) { this.id = id; }
        public void setKind(String kind) { this.kind = kind; }
        public void setStatus(String status) { this.status = status; }
        public void setVolumes(int volumes) { this.volumes = volumes; }
        public void setChapters(int chapters) { this.chapters = chapters; }
    }

    public static class RanobeRelation {
        private String relation;
        @SerializedName("relation_string") private String relationString;
        private RanobeBasic ranobe;

        public String getRelation() { return relation; }
        public String getRelationString() { return relationString; }
        public RanobeBasic getRanobe() { return ranobe; }
        public void setRelation(String relation) { this.relation = relation; }
        public void setRelationString(String relationString) { this.relationString = relationString; }
        public void setRanobe(RanobeBasic ranobe) { this.ranobe = ranobe; }
    }

    public static class User {
        private int id;
        private String nickname;
        private String avatar;
        private ImageSet image;
        @SerializedName("last_online_at") private String lastOnlineAt;
        private String url;
        private String name;
        private String sex;
        @SerializedName("full_years") private Integer fullYears;
        @SerializedName("last_online") private String lastOnline;
        private String website;
        private String location;
        private boolean banned;
        private String about;
        @SerializedName("about_html") private String aboutHtml;
        @SerializedName("common_info") private List<String> commonInfo;
        @SerializedName("show_comments") private boolean showComments;
        @SerializedName("in_friends") private Boolean inFriends;
        @SerializedName("is_ignored") private boolean isIgnored;
        private UserStats stats;
        @SerializedName("style_id") private Integer styleId;

        public int getId() { return id; }
        public String getNickname() { return nickname; }
        public String getAvatar() { return avatar; }
        public ImageSet getImage() { return image; }
        public String getLastOnlineAt() { return lastOnlineAt; }
        public String getUrl() { return url; }
        public String getName() { return name; }
        public String getSex() { return sex; }
        public Integer getFullYears() { return fullYears; }
        public String getLastOnline() { return lastOnline; }
        public String getWebsite() { return website; }
        public String getLocation() { return location; }
        public boolean isBanned() { return banned; }
        public String getAbout() { return about; }
        public String getAboutHtml() { return aboutHtml; }
        public List<String> getCommonInfo() { return commonInfo; }
        public boolean isShowComments() { return showComments; }
        public Boolean getInFriends() { return inFriends; }
        public boolean isIgnored() { return isIgnored; }
        public UserStats getStats() { return stats; }
        public Integer getStyleId() { return styleId; }
        public void setId(int id) { this.id = id; }
        public void setNickname(String nickname) { this.nickname = nickname; }
        public void setAvatar(String avatar) { this.avatar = avatar; }
        public void setImage(ImageSet image) { this.image = image; }
        public void setLastOnlineAt(String lastOnlineAt) { this.lastOnlineAt = lastOnlineAt; }
        public void setUrl(String url) { this.url = url; }
        public void setName(String name) { this.name = name; }
        public void setSex(String sex) { this.sex = sex; }
        public void setFullYears(Integer fullYears) { this.fullYears = fullYears; }
        public void setLastOnline(String lastOnline) { this.lastOnline = lastOnline; }
        public void setWebsite(String website) { this.website = website; }
        public void setLocation(String location) { this.location = location; }
        public void setBanned(boolean banned) { this.banned = banned; }
        public void setAbout(String about) { this.about = about; }
        public void setAboutHtml(String aboutHtml) { this.aboutHtml = aboutHtml; }
        public void setCommonInfo(List<String> commonInfo) { this.commonInfo = commonInfo; }
        public void setShowComments(boolean showComments) { this.showComments = showComments; }
        public void setInFriends(Boolean inFriends) { this.inFriends = inFriends; }
        public void setIgnored(boolean ignored) { isIgnored = ignored; }
        public void setStats(UserStats stats) { this.stats = stats; }
        public void setStyleId(Integer styleId) { this.styleId = styleId; }
    }

    public static class UserBasic {
        private int id;
        private String nickname;
        private String avatar;
        private ImageSet image;
        @SerializedName("last_online_at") private String lastOnlineAt;
        private String url;

        public int getId() { return id; }
        public String getNickname() { return nickname; }
        public String getAvatar() { return avatar; }
        public ImageSet getImage() { return image; }
        public String getLastOnlineAt() { return lastOnlineAt; }
        public String getUrl() { return url; }
        public void setId(int id) { this.id = id; }
        public void setNickname(String nickname) { this.nickname = nickname; }
        public void setAvatar(String avatar) { this.avatar = avatar; }
        public void setImage(ImageSet image) { this.image = image; }
        public void setLastOnlineAt(String lastOnlineAt) { this.lastOnlineAt = lastOnlineAt; }
        public void setUrl(String url) { this.url = url; }
    }

    public static class UserStats {
        private Map<String, UserStatsStatus> statuses;
        private Map<String, UserStatsStatus> fullStatuses;
        private Map<String, UserStatsStatus> scores;
        private Map<String, UserStatsStatus> types;
        private Map<String, UserStatsStatus> ratings;
        @SerializedName("has_anime?") private boolean hasAnime;
        @SerializedName("has_manga?") private boolean hasManga;
        private List<Object> genres;
        private List<Object> studios;
        private List<Object> publishers;
        private List<UserActivity> activity;

        public Map<String, UserStatsStatus> getStatuses() { return statuses; }
        public Map<String, UserStatsStatus> getFullStatuses() { return fullStatuses; }
        public Map<String, UserStatsStatus> getScores() { return scores; }
        public Map<String, UserStatsStatus> getTypes() { return types; }
        public Map<String, UserStatsStatus> getRatings() { return ratings; }
        public boolean isHasAnime() { return hasAnime; }
        public boolean isHasManga() { return hasManga; }
        public List<Object> getGenres() { return genres; }
        public List<Object> getStudios() { return studios; }
        public List<Object> getPublishers() { return publishers; }
        public List<UserActivity> getActivity() { return activity; }
        public void setStatuses(Map<String, UserStatsStatus> statuses) { this.statuses = statuses; }
        public void setFullStatuses(Map<String, UserStatsStatus> fullStatuses) { this.fullStatuses = fullStatuses; }
        public void setScores(Map<String, UserStatsStatus> scores) { this.scores = scores; }
        public void setTypes(Map<String, UserStatsStatus> types) { this.types = types; }
        public void setRatings(Map<String, UserStatsStatus> ratings) { this.ratings = ratings; }
        public void setHasAnime(boolean hasAnime) { this.hasAnime = hasAnime; }
        public void setHasManga(boolean hasManga) { this.hasManga = hasManga; }
        public void setGenres(List<Object> genres) { this.genres = genres; }
        public void setStudios(List<Object> studios) { this.studios = studios; }
        public void setPublishers(List<Object> publishers) { this.publishers = publishers; }
        public void setActivity(List<UserActivity> activity) { this.activity = activity; }
    }

    public static class UserStatsStatus {
        private int id;
        private String groupedId;
        private String name;
        private int size;
        private String type;
        public int getId() { return id; }
        public String getGroupedId() { return groupedId; }
        public String getName() { return name; }
        public int getSize() { return size; }
        public String getType() { return type; }
        public void setId(int id) { this.id = id; }
        public void setGroupedId(String groupedId) { this.groupedId = groupedId; }
        public void setName(String name) { this.name = name; }
        public void setSize(int size) { this.size = size; }
        public void setType(String type) { this.type = type; }
    }

    public static class UserActivity {
        private List<Integer> name;
        private int value;
        public List<Integer> getName() { return name; }
        public int getValue() { return value; }
        public void setName(List<Integer> name) { this.name = name; }
        public void setValue(int value) { this.value = value; }
    }

    public static class UserFavourites {
        private List<UserFavourite> animes;
        private List<UserFavourite> mangas;
        private List<UserFavourite> ranobe;
        private List<UserFavourite> characters;
        private List<UserFavourite> people;
        private List<UserFavourite> mangakas;
        private List<UserFavourite> seyu;
        private List<UserFavourite> producers;

        public List<UserFavourite> getAnimes() { return animes; }
        public List<UserFavourite> getMangas() { return mangas; }
        public List<UserFavourite> getRanobe() { return ranobe; }
        public List<UserFavourite> getCharacters() { return characters; }
        public List<UserFavourite> getPeople() { return people; }
        public List<UserFavourite> getMangakas() { return mangakas; }
        public List<UserFavourite> getSeyu() { return seyu; }
        public List<UserFavourite> getProducers() { return producers; }
        public void setAnimes(List<UserFavourite> animes) { this.animes = animes; }
        public void setMangas(List<UserFavourite> mangas) { this.mangas = mangas; }
        public void setRanobe(List<UserFavourite> ranobe) { this.ranobe = ranobe; }
        public void setCharacters(List<UserFavourite> characters) { this.characters = characters; }
        public void setPeople(List<UserFavourite> people) { this.people = people; }
        public void setMangakas(List<UserFavourite> mangakas) { this.mangakas = mangakas; }
        public void setSeyu(List<UserFavourite> seyu) { this.seyu = seyu; }
        public void setProducers(List<UserFavourite> producers) { this.producers = producers; }
    }

    public static class UserFavourite {
        private int id;
        private String name;
        private String russian;
        private String image;
        private String url;
        public int getId() { return id; }
        public String getName() { return name; }
        public String getRussian() { return russian; }
        public String getImage() { return image; }
        public String getUrl() { return url; }
        public void setId(int id) { this.id = id; }
        public void setName(String name) { this.name = name; }
        public void setRussian(String russian) { this.russian = russian; }
        public void setImage(String image) { this.image = image; }
        public void setUrl(String url) { this.url = url; }
    }

    public static class UserHistoryRecord {
        private int id;
        @SerializedName("created_at") private String createdAt;
        private String description;
        private Object target;

        public int getId() { return id; }
        public String getCreatedAt() { return createdAt; }
        public String getDescription() { return description; }
        public Object getTarget() { return target; }
        public void setId(int id) { this.id = id; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
        public void setDescription(String description) { this.description = description; }
        public void setTarget(Object target) { this.target = target; }
    }

    public static class UserUnreadMessages {
        private int messages;
        private int news;
        private int notifications;
        public int getMessages() { return messages; }
        public int getNews() { return news; }
        public int getNotifications() { return notifications; }
        public void setMessages(int messages) { this.messages = messages; }
        public void setNews(int news) { this.news = news; }
        public void setNotifications(int notifications) { this.notifications = notifications; }
    }

    public static class UserRate {
        private int id;
        @SerializedName("user_id") private int userId;
        @SerializedName("target_id") private int targetId;
        @SerializedName("target_type") private String targetType;
        private int score;
        private String status;
        private int rewatches;
        private int episodes;
        private int volumes;
        private int chapters;
        private String text;
        @SerializedName("text_html") private String textHtml;
        @SerializedName("created_at") private String createdAt;
        @SerializedName("updated_at") private String updatedAt;

        public int getId() { return id; }
        public int getUserId() { return userId; }
        public int getTargetId() { return targetId; }
        public String getTargetType() { return targetType; }
        public int getScore() { return score; }
        public String getStatus() { return status; }
        public int getRewatches() { return rewatches; }
        public int getEpisodes() { return episodes; }
        public int getVolumes() { return volumes; }
        public int getChapters() { return chapters; }
        public String getText() { return text; }
        public String getTextHtml() { return textHtml; }
        public String getCreatedAt() { return createdAt; }
        public String getUpdatedAt() { return updatedAt; }

        public void setId(int id) { this.id = id; }
        public void setUserId(int userId) { this.userId = userId; }
        public void setTargetId(int targetId) { this.targetId = targetId; }
        public void setTargetType(String targetType) { this.targetType = targetType; }
        public void setScore(int score) { this.score = score; }
        public void setStatus(String status) { this.status = status; }
        public void setRewatches(int rewatches) { this.rewatches = rewatches; }
        public void setEpisodes(int episodes) { this.episodes = episodes; }
        public void setVolumes(int volumes) { this.volumes = volumes; }
        public void setChapters(int chapters) { this.chapters = chapters; }
        public void setText(String text) { this.text = text; }
        public void setTextHtml(String textHtml) { this.textHtml = textHtml; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
        public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

        public String getStatusRussian() {
            if (status == null) return "Unknown";
            switch (status) {
                case "planned": return "Planned";
                case "watching": return "Watching";
                case "completed": return "Completed";
                case "rewatching": return "Rewatching";
                case "on_hold": return "On Hold";
                case "dropped": return "Dropped";
                default: return status;
            }
        }
    }

    public static class UserRateBasic {
        private int id;
        private int score;
        private String status;
        private int rewatches;
        private int episodes;
        private int volumes;
        private int chapters;
        private String text;
        @SerializedName("text_html") private String textHtml;
        @SerializedName("created_at") private String createdAt;
        @SerializedName("updated_at") private String updatedAt;

        public int getId() { return id; }
        public int getScore() { return score; }
        public String getStatus() { return status; }
        public int getRewatches() { return rewatches; }
        public int getEpisodes() { return episodes; }
        public int getVolumes() { return volumes; }
        public int getChapters() { return chapters; }
        public String getText() { return text; }
        public String getTextHtml() { return textHtml; }
        public String getCreatedAt() { return createdAt; }
        public String getUpdatedAt() { return updatedAt; }
        public void setId(int id) { this.id = id; }
        public void setScore(int score) { this.score = score; }
        public void setStatus(String status) { this.status = status; }
        public void setRewatches(int rewatches) { this.rewatches = rewatches; }
        public void setEpisodes(int episodes) { this.episodes = episodes; }
        public void setVolumes(int volumes) { this.volumes = volumes; }
        public void setChapters(int chapters) { this.chapters = chapters; }
        public void setText(String text) { this.text = text; }
        public void setTextHtml(String textHtml) { this.textHtml = textHtml; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
        public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    }

    public static class UserRateTemplate {
        @SerializedName("user_id") private int userId;
        @SerializedName("target_id") private int targetId;
        @SerializedName("target_type") private String targetType;
        private Integer score;
        private String status;
        private Integer rewatches;
        private Integer episodes;
        private Integer volumes;
        private Integer chapters;
        private String text;

        public int getUserId() { return userId; }
        public int getTargetId() { return targetId; }
        public String getTargetType() { return targetType; }
        public Integer getScore() { return score; }
        public String getStatus() { return status; }
        public Integer getRewatches() { return rewatches; }
        public Integer getEpisodes() { return episodes; }
        public Integer getVolumes() { return volumes; }
        public Integer getChapters() { return chapters; }
        public String getText() { return text; }
        public void setUserId(int userId) { this.userId = userId; }
        public void setTargetId(int targetId) { this.targetId = targetId; }
        public void setTargetType(String targetType) { this.targetType = targetType; }
        public void setScore(Integer score) { this.score = score; }
        public void setStatus(String status) { this.status = status; }
        public void setRewatches(Integer rewatches) { this.rewatches = rewatches; }
        public void setEpisodes(Integer episodes) { this.episodes = episodes; }
        public void setVolumes(Integer volumes) { this.volumes = volumes; }
        public void setChapters(Integer chapters) { this.chapters = chapters; }
        public void setText(String text) { this.text = text; }
    }

    public static class Topic<T> {
        private int id;
        @SerializedName("topic_title") private String topicTitle;
        private String body;
        @SerializedName("html_body") private String htmlBody;
        @SerializedName("html_footer") private String htmlFooter;
        @SerializedName("created_at") private String createdAt;
        @SerializedName("comments_count") private int commentsCount;
        private Forum forum;
        private UserBasic user;
        private String type;
        @SerializedName("linked_id") private int linkedId;
        @SerializedName("linked_type") private String linkedType;
        private T linked;
        private boolean viewed;
        @SerializedName("last_comment_viewed") private Boolean lastCommentViewed;
        private String event;
        private Integer episode;

        public int getId() { return id; }
        public String getTopicTitle() { return topicTitle; }
        public String getBody() { return body; }
        public String getHtmlBody() { return htmlBody; }
        public String getHtmlFooter() { return htmlFooter; }
        public String getCreatedAt() { return createdAt; }
        public int getCommentsCount() { return commentsCount; }
        public Forum getForum() { return forum; }
        public UserBasic getUser() { return user; }
        public String getType() { return type; }
        public int getLinkedId() { return linkedId; }
        public String getLinkedType() { return linkedType; }
        public T getLinked() { return linked; }
        public boolean isViewed() { return viewed; }
        public Boolean getLastCommentViewed() { return lastCommentViewed; }
        public String getEvent() { return event; }
        public Integer getEpisode() { return episode; }
        public void setId(int id) { this.id = id; }
        public void setTopicTitle(String topicTitle) { this.topicTitle = topicTitle; }
        public void setBody(String body) { this.body = body; }
        public void setHtmlBody(String htmlBody) { this.htmlBody = htmlBody; }
        public void setHtmlFooter(String htmlFooter) { this.htmlFooter = htmlFooter; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
        public void setCommentsCount(int commentsCount) { this.commentsCount = commentsCount; }
        public void setForum(Forum forum) { this.forum = forum; }
        public void setUser(UserBasic user) { this.user = user; }
        public void setType(String type) { this.type = type; }
        public void setLinkedId(int linkedId) { this.linkedId = linkedId; }
        public void setLinkedType(String linkedType) { this.linkedType = linkedType; }
        public void setLinked(T linked) { this.linked = linked; }
        public void setViewed(boolean viewed) { this.viewed = viewed; }
        public void setLastCommentViewed(Boolean lastCommentViewed) { this.lastCommentViewed = lastCommentViewed; }
        public void setEvent(String event) { this.event = event; }
        public void setEpisode(Integer episode) { this.episode = episode; }
    }

    public static class TopicBasic {
        private int id;
        private Object linked;
        private String event;
        private Integer episode;
        @SerializedName("created_at") private String createdAt;
        private String url;

        public int getId() { return id; }
        public Object getLinked() { return linked; }
        public String getEvent() { return event; }
        public Integer getEpisode() { return episode; }
        public String getCreatedAt() { return createdAt; }
        public String getUrl() { return url; }
        public void setId(int id) { this.id = id; }
        public void setLinked(Object linked) { this.linked = linked; }
        public void setEvent(String event) { this.event = event; }
        public void setEpisode(Integer episode) { this.episode = episode; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
        public void setUrl(String url) { this.url = url; }
    }

    public static class TopicIgnore {
        @SerializedName("user_id") private int userId;
        @SerializedName("is_ignored") private boolean isIgnored;

        public int getUserId() { return userId; }
        public boolean isIgnored() { return isIgnored; }
        public void setUserId(int userId) { this.userId = userId; }
        public void setIgnored(boolean ignored) { isIgnored = ignored; }
    }

    public static class Comment {
        private int id;
        @SerializedName("user_id") private int userId;
        @SerializedName("commentable_id") private int commentableId;
        @SerializedName("commentable_type") private String commentableType;
        private String body;
        @SerializedName("html_body") private String htmlBody;
        @SerializedName("created_at") private String createdAt;
        @SerializedName("updated_at") private String updatedAt;
        @SerializedName("is_offtopic") private boolean isOfftopic;
        @SerializedName("is_summary") private boolean isSummary;
        @SerializedName("can_be_edited") private boolean canBeEdited;
        private UserBasic user;

        public int getId() { return id; }
        public int getUserId() { return userId; }
        public int getCommentableId() { return commentableId; }
        public String getCommentableType() { return commentableType; }
        public String getBody() { return body; }
        public String getHtmlBody() { return htmlBody; }
        public String getCreatedAt() { return createdAt; }
        public String getUpdatedAt() { return updatedAt; }
        public boolean isOfftopic() { return isOfftopic; }
        public boolean isSummary() { return isSummary; }
        public boolean isCanBeEdited() { return canBeEdited; }
        public UserBasic getUser() { return user; }
        public void setId(int id) { this.id = id; }
        public void setUserId(int userId) { this.userId = userId; }
        public void setCommentableId(int commentableId) { this.commentableId = commentableId; }
        public void setCommentableType(String commentableType) { this.commentableType = commentableType; }
        public void setBody(String body) { this.body = body; }
        public void setHtmlBody(String htmlBody) { this.htmlBody = htmlBody; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
        public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
        public void setOfftopic(boolean offtopic) { isOfftopic = offtopic; }
        public void setSummary(boolean summary) { isSummary = summary; }
        public void setCanBeEdited(boolean canBeEdited) { this.canBeEdited = canBeEdited; }
        public void setUser(UserBasic user) { this.user = user; }
    }

    public static class CommentBasic {
        private int id;
        private int commentableId;
        private String commentableType;
        private String body;
        private int userId;
        private String createdAt;
        private String updatedAt;
        private boolean isOfftopic;

        public int getId() { return id; }
        public int getCommentableId() { return commentableId; }
        public String getCommentableType() { return commentableType; }
        public String getBody() { return body; }
        public int getUserId() { return userId; }
        public String getCreatedAt() { return createdAt; }
        public String getUpdatedAt() { return updatedAt; }
        public boolean isOfftopic() { return isOfftopic; }
        public void setId(int id) { this.id = id; }
        public void setCommentableId(int commentableId) { this.commentableId = commentableId; }
        public void setCommentableType(String commentableType) { this.commentableType = commentableType; }
        public void setBody(String body) { this.body = body; }
        public void setUserId(int userId) { this.userId = userId; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
        public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
        public void setOfftopic(boolean offtopic) { isOfftopic = offtopic; }
    }

    public static class CommentTemplate {
        private String body;
        private int commentableId;
        private String commentableType;
        private Boolean isOfftopic;

        public String getBody() { return body; }
        public int getCommentableId() { return commentableId; }
        public String getCommentableType() { return commentableType; }
        public Boolean getIsOfftopic() { return isOfftopic; }
        public void setBody(String body) { this.body = body; }
        public void setCommentableId(int commentableId) { this.commentableId = commentableId; }
        public void setCommentableType(String commentableType) { this.commentableType = commentableType; }
        public void setIsOfftopic(Boolean isOfftopic) { this.isOfftopic = isOfftopic; }
    }

    public static class Character {
        private int id;
        private String name;
        private String russian;
        private ImageSet image;
        private String url;
        private String altname;
        private String japanese;
        private String description;
        @SerializedName("description_html") private String descriptionHtml;
        @SerializedName("description_source") private String descriptionSource;
        private boolean favoured;
        @SerializedName("thread_id") private int threadId;
        @SerializedName("topic_id") private int topicId;
        @SerializedName("updated_at") private String updatedAt;
        private List<PersonBasic> seyu;
        private List<RoleBased<AnimeBasic>> animes;
        private List<RoleBased<MangaBasic>> mangas;

        public int getId() { return id; }
        public String getName() { return name; }
        public String getRussian() { return russian; }
        public ImageSet getImage() { return image; }
        public String getUrl() { return url; }
        public String getAltname() { return altname; }
        public String getJapanese() { return japanese; }
        public String getDescription() { return description; }
        public String getDescriptionHtml() { return descriptionHtml; }
        public String getDescriptionSource() { return descriptionSource; }
        public boolean isFavoured() { return favoured; }
        public int getThreadId() { return threadId; }
        public int getTopicId() { return topicId; }
        public String getUpdatedAt() { return updatedAt; }
        public List<PersonBasic> getSeyu() { return seyu; }
        public List<RoleBased<AnimeBasic>> getAnimes() { return animes; }
        public List<RoleBased<MangaBasic>> getMangas() { return mangas; }
        public void setId(int id) { this.id = id; }
        public void setName(String name) { this.name = name; }
        public void setRussian(String russian) { this.russian = russian; }
        public void setImage(ImageSet image) { this.image = image; }
        public void setUrl(String url) { this.url = url; }
        public void setAltname(String altname) { this.altname = altname; }
        public void setJapanese(String japanese) { this.japanese = japanese; }
        public void setDescription(String description) { this.description = description; }
        public void setDescriptionHtml(String descriptionHtml) { this.descriptionHtml = descriptionHtml; }
        public void setDescriptionSource(String descriptionSource) { this.descriptionSource = descriptionSource; }
        public void setFavoured(boolean favoured) { this.favoured = favoured; }
        public void setThreadId(int threadId) { this.threadId = threadId; }
        public void setTopicId(int topicId) { this.topicId = topicId; }
        public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
        public void setSeyu(List<PersonBasic> seyu) { this.seyu = seyu; }
        public void setAnimes(List<RoleBased<AnimeBasic>> animes) { this.animes = animes; }
        public void setMangas(List<RoleBased<MangaBasic>> mangas) { this.mangas = mangas; }
    }

    public static class CharacterBasic {
        private int id;
        private String name;
        private String russian;
        private ImageSet image;
        private String url;

        public int getId() { return id; }
        public String getName() { return name; }
        public String getRussian() { return russian; }
        public ImageSet getImage() { return image; }
        public String getUrl() { return url; }
        public void setId(int id) { this.id = id; }
        public void setName(String name) { this.name = name; }
        public void setRussian(String russian) { this.russian = russian; }
        public void setImage(ImageSet image) { this.image = image; }
        public void setUrl(String url) { this.url = url; }
    }

    public static class RoleBased<T> {
        private String role;
        private List<String> roles;
        private T data;

        public String getRole() { return role; }
        public List<String> getRoles() { return roles; }
        public T getData() { return data; }
        public void setRole(String role) { this.role = role; }
        public void setRoles(List<String> roles) { this.roles = roles; }
        public void setData(T data) { this.data = data; }
    }

    public static class Person {
        private int id;
        private String name;
        private String russian;
        private ImageSet image;
        private String url;
        private String japanese;
        @SerializedName("job_title") private String jobTitle;
        @SerializedName("birth_on") private PersonVitalDay birthOn;
        @SerializedName("deceased_on") private PersonVitalDay deceasedOn;
        private String website;
        @SerializedName("groupped_roles") private List<PersonGroupedRole> groupedRoles;
        private PersonRole roles;
        private List<PersonWork> works;
        @SerializedName("topic_id") private Integer topicId;
        @SerializedName("person_favoured") private boolean personFavoured;
        private boolean producer;
        @SerializedName("producer_favoured") private boolean producerFavoured;
        private boolean mangaka;
        @SerializedName("mangaka_favoured") private boolean mangakaFavoured;
        private boolean seyu;
        @SerializedName("seyu_favoured") private boolean seyuFavoured;
        @SerializedName("updated_at") private String updatedAt;
        @SerializedName("thread_id") private Integer threadId;
        private PersonVitalDay birthday;

        public int getId() { return id; }
        public String getName() { return name; }
        public String getRussian() { return russian; }
        public ImageSet getImage() { return image; }
        public String getUrl() { return url; }
        public String getJapanese() { return japanese; }
        public String getJobTitle() { return jobTitle; }
        public PersonVitalDay getBirthOn() { return birthOn; }
        public PersonVitalDay getDeceasedOn() { return deceasedOn; }
        public String getWebsite() { return website; }
        public List<PersonGroupedRole> getGroupedRoles() { return groupedRoles; }
        public PersonRole getRoles() { return roles; }
        public List<PersonWork> getWorks() { return works; }
        public Integer getTopicId() { return topicId; }
        public boolean isPersonFavoured() { return personFavoured; }
        public boolean isProducer() { return producer; }
        public boolean isProducerFavoured() { return producerFavoured; }
        public boolean isMangaka() { return mangaka; }
        public boolean isMangakaFavoured() { return mangakaFavoured; }
        public boolean isSeyu() { return seyu; }
        public boolean isSeyuFavoured() { return seyuFavoured; }
        public String getUpdatedAt() { return updatedAt; }
        public Integer getThreadId() { return threadId; }
        public PersonVitalDay getBirthday() { return birthday; }
        public void setId(int id) { this.id = id; }
        public void setName(String name) { this.name = name; }
        public void setRussian(String russian) { this.russian = russian; }
        public void setImage(ImageSet image) { this.image = image; }
        public void setUrl(String url) { this.url = url; }
        public void setJapanese(String japanese) { this.japanese = japanese; }
        public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
        public void setBirthOn(PersonVitalDay birthOn) { this.birthOn = birthOn; }
        public void setDeceasedOn(PersonVitalDay deceasedOn) { this.deceasedOn = deceasedOn; }
        public void setWebsite(String website) { this.website = website; }
        public void setGroupedRoles(List<PersonGroupedRole> groupedRoles) { this.groupedRoles = groupedRoles; }
        public void setRoles(PersonRole roles) { this.roles = roles; }
        public void setWorks(List<PersonWork> works) { this.works = works; }
        public void setTopicId(Integer topicId) { this.topicId = topicId; }
        public void setPersonFavoured(boolean personFavoured) { this.personFavoured = personFavoured; }
        public void setProducer(boolean producer) { this.producer = producer; }
        public void setProducerFavoured(boolean producerFavoured) { this.producerFavoured = producerFavoured; }
        public void setMangaka(boolean mangaka) { this.mangaka = mangaka; }
        public void setMangakaFavoured(boolean mangakaFavoured) { this.mangakaFavoured = mangakaFavoured; }
        public void setSeyu(boolean seyu) { this.seyu = seyu; }
        public void setSeyuFavoured(boolean seyuFavoured) { this.seyuFavoured = seyuFavoured; }
        public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
        public void setThreadId(Integer threadId) { this.threadId = threadId; }
        public void setBirthday(PersonVitalDay birthday) { this.birthday = birthday; }
    }

    public static class PersonBasic {
        private int id;
        private String name;
        private String russian;
        private ImageSet image;
        private String url;

        public int getId() { return id; }
        public String getName() { return name; }
        public String getRussian() { return russian; }
        public ImageSet getImage() { return image; }
        public String getUrl() { return url; }
        public void setId(int id) { this.id = id; }
        public void setName(String name) { this.name = name; }
        public void setRussian(String russian) { this.russian = russian; }
        public void setImage(ImageSet image) { this.image = image; }
        public void setUrl(String url) { this.url = url; }
    }

    public static class PersonVitalDay {
        private int day;
        private int month;
        private int year;
        public int getDay() { return day; }
        public int getMonth() { return month; }
        public int getYear() { return year; }
        public void setDay(int day) { this.day = day; }
        public void setMonth(int month) { this.month = month; }
        public void setYear(int year) { this.year = year; }
    }

    public static class PersonGroupedRole {
        private List<String> roles;
        public List<String> getRoles() { return roles; }
        public void setRoles(List<String> roles) { this.roles = roles; }
    }

    public static class PersonRole {
        private List<CharacterBasic> characters;
        private List<AnimeBasic> anime;
        public List<CharacterBasic> getCharacters() { return characters; }
        public List<AnimeBasic> getAnime() { return anime; }
        public void setCharacters(List<CharacterBasic> characters) { this.characters = characters; }
        public void setAnime(List<AnimeBasic> anime) { this.anime = anime; }
    }

    public static class PersonWork {
        private AnimeBasic anime;
        private MangaBasic manga;
        private String role;
        public AnimeBasic getAnime() { return anime; }
        public MangaBasic getManga() { return manga; }
        public String getRole() { return role; }
        public void setAnime(AnimeBasic anime) { this.anime = anime; }
        public void setManga(MangaBasic manga) { this.manga = manga; }
        public void setRole(String role) { this.role = role; }
    }

    public static class Review {
        private int id;
        @SerializedName("user_id") private int userId;
        @SerializedName("anime_id") private Integer animeId;
        @SerializedName("manga_id") private Integer mangaId;
        private String body;
        private String opinion;
        @SerializedName("is_written_before_release") private boolean isWrittenBeforeRelease;
        @SerializedName("created_at") private String createdAt;
        @SerializedName("updated_at") private String updatedAt;
        @SerializedName("comments_count") private int commentsCount;
        @SerializedName("cached_votes_up") private int cachedVotesUp;
        @SerializedName("cached_votes_down") private int cachedVotesDown;
        @SerializedName("changed_at") private String changedAt;

        public int getId() { return id; }
        public int getUserId() { return userId; }
        public Integer getAnimeId() { return animeId; }
        public Integer getMangaId() { return mangaId; }
        public String getBody() { return body; }
        public String getOpinion() { return opinion; }
        public boolean isWrittenBeforeRelease() { return isWrittenBeforeRelease; }
        public String getCreatedAt() { return createdAt; }
        public String getUpdatedAt() { return updatedAt; }
        public int getCommentsCount() { return commentsCount; }
        public int getCachedVotesUp() { return cachedVotesUp; }
        public int getCachedVotesDown() { return cachedVotesDown; }
        public String getChangedAt() { return changedAt; }
        public void setId(int id) { this.id = id; }
        public void setUserId(int userId) { this.userId = userId; }
        public void setAnimeId(Integer animeId) { this.animeId = animeId; }
        public void setMangaId(Integer mangaId) { this.mangaId = mangaId; }
        public void setBody(String body) { this.body = body; }
        public void setOpinion(String opinion) { this.opinion = opinion; }
        public void setWrittenBeforeRelease(boolean writtenBeforeRelease) { isWrittenBeforeRelease = writtenBeforeRelease; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
        public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
        public void setCommentsCount(int commentsCount) { this.commentsCount = commentsCount; }
        public void setCachedVotesUp(int cachedVotesUp) { this.cachedVotesUp = cachedVotesUp; }
        public void setCachedVotesDown(int cachedVotesDown) { this.cachedVotesDown = cachedVotesDown; }
        public void setChangedAt(String changedAt) { this.changedAt = changedAt; }
    }

    public static class Forum {
        private int id;
        private int position;
        private String name;
        private String permalink;
        private String url;

        public int getId() { return id; }
        public int getPosition() { return position; }
        public String getName() { return name; }
        public String getPermalink() { return permalink; }
        public String getUrl() { return url; }
        public void setId(int id) { this.id = id; }
        public void setPosition(int position) { this.position = position; }
        public void setName(String name) { this.name = name; }
        public void setPermalink(String permalink) { this.permalink = permalink; }
        public void setUrl(String url) { this.url = url; }
    }

    public static class Franchise {
        private List<FranchiseLink> links;
        private List<FranchiseNode> nodes;
        @SerializedName("current_id") private int currentId;

        public List<FranchiseLink> getLinks() { return links; }
        public List<FranchiseNode> getNodes() { return nodes; }
        public int getCurrentId() { return currentId; }
        public void setLinks(List<FranchiseLink> links) { this.links = links; }
        public void setNodes(List<FranchiseNode> nodes) { this.nodes = nodes; }
        public void setCurrentId(int currentId) { this.currentId = currentId; }
    }

    public static class FranchiseLink {
        private int id;
        private int sourceId;
        private int targetId;
        private int source;
        private int target;
        private int weight;
        private String relation;

        public int getId() { return id; }
        public int getSourceId() { return sourceId; }
        public int getTargetId() { return targetId; }
        public int getSource() { return source; }
        public int getTarget() { return target; }
        public int getWeight() { return weight; }
        public String getRelation() { return relation; }
        public void setId(int id) { this.id = id; }
        public void setSourceId(int sourceId) { this.sourceId = sourceId; }
        public void setTargetId(int targetId) { this.targetId = targetId; }
        public void setSource(int source) { this.source = source; }
        public void setTarget(int target) { this.target = target; }
        public void setWeight(int weight) { this.weight = weight; }
        public void setRelation(String relation) { this.relation = relation; }
    }

    public static class FranchiseNode {
        private int id;
        private int date;
        private String name;
        private String imageUrl;
        private String url;
        private Integer year;
        private String kind;
        private int weight;

        public int getId() { return id; }
        public int getDate() { return date; }
        public String getName() { return name; }
        public String getImageUrl() { return imageUrl; }
        public String getUrl() { return url; }
        public Integer getYear() { return year; }
        public String getKind() { return kind; }
        public int getWeight() { return weight; }
        public void setId(int id) { this.id = id; }
        public void setDate(int date) { this.date = date; }
        public void setName(String name) { this.name = name; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public void setUrl(String url) { this.url = url; }
        public void setYear(Integer year) { this.year = year; }
        public void setKind(String kind) { this.kind = kind; }
        public void setWeight(int weight) { this.weight = weight; }
    }

    public static class ExternalLink {
        private Integer id;
        private String kind;
        private String url;
        private String source;
        @SerializedName("entry_id") private int entryId;
        @SerializedName("entry_type") private String entryType;
        @SerializedName("created_at") private String createdAt;
        @SerializedName("updated_at") private String updatedAt;
        @SerializedName("imported_at") private String importedAt;

        public Integer getId() { return id; }
        public String getKind() { return kind; }
        public String getUrl() { return url; }
        public String getSource() { return source; }
        public int getEntryId() { return entryId; }
        public String getEntryType() { return entryType; }
        public String getCreatedAt() { return createdAt; }
        public String getUpdatedAt() { return updatedAt; }
        public String getImportedAt() { return importedAt; }
        public void setId(Integer id) { this.id = id; }
        public void setKind(String kind) { this.kind = kind; }
        public void setUrl(String url) { this.url = url; }
        public void setSource(String source) { this.source = source; }
        public void setEntryId(int entryId) { this.entryId = entryId; }
        public void setEntryType(String entryType) { this.entryType = entryType; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
        public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
        public void setImportedAt(String importedAt) { this.importedAt = importedAt; }
    }

    public static class Episode {
        @SerializedName("next_episode") private int nextEpisode;
        @SerializedName("next_episode_at") private String nextEpisodeAt;
        private Integer duration;
        private AnimeBasic anime;

        public int getNextEpisode() { return nextEpisode; }
        public String getNextEpisodeAt() { return nextEpisodeAt; }
        public Integer getDuration() { return duration; }
        public AnimeBasic getAnime() { return anime; }
        public void setNextEpisode(int nextEpisode) { this.nextEpisode = nextEpisode; }
        public void setNextEpisodeAt(String nextEpisodeAt) { this.nextEpisodeAt = nextEpisodeAt; }
        public void setDuration(Integer duration) { this.duration = duration; }
        public void setAnime(AnimeBasic anime) { this.anime = anime; }
    }

    public static class EpisodeNotification {
        private int id;
        @SerializedName("anime_id") private int animeId;
        private int episode;
        @SerializedName("is_raw") private boolean isRaw;
        @SerializedName("is_subtitles") private boolean isSubtitles;
        @SerializedName("is_fandub") private boolean isFandub;
        @SerializedName("is_anime365") private boolean isAnime365;
        @SerializedName("topic_id") private int topicId;

        public int getId() { return id; }
        public int getAnimeId() { return animeId; }
        public int getEpisode() { return episode; }
        public boolean isRaw() { return isRaw; }
        public boolean isSubtitles() { return isSubtitles; }
        public boolean isFandub() { return isFandub; }
        public boolean isAnime365() { return isAnime365; }
        public int getTopicId() { return topicId; }
        public void setId(int id) { this.id = id; }
        public void setAnimeId(int animeId) { this.animeId = animeId; }
        public void setEpisode(int episode) { this.episode = episode; }
        public void setRaw(boolean raw) { isRaw = raw; }
        public void setSubtitles(boolean subtitles) { isSubtitles = subtitles; }
        public void setFandub(boolean fandub) { isFandub = fandub; }
        public void setAnime365(boolean anime365) { isAnime365 = anime365; }
        public void setTopicId(int topicId) { this.topicId = topicId; }
    }

    public static class EpisodeNotificationTemplate {
        @SerializedName("anime_id") private int animeId;
        private int episode;
        @SerializedName("aired_at") private String airedAt;
        @SerializedName("is_raw") private Boolean isRaw;
        @SerializedName("is_subtitles") private Boolean isSubtitles;
        @SerializedName("is_fandub") private Boolean isFandub;
        @SerializedName("is_anime365") private Boolean isAnime365;

        public int getAnimeId() { return animeId; }
        public int getEpisode() { return episode; }
        public String getAiredAt() { return airedAt; }
        public Boolean getIsRaw() { return isRaw; }
        public Boolean getIsSubtitles() { return isSubtitles; }
        public Boolean getIsFandub() { return isFandub; }
        public Boolean getIsAnime365() { return isAnime365; }
        public void setAnimeId(int animeId) { this.animeId = animeId; }
        public void setEpisode(int episode) { this.episode = episode; }
        public void setAiredAt(String airedAt) { this.airedAt = airedAt; }
        public void setIsRaw(Boolean isRaw) { this.isRaw = isRaw; }
        public void setIsSubtitles(Boolean isSubtitles) { this.isSubtitles = isSubtitles; }
        public void setIsFandub(Boolean isFandub) { this.isFandub = isFandub; }
        public void setIsAnime365(Boolean isAnime365) { this.isAnime365 = isAnime365; }
    }

    public static class Ban {
        private int id;
        @SerializedName("user_id") private int userId;
        private CommentBasic comment;
        @SerializedName("moderator_id") private int moderatorId;
        private String reason;
        @SerializedName("created_at") private String createdAt;
        @SerializedName("duration_minutes") private int durationMinutes;
        private UserBasic user;
        private UserBasic moderator;

        public int getId() { return id; }
        public int getUserId() { return userId; }
        public CommentBasic getComment() { return comment; }
        public int getModeratorId() { return moderatorId; }
        public String getReason() { return reason; }
        public String getCreatedAt() { return createdAt; }
        public int getDurationMinutes() { return durationMinutes; }
        public UserBasic getUser() { return user; }
        public UserBasic getModerator() { return moderator; }
        public void setId(int id) { this.id = id; }
        public void setUserId(int userId) { this.userId = userId; }
        public void setComment(CommentBasic comment) { this.comment = comment; }
        public void setModeratorId(int moderatorId) { this.moderatorId = moderatorId; }
        public void setReason(String reason) { this.reason = reason; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
        public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
        public void setUser(UserBasic user) { this.user = user; }
        public void setModerator(UserBasic moderator) { this.moderator = moderator; }
    }

    public static class Achievement {
        private int id;
        @SerializedName("neko_id") private String nekoId;
        private int level;
        private int progress;
        @SerializedName("user_id") private int userId;
        @SerializedName("created_at") private String createdAt;
        @SerializedName("updated_at") private String updatedAt;

        public int getId() { return id; }
        public String getNekoId() { return nekoId; }
        public int getLevel() { return level; }
        public int getProgress() { return progress; }
        public int getUserId() { return userId; }
        public String getCreatedAt() { return createdAt; }
        public String getUpdatedAt() { return updatedAt; }
        public void setId(int id) { this.id = id; }
        public void setNekoId(String nekoId) { this.nekoId = nekoId; }
        public void setLevel(int level) { this.level = level; }
        public void setProgress(int progress) { this.progress = progress; }
        public void setUserId(int userId) { this.userId = userId; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
        public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    }

    public static class Role {
        private List<String> roles;
        @SerializedName("roles_russian") private List<String> rolesRussian;
        private CharacterBasic character;
        private PersonBasic person;

        public List<String> getRoles() { return roles; }
        public List<String> getRolesRussian() { return rolesRussian; }
        public CharacterBasic getCharacter() { return character; }
        public PersonBasic getPerson() { return person; }
        public void setRoles(List<String> roles) { this.roles = roles; }
        public void setRolesRussian(List<String> rolesRussian) { this.rolesRussian = rolesRussian; }
        public void setCharacter(CharacterBasic character) { this.character = character; }
        public void setPerson(PersonBasic person) { this.person = person; }
    }

    public static class Style {
        private Integer id;
        @SerializedName("owner_id") private Integer ownerId;
        @SerializedName("owner_type") private String ownerType;
        private String name;
        private String css;
        @SerializedName("compiled_css") private String compiledCss;
        @SerializedName("created_at") private String createdAt;
        @SerializedName("updated_at") private String updatedAt;

        public Integer getId() { return id; }
        public Integer getOwnerId() { return ownerId; }
        public String getOwnerType() { return ownerType; }
        public String getName() { return name; }
        public String getCss() { return css; }
        public String getCompiledCss() { return compiledCss; }
        public String getCreatedAt() { return createdAt; }
        public String getUpdatedAt() { return updatedAt; }
        public void setId(Integer id) { this.id = id; }
        public void setOwnerId(Integer ownerId) { this.ownerId = ownerId; }
        public void setOwnerType(String ownerType) { this.ownerType = ownerType; }
        public void setName(String name) { this.name = name; }
        public void setCss(String css) { this.css = css; }
        public void setCompiledCss(String compiledCss) { this.compiledCss = compiledCss; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
        public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    }

    public static class AbuseRequest {
        private String kind;
        private boolean value;
        @SerializedName("affected_ids") private List<Integer> affectedIds;

        public String getKind() { return kind; }
        public boolean isValue() { return value; }
        public List<Integer> getAffectedIds() { return affectedIds; }
        public void setKind(String kind) { this.kind = kind; }
        public void setValue(boolean value) { this.value = value; }
        public void setAffectedIds(List<Integer> affectedIds) { this.affectedIds = affectedIds; }
    }

    public static class IgnoreNotice {
        @SerializedName("topic_id") private int topicId;
        @SerializedName("is_ignored") private boolean isIgnored;

        public int getTopicId() { return topicId; }
        public boolean isIgnored() { return isIgnored; }
        public void setTopicId(int topicId) { this.topicId = topicId; }
        public void setIgnored(boolean ignored) { isIgnored = ignored; }
    }

    public static class ContentConstants {
        private List<String> kind;
        private List<String> status;
        public List<String> getKind() { return kind; }
        public List<String> getStatus() { return status; }
        public void setKind(List<String> kind) { this.kind = kind; }
        public void setStatus(List<String> status) { this.status = status; }
    }

    public static class UserRateConstants {
        private List<String> status;
        public List<String> getStatus() { return status; }
        public void setStatus(List<String> status) { this.status = status; }
    }

    public static class ClubConstants {
        @SerializedName("join_policy") private List<String> joinPolicy;
        @SerializedName("comment_policy") private List<String> commentPolicy;
        @SerializedName("image_upload_policy") private List<String> imageUploadPolicy;

        public List<String> getJoinPolicy() { return joinPolicy; }
        public List<String> getCommentPolicy() { return commentPolicy; }
        public List<String> getImageUploadPolicy() { return imageUploadPolicy; }
        public void setJoinPolicy(List<String> joinPolicy) { this.joinPolicy = joinPolicy; }
        public void setCommentPolicy(List<String> commentPolicy) { this.commentPolicy = commentPolicy; }
        public void setImageUploadPolicy(List<String> imageUploadPolicy) { this.imageUploadPolicy = imageUploadPolicy; }
    }

    public static class Smiley {
        private String bbcode;
        private String path;
        public String getBbcode() { return bbcode; }
        public String getPath() { return path; }
        public void setBbcode(String bbcode) { this.bbcode = bbcode; }
        public void setPath(String path) { this.path = path; }
    }

    public static class GraphQLRequest {
        private String query;
        private Map<String, Object> variables;
        private String operationName;

        public String getQuery() { return query; }
        public Map<String, Object> getVariables() { return variables; }
        public String getOperationName() { return operationName; }
        public void setQuery(String query) { this.query = query; }
        public void setVariables(Map<String, Object> variables) { this.variables = variables; }
        public void setOperationName(String operationName) { this.operationName = operationName; }
    }

    public static class GraphQLResponse {
        private Map<String, Object> data;
        private List<GraphQLError> errors;

        public Map<String, Object> getData() { return data; }
        public List<GraphQLError> getErrors() { return errors; }
        public boolean hasErrors() { return errors != null && !errors.isEmpty(); }
        public void setData(Map<String, Object> data) { this.data = data; }
        public void setErrors(List<GraphQLError> errors) { this.errors = errors; }
    }

    public static class GraphQLError {
        private String message;
        private List<Object> locations;
        private Map<String, Object> extensions;

        public String getMessage() { return message; }
        public List<Object> getLocations() { return locations; }
        public Map<String, Object> getExtensions() { return extensions; }
        public void setMessage(String message) { this.message = message; }
        public void setLocations(List<Object> locations) { this.locations = locations; }
        public void setExtensions(Map<String, Object> extensions) { this.extensions = extensions; }
    }
}

class ShikimoriClient {
    private static final String BASE_URL = "https://shikimori.one";
    private static final String USER_AGENT = "ShikiApp-Java/1.0";

    private final OkHttpClient client;
    private final Gson gson;
    private final RateLimiter rateLimiter;
    private String accessToken;

    public ShikimoriClient() {
        this(null);
    }

    public ShikimoriClient(String accessToken) {
        this.accessToken = accessToken;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.rateLimiter = new RateLimiter();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        this.client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request.Builder builder = original.newBuilder()
                            .header("User-Agent", USER_AGENT);

                    if (accessToken != null && !accessToken.isEmpty()) {
                        builder.header("Authorization", "Bearer " + accessToken);
                    }

                    return chain.proceed(builder.build());
                })
                .addInterceptor(logging)
                .addInterceptor(chain -> {
                    try {
                        rateLimiter.waitIfNeeded();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new IOException("Rate limiter interrupted", e);
                    }
                    return chain.proceed(chain.request());
                })
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    @SuppressWarnings("unchecked")
    private <T> T executeRequest(String path, String method, Object body, TypeRef<T> typeRef) {
        try {
            String url = BASE_URL + path;
            Request.Builder builder = new Request.Builder().url(url);

            if ("GET".equalsIgnoreCase(method) && body instanceof Map) {
                HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
                Map<String, Object> params = (Map<String, Object>) body;
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    if (entry.getValue() != null) {
                        urlBuilder.addQueryParameter(entry.getKey(), entry.getValue().toString());
                    }
                }
                builder.url(urlBuilder.build());
            }

            switch (method.toUpperCase()) {
                case "GET":
                    builder.get();
                    break;
                case "POST":
                    builder.post(createRequestBody(body));
                    break;
                case "PATCH":
                    builder.patch(createRequestBody(body));
                    break;
                case "PUT":
                    builder.put(createRequestBody(body));
                    break;
                case "DELETE":
                    builder.delete();
                    break;
                default:
                    throw new ApiException("Unsupported method: " + method);
            }

            try (Response response = client.newCall(builder.build()).execute()) {
                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "";
                    throw new ApiException("HTTP " + response.code() + ": " + errorBody, response.code(), errorBody);
                }

                if (typeRef != null && response.body() != null) {
                    String json = response.body().string();
                    if (typeRef.isList()) {
                        Type listType = TypeToken.getParameterized(List.class, typeRef.getClazz()).getType();
                        return gson.fromJson(json, listType);
                    } else {
                        return gson.fromJson(json, (Class<T>) typeRef.getClazz());
                    }
                }
                return null;
            }
        } catch (IOException e) {
            throw new ApiException("Network error: " + e.getMessage(), e);
        }
    }

    private RequestBody createRequestBody(Object body) {
        if (body == null) {
            return RequestBody.create(null, new byte[0]);
        }
        String json = gson.toJson(body);
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
    }

    public <T> T get(String path, Map<String, Object> params, TypeRef<T> typeRef) {
        return executeRequest(path, "GET", params, typeRef);
    }

    public <T> T post(String path, Object body, TypeRef<T> typeRef) {
        return executeRequest(path, "POST", body, typeRef);
    }

    public <T> T patch(String path, Object body, TypeRef<T> typeRef) {
        return executeRequest(path, "PATCH", body, typeRef);
    }

    public <T> T put(String path, Object body, TypeRef<T> typeRef) {
        return executeRequest(path, "PUT", body, typeRef);
    }

    public void delete(String path) {
        executeRequest(path, "DELETE", null, null);
    }
}

abstract class TypeRef<T> {
    private final Type type;
    private final Class<?> clazz;
    private final boolean isList;

    @SuppressWarnings("unchecked")
    protected TypeRef() {
        Type superClass = getClass().getGenericSuperclass();
        this.type = ((java.lang.reflect.ParameterizedType) superClass).getActualTypeArguments()[0];
        this.isList = type.getTypeName().startsWith("java.util.List");

        if (isList) {
            this.clazz = (Class<?>) ((java.lang.reflect.ParameterizedType) type).getActualTypeArguments()[0];
        } else {
            this.clazz = (Class<?>) type;
        }
    }

    public Type getType() { return type; }
    public Class<?> getClazz() { return clazz; }
    public boolean isList() { return isList; }
}