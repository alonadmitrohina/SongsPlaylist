package profitsoft.intership.songsplaylist.service.playlist;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import profitsoft.intership.songsplaylist.data.PlaylistData;
import profitsoft.intership.songsplaylist.dto.playlist.PlaylistDetailsDto;
import profitsoft.intership.songsplaylist.dto.playlist.PlaylistSaveDto;
import profitsoft.intership.songsplaylist.exception.PlaylistNotFoundException;
import profitsoft.intership.songsplaylist.mapper.PlaylistMapper;
import profitsoft.intership.songsplaylist.repository.PlaylistRepository;

import java.util.List;

@RequiredArgsConstructor
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepository playlistRepository;

    @Override
    @Transactional
    public Long createPlaylist(PlaylistSaveDto playlistSaveDto) {
        validatePlaylist(playlistSaveDto);
        PlaylistData playlistData = PlaylistMapper.toData(playlistSaveDto);
        return playlistRepository.save(playlistData).getId();
    }

    @Override
    public List<PlaylistDetailsDto> getPlaylists() {
        List<PlaylistData> playlistDataList = playlistRepository.findAll();
        return playlistDataList.stream()
                .map(PlaylistMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void updatePlaylist(Long id, PlaylistSaveDto playlistSaveDto) {
        validatePlaylistForUpdate(id, playlistSaveDto);
        PlaylistData playlistData = findPlaylist(id);
        PlaylistMapper.updateData(playlistData, playlistSaveDto);
        playlistRepository.save(playlistData);
    }

    @Override
    @Transactional
    public void deletePlaylist(Long id) {
        playlistRepository.delete(findPlaylist(id));
    }

    /**
     * Перевірка наявності та унікальності імені
     * @param playlistSaveDto
     */
    private void validatePlaylist(PlaylistSaveDto playlistSaveDto){
        nameExists(playlistSaveDto);
        if(playlistRepository.findByName(playlistSaveDto.getName()) != null){
            throw new IllegalArgumentException("Playlist already exists");
        }
    }

    /**
     * Перевірка наявності, унікальності імені та збігу id для оновлення
     * @param id
     * @param playlistSaveDto
     */
    private void validatePlaylistForUpdate(Long id, PlaylistSaveDto playlistSaveDto){
        nameExists(playlistSaveDto);
        PlaylistData existing = playlistRepository.findByName(playlistSaveDto.getName());
        if(existing != null && !existing.getId().equals(id)){
            throw new IllegalArgumentException("Playlist already exists");
        }
    }

    /**
     * Перевірка наявності імені
     * @param playlistSaveDto
     */
    private void nameExists(PlaylistSaveDto playlistSaveDto){
        if(playlistSaveDto.getName() == null || playlistSaveDto.getName().isEmpty()){
            throw new IllegalArgumentException("Playlist name is required");
        }
    }

    /**
     * Пошук плейліста за id
     * @param id
     * @return
     */
    private PlaylistData findPlaylist(Long id) {
        return playlistRepository.findById(id)
                .orElseThrow(() -> new PlaylistNotFoundException(
                        "Playlist with id %d not found".formatted(id)
                ));
    }

}
