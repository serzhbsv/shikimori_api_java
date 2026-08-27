# Shikimori API Java Client

[![GitHub release](https://img.shields.io/github/v/release/serzhbsv/shikimori_api_java)](https://github.com/serzhbsv/shikimori_api_java/releases)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java 11+](https://img.shields.io/badge/Java-11%2B-blue.svg)](https://adoptium.net/)
[![CI](https://github.com/serzhbsv/shikimori_api_java/actions/workflows/build.yml/badge.svg)](https://github.com/serzhbsv/shikimori_api_java/actions/workflows/build.yml)

Full-featured Java client for the [Shikimori API](https://shikimori.io/api/doc).

## Features

- ✅ **Complete REST API v1/v2** – all endpoints
- ✅ **GraphQL support** – modern query language
- ✅ **OAuth2 authentication** – with refresh token
- ✅ **Rate limiting** – 5 rps / 90 rpm (automatic)
- ✅ **60+ data models** – fully typed
- ✅ **Lightweight** – only OkHttp + Gson
- ✅ **Java 11+ compatible**

## Quick Start

### Gradle
```gradle
dependencies {
    implementation 'com.pyshiki:shkimori-api:1.0.0'
}
```

### Maven
```xml
<dependency>
    <groupId>com.pyshiki</groupId>
    <artifactId>shkimori-api</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Manual
Download JAR from [Releases](https://github.com/serzhbsv/shikimori_api_java/releases)

## Usage Examples

### Search Anime
```java
ShikimoriApi api = new ShikimoriApi();
List<Models.AnimeBasic> results = api.animes().list(Map.of(
    "search", "Наруто",
    "limit", 10
));

for (Models.AnimeBasic anime : results) {
    System.out.println(anime.getRussian() + " | Score: " + anime.getScore());
}
```

### Get Anime by ID
```java
Models.Anime anime = api.animes().byId(1);
System.out.println("Name: " + anime.getRussian());
System.out.println("Status: " + anime.getStatusRussian());
System.out.println("Genres: " + anime.getGenres());
```

### Get Ongoings
```java
List<Models.AnimeBasic> ongoings = api.animes().list(Map.of(
    "status", "ongoing",
    "limit", 20
));
```

### OAuth2 Authentication
```java
OAuth2Manager oauth = new OAuth2Manager("CLIENT_ID", "CLIENT_SECRET");

String authUrl = oauth.getAuthorizationUrl();
System.out.println("Open in browser: " + authUrl);

OAuth2Manager.OAuthToken token = oauth.exchangeCode("AUTH_CODE");
String accessToken = token.accessToken;

ShikimoriApi api = new ShikimoriApi(accessToken);
Models.User user = api.users().whoami();
```

### Refresh Token
```java
OAuth2Manager.OAuthToken newToken = oauth.refreshToken("REFRESH_TOKEN");
api.setAccessToken(newToken.accessToken);
```

### User Rates
```java
List<Models.UserRate> rates = api.userRates().list(Map.of(
    "user_id", 12345,
    "limit", 10
));

Models.UserRateTemplate rate = new Models.UserRateTemplate();
rate.setUserId(12345);
rate.setTargetId(1);
rate.setScore(8);
rate.setStatus("completed");

Models.UserRate created = api.userRates().create(rate);
Models.UserRate updated = api.userRates().increment(created.getId());
```

### GraphQL
```java
Models.GraphQLRequest request = new Models.GraphQLRequest();
request.setQuery("query { anime(id: 1) { name russian score } }");
Models.GraphQLResponse response = api.graphql().query(request);
```

## Endpoints Coverage

| Category | Endpoints |
|----------|-----------|
| Anime | list, byId, roles, similar, related, screenshots, franchise, externalLinks, topics |
| Manga | list, byId, roles, similar, related, externalLinks, topics |
| Ranobe | list, byId, roles, similar, related, externalLinks, topics |
| Users | whoami, byId, byNickname, animeRates, mangaRates, favorites, history, unreadMessages, ignore, unignore |
| UserRates | list, byId, create, update, delete, increment |
| Topics | list, byId, create, update, delete, ignore, unignore |
| Comments | list, byId, create, update, delete |
| Characters | byId, search |
| People | byId, search |
| Reviews | list, byId |
| Genres | list |
| Studios | list |
| Calendar | list |
| Abuse | offtopic, review, abuse, spoiler |
| Friends | add, remove |
| Favorites | add, remove, reorder |
| Bans | list |
| Forums | list |
| Achievements | list |
| EpisodeNotifications | create |
| GraphQL | query |
| Appears | markAsRead |

## Building from Source

```bash
git clone https://github.com/serzhbsv/shikimori_api_java.git
cd shikimori_api_java
./gradlew build
./gradlew fatJar
./gradlew run
```

## Requirements

- Java 11 or higher
- Gradle 6.7+ (wrapper included)

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

MIT License. See [LICENSE](LICENSE) for details.