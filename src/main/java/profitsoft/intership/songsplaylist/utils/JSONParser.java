package profitsoft.intership.songsplaylist.utils;

import lombok.Getter;
import lombok.Setter;
import profitsoft.intership.songsplaylist.dto.song.SongSaveDto;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.json.JsonFactory;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@Setter
public class JSONParser {

    List<SongSaveDto> songs = new CopyOnWriteArrayList<>();


    public List<SongSaveDto> getSongs(){
        return songs;
    }

    /**
     * Парсинг об'єктів з inputStream зі створенням localSongs для збору даних локально у потоці
     * @param inputStream
     * @throws IOException
     */
    public void parseJSON(InputStream inputStream) throws IOException {
        JsonFactory jsonFactory = new JsonFactory();

        try (JsonParser jsonParser = jsonFactory.createParser(inputStream)) {

            if(jsonParser.nextToken() != JsonToken.START_ARRAY){
                return;
            }

            List<SongSaveDto> localSongs = new ArrayList<>();
            while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                SongSaveDto song = extractSong(jsonParser);
                localSongs.add(song);
            }
            songs.addAll(localSongs);
        }
    }


    /**
     * Створення об'єкту Song з об'єкту в json
     * @param jsonParser
     * @return
     * @throws IOException
     */
    private SongSaveDto extractSong(JsonParser jsonParser) throws IOException {
        String name = null;
        String singer = null;
        List<String> genres = null;
        int releaseYear = 0;
        long playlistId = 0;

        JsonToken token;
        while ((token = jsonParser.nextToken()) != JsonToken.END_OBJECT && token != null) {
            String fieldName = jsonParser.currentName();

            switch (fieldName) {
                case "name":
                    jsonParser.nextToken();
                    name = jsonParser.getValueAsString();
                    break;

                case "artist":
                    jsonParser.nextToken();
                    singer = jsonParser.getValueAsString();
                    break;

                case "year":
                    jsonParser.nextToken();
                    releaseYear = jsonParser.getValueAsInt();
                    break;

                case "genres":
                    jsonParser.nextToken();
                    genres = Arrays.stream(jsonParser.getValueAsString().split(","))
                            .map(String::trim)
                            .toList();
                    break;

                case "playlistId":
                    jsonParser.nextToken();
                    playlistId = jsonParser.getValueAsInt();
                    break;
            }
        }

        if (name == null || singer == null || releaseYear == 0 || playlistId == 0) {
            throw new IOException();
        }
        return new SongSaveDto(name, singer, releaseYear, genres, playlistId);
    }

}

