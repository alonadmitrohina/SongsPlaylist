package profitsoft.intership.songsplaylist.service.playlist;



import profitsoft.intership.songsplaylist.dto.playlist.PlaylistDetailsDto;
import profitsoft.intership.songsplaylist.dto.playlist.PlaylistSaveDto;

import java.util.List;

public interface PlaylistService {

    Long createPlaylist(PlaylistSaveDto playlistSaveDto);

    List<PlaylistDetailsDto> getPlaylists();

    void updatePlaylist(Long id, PlaylistSaveDto playlistSaveDto);

    void deletePlaylist(Long id);
}
