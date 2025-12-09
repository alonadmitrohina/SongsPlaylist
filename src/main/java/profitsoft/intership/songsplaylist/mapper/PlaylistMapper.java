package profitsoft.intership.songsplaylist.mapper;


import profitsoft.intership.songsplaylist.data.PlaylistData;
import profitsoft.intership.songsplaylist.dto.playlist.PlaylistDetailsDto;
import profitsoft.intership.songsplaylist.dto.playlist.PlaylistSaveDto;

public class PlaylistMapper {

    /**
     * Створення об'єкту playlist для бази даних за data transfer object
     * @param playlistSaveDto
     * @return
     */
    public static PlaylistData toData(PlaylistSaveDto playlistSaveDto) {
        PlaylistData playlistData = new PlaylistData();
        playlistData.setName(playlistSaveDto.getName());
        playlistData.setDescription(playlistSaveDto.getDescription());
        return playlistData;
    }

    /**
     * Створення data transfer object за даними об'єкту playlist з бази даних
     * @param playlistData
     * @return
     */
    public static PlaylistDetailsDto toDto(PlaylistData playlistData) {
        return PlaylistDetailsDto.builder()
                .id(playlistData.getId())
                .name(playlistData.getName())
                .description(playlistData.getDescription()).build();
    }

    /**
     * Ононвлення об'єкту playlist
     * @param playlistData
     * @param playlistSaveDto
     */
    public static void updateData(PlaylistData playlistData, PlaylistSaveDto playlistSaveDto) {
        playlistData.setName(playlistSaveDto.getName());
        playlistData.setDescription(playlistSaveDto.getDescription());
    }

}
