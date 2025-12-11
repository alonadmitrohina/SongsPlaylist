# SongsPlaylist — Spring Boot REST API Service
Сутність Song (основна) - відноситься як many-to-one до Сутності Playlist.

Сервіс зберігає в БД (Postgre) і надає доступ через REST API до даних Сутностей Song і Playlist,
підтримує валідацію, фільтрацію, пагінацію, імпорт JSON, генерацію excel-звітів та інтеграційні тести.

**Для запуску проєкту:**
1) У PostgreSQL виконайте:
`CREATE DATABASE songs_playlist;`
2) У `src/main/resources/application-local.yaml` замінити your_username та your_password на локальні:
3) Запустити SongsPlaylistApplication: liquibase-changelog створить структуру бази та стартові дані для таблиці playlists.
4) У `src/main/resources/json` зберігаються 10 json файлів для наповнення таблиці songs (за допомогою endpoint api/songs/update, що приймає список файлів).

Усі endpoints покрито інтеграційними тестами.

**Endpoints info:**
1) POST /api/songs та PUT /api/songs/{id} приймають raw у такому форматі:
```json
{
  "name": "Test song",
  "artist": "Test",
  "year": 2025,
  "genres": [
    "pop",
    "rock"
  ],
  "playlistId": 1
}
```

2) POST /api/songs/_list як параметри для фільтрації приймає playlistId, artist, year:
```json
{
  "playlistId": 2,
  ...,
  "page": 1,
  "size": 20
}
```

4) POST /api/songs/_report приймає лише параметри для фільтрації (playlistId, artist, year) та пропонує завантажити .xlsx файл:
```json
{
  "playlistId": 2,
  ...,
}
```

6) POST /api/playlists та PUT /api/playlists/{id} приймають raw у такому форматі:
```json
{
    "name": "Testlist",
    "description": "Test desc"
}
```
