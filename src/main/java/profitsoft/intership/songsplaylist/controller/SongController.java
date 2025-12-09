package profitsoft.intership.songsplaylist.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import profitsoft.intership.songsplaylist.dto.song.SongDetailsDto;
import profitsoft.intership.songsplaylist.dto.song.SongInfoDto;
import profitsoft.intership.songsplaylist.dto.song.SongQueryDto;
import profitsoft.intership.songsplaylist.dto.song.SongSaveDto;
import profitsoft.intership.songsplaylist.service.song.SongService;
import profitsoft.intership.songsplaylist.utils.response.JSONResponse;
import profitsoft.intership.songsplaylist.utils.response.PaginationResponse;
import profitsoft.intership.songsplaylist.utils.response.RestResponse;


import java.util.List;

@RestController
@RequestMapping("/api/songs")
@RequiredArgsConstructor
public class SongController {

    public final SongService songService;


    @GetMapping("/{id}")
    public SongDetailsDto getSong(@PathVariable Long id) {
        return songService.getSong(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RestResponse createSong(@Valid @RequestBody SongSaveDto songSaveDto) {
        Long id = songService.createSong(songSaveDto);
        return new RestResponse(String.valueOf(id));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public RestResponse updateSong(@PathVariable Long id, @Valid @RequestBody SongSaveDto songSaveDto) {
        songService.updateSong(id, songSaveDto);
        return new RestResponse("OK");
    }

    @DeleteMapping("/{id}")
    public RestResponse deleteSong(@PathVariable Long id) {
        songService.deleteSong(id);
        return new RestResponse("OK");
    }

    @PostMapping("/_list")
    public PaginationResponse<SongInfoDto> getSongList(@Valid @RequestBody SongQueryDto songQueryDto) {
        return  songService.getSongList(songQueryDto);
    }

    @PostMapping(
            value = "/_report",
            produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    )
    public void reportSongList(
            @Valid @RequestBody SongQueryDto songQueryDto,
            HttpServletResponse response
    ) {
        songService.generateReport(songQueryDto, response);
    }

    @PostMapping("/upload")
    public JSONResponse uploadSongs(@RequestParam("files") List<MultipartFile> files) {
        return songService.uploadSongs(files);
    }

}
