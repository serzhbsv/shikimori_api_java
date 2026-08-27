# Shikimori API Java клиент

[![GitHub release](https://img.shields.io/github/v/release/yourusername/shkimori-api)](https://github.com/yourusername/shkimori-api/releases)
[![Лицензия: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java 11+](https://img.shields.io/badge/Java-11%2B-blue.svg)](https://adoptium.net/)

Полнофункциональный Java-клиент для [Shikimori API](https://shikimori.io/api/doc).

## Возможности

- ✅ **Полная поддержка REST API v1/v2** – все эндпоинты
- ✅ **GraphQL** – современный язык запросов
- ✅ **OAuth2 авторизация** – с обновлением токена
- ✅ **Ограничение запросов** – 5 rps / 90 rpm (автоматически)
- ✅ **60+ моделей данных** – полная типизация
- ✅ **Лёгкий** – только OkHttp и Gson
- ✅ **Совместимость с Java 11+**

## Быстрый старт

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

### Вручную
Скачайте JAR из [Releases](https://github.com/yourusername/shkimori-api/releases)

## Примеры использования

### Поиск аниме
```java
ShikimoriApi api = new ShikimoriApi();
List<Models.AnimeBasic> results = api.animes().list(Map.of(
    "search", "Наруто",
    "limit", 10
));

for (Models.AnimeBasic anime : results) {
    System.out.println(anime.getRussian() + " | Оценка: " + anime.getScore());
}
```

### Получить аниме по ID
```java
Models.Anime anime = api.animes().byId(1);
System.out.println("Название: " + anime.getRussian());
System.out.println("Статус: " + anime.getStatusRussian());
System.out.println("Жанры: " + anime.getGenres());
```

### Получить онгоинги
```java
List<Models.AnimeBasic> ongoings = api.animes().list(Map.of(
    "status", "ongoing",
    "limit", 20
));
```

### OAuth2 авторизация
```java
OAuth2Manager oauth = new OAuth2Manager("CLIENT_ID", "CLIENT_SECRET");

String authUrl = oauth.getAuthorizationUrl();
System.out.println("Откройте в браузере: " + authUrl);

OAuth2Manager.OAuthToken token = oauth.exchangeCode("AUTH_CODE");
String accessToken = token.accessToken;

ShikimoriApi api = new ShikimoriApi(accessToken);
Models.User user = api.users().whoami();
```

### Обновление токена
```java
OAuth2Manager.OAuthToken newToken = oauth.refreshToken("REFRESH_TOKEN");
api.setAccessToken(newToken.accessToken);
```

### Оценки пользователя
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

## Доступные эндпоинты

| Категория | Эндпоинты |
|-----------|-----------|
| Аниме | list, byId, roles, similar, related, screenshots, franchise, externalLinks, topics |
| Манга | list, byId, roles, similar, related, externalLinks, topics |
| Ранобэ | list, byId, roles, similar, related, externalLinks, topics |
| Пользователи | whoami, byId, byNickname, animeRates, mangaRates, favorites, history, unreadMessages, ignore, unignore |
| Оценки | list, byId, create, update, delete, increment |
| Топики | list, byId, create, update, delete, ignore, unignore |
| Комментарии | list, byId, create, update, delete |
| Персонажи | byId, search |
| Люди | byId, search |
| Рецензии | list, byId |
| Жанры | list |
| Студии | list |
| Календарь | list |
| Жалобы | offtopic, review, abuse, spoiler |
| Друзья | add, remove |
| Избранное | add, remove, reorder |
| Баны | list |
| Форумы | list |
| Достижения | list |
| Уведомления о сериях | create |
| GraphQL | query |
| Appears | markAsRead |

## Сборка из исходников

```bash
git clone https://github.com/yourusername/shkimori-api.git
cd shkimori-api
./gradlew build
./gradlew fatJar
./gradlew run
```

## Требования

- Java 11 или выше
- Gradle 6.7+ (обёртка уже включена)

## Вклад в проект

Приветствуются любые contributions! Смело открывайте Pull Request.

## Лицензия

MIT License. Подробнее в файле [LICENSE](LICENSE).