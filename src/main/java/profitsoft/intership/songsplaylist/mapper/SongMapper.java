package profitsoft.intership.songsplaylist.mapper;


import profitsoft.intership.songsplaylist.data.SongData;
import profitsoft.intership.songsplaylist.dto.song.SongDetailsDto;
import profitsoft.intership.songsplaylist.dto.song.SongInfoDto;
import profitsoft.intership.songsplaylist.dto.song.SongSaveDto;

public class SongMapper {

    /**
     * Створення об'єкту song для бази даних за data transfer object
     * @param songSaveDto
     * @return
     */
    public static SongData toData(SongSaveDto songSaveDto) {
        SongData songData = new SongData();
        updateData(songData, songSaveDto);
        return songData;
    }

    /**
     * Створення data transfer object (details) за даними об'єкту song з бази даних
     * @param songData
     * @return
     */
    public static SongDetailsDto toDto(SongData songData) {
        return SongDetailsDto.builder()
                .id(songData.getId())
                .name(songData.getName())
                .artist(songData.getArtist())
                .year(songData.getYear())
                .genres(songData.getGenres())
                .playlist(PlaylistMapper.toDto(songData.getPlaylist()))
                .build();
    }

    /**
     * Ононвлення об'єкту song
     * @param songData
     * @param songSaveDto
     */
    public static void updateData(SongData songData, SongSaveDto songSaveDto) {
        songData.setName(songSaveDto.getName());
        songData.setArtist(songSaveDto.getArtist());
        songData.setYear(songSaveDto.getYear());
        songData.setGenres(songSaveDto.getGenres());
    }

    /**
     * творення data transfer object (info) за даними об'єкту song з бази даних
     * @param songData
     * @return
     */
    public static SongInfoDto toInfoDto(SongData songData){
        return SongInfoDto.builder()
                .id(songData.getId())
                .name(songData.getName())
                .artist(songData.getArtist())
                .year(songData.getYear())
                .playlist(PlaylistMapper.toDto(songData.getPlaylist()))
                .build();
    }

}
