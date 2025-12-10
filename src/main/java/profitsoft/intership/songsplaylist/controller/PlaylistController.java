package profitsoft.intership.songsplaylist.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import profitsoft.intership.songsplaylist.dto.playlist.PlaylistDetailsDto;
import profitsoft.intership.songsplaylist.dto.playlist.PlaylistSaveDto;
import profitsoft.intership.songsplaylist.service.playlist.PlaylistService;
import profitsoft.intership.songsplaylist.utils.response.RestResponse;

import java.util.List;

@RestController
@RequestMapping("/api/playlists")
@RequiredArgsConstructor
public class PlaylistController {

    public final PlaylistService playlistService;

    @GetMapping
    public List<PlaylistDetailsDto> getPlayLists(){
        return playlistService.getPlaylists();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RestResponse createPlaylist(@Valid @RequestBody PlaylistSaveDto playlistSaveDto){
        Long id = playlistService.createPlaylist(playlistSaveDto);
        return new RestResponse(String.valueOf(id));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public RestResponse updatePlaylist(@PathVariable Long id, @Valid @RequestBody PlaylistSaveDto playlistSaveDto){
        playlistService.updatePlaylist(id, playlistSaveDto);
        return new RestResponse("OK");
    }

    @DeleteMapping("/{id}")
    public RestResponse deletePlaylist(@PathVariable Long id){
        playlistService.deletePlaylist(id);
        return new RestResponse("OK");
    }
}

