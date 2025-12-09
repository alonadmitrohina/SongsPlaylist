package profitsoft.intership.songsplaylist.service.song;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import profitsoft.intership.songsplaylist.dto.song.SongDetailsDto;
import profitsoft.intership.songsplaylist.dto.song.SongInfoDto;
import profitsoft.intership.songsplaylist.dto.song.SongQueryDto;
import profitsoft.intership.songsplaylist.dto.song.SongSaveDto;
import profitsoft.intership.songsplaylist.utils.response.JSONResponse;
import profitsoft.intership.songsplaylist.utils.response.PaginationResponse;

import java.util.List;

public interface SongService {

    Long createSong(SongSaveDto songSaveDto);

    SongDetailsDto getSong(Long id);

    void updateSong(Long id, SongSaveDto songSaveDto);

    void deleteSong(Long id);

    PaginationResponse<SongInfoDto> getSongList(SongQueryDto songQueryDto);

    void generateReport(SongQueryDto songQueryDto, HttpServletResponse httpServletResponse);

    JSONResponse uploadSongs(List<MultipartFile> files);

}