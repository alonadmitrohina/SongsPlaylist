package profitsoft.intership.songsplaylist.service.song;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import profitsoft.intership.songsplaylist.dao.SongDao;
import profitsoft.intership.songsplaylist.data.PlaylistData;
import profitsoft.intership.songsplaylist.data.SongData;
import profitsoft.intership.songsplaylist.dto.song.SongDetailsDto;
import profitsoft.intership.songsplaylist.dto.song.SongInfoDto;
import profitsoft.intership.songsplaylist.dto.song.SongQueryDto;
import profitsoft.intership.songsplaylist.dto.song.SongSaveDto;
import profitsoft.intership.songsplaylist.exception.PaginationException;
import profitsoft.intership.songsplaylist.exception.PlaylistNotFoundException;
import profitsoft.intership.songsplaylist.exception.SongNotFoundException;
import profitsoft.intership.songsplaylist.mapper.SongMapper;
import profitsoft.intership.songsplaylist.repository.PlaylistRepository;
import profitsoft.intership.songsplaylist.repository.SongRepository;
import profitsoft.intership.songsplaylist.utils.ExcelGenerator;
import profitsoft.intership.songsplaylist.utils.JSONParser;
import profitsoft.intership.songsplaylist.utils.response.JSONResponse;
import profitsoft.intership.songsplaylist.utils.response.PaginationResponse;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;
    private final PlaylistRepository playlistRepository;
    private final SongDao songDao;

    @Override
    @Transactional
    public Long createSong(SongSaveDto songSaveDto) {
        validateSong(songSaveDto);
        PlaylistData playlist = findPlaylist(songSaveDto.getPlaylistId());
        SongData songData = SongMapper.toData(songSaveDto);
        songData.setPlaylist(playlist);
        return songRepository.save(songData).getId();
    }

    @Override
    public SongDetailsDto getSong(Long id){
        SongData songData = findSong(id);
        return SongMapper.toDto(songData);
    }

    @Override
    @Transactional
    public void updateSong(Long id, SongSaveDto songSaveDto){
        validateSongForUpdate(songSaveDto);
        SongData songData = findSong(id);
        PlaylistData playlist = findPlaylist(songSaveDto.getPlaylistId());
        SongMapper.updateData(songData, songSaveDto);
        songData.setPlaylist(playlist);
        songRepository.save(songData);
    }

    @Override
    @Transactional
    public void deleteSong(Long id){
        songRepository.delete(findSong(id));
    }


    @Override
    @Transactional
    public PaginationResponse<SongInfoDto> getSongList(SongQueryDto songQueryDto) {
        if(songQueryDto.getYear() != null){
            validateSongQuery(songQueryDto);
        }
        validatePagination(songQueryDto);

        List<SongInfoDto> list = songDao.getSongList(songQueryDto, songQueryDto.getPage() - 1, songQueryDto.getSize());
        int totalPages = songDao.getTotalPages(songQueryDto);

        PaginationResponse<SongInfoDto> response = new PaginationResponse<>();
        response.setList(list);
        response.setTotalPages(totalPages);

        return response;
    }

    @Override
    @Transactional
    public void generateReport(SongQueryDto songQueryDto, HttpServletResponse httpServletResponse) {
        if(songQueryDto.getYear() != null){
            validateSongQuery(songQueryDto);
        }

        List<SongInfoDto> list = songDao.getSongList(songQueryDto);

        String filename = "songs_report_" + songQueryDto + ".xlsx";
        httpServletResponse.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=%s".formatted(filename));
        httpServletResponse.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        );
        try {
            byte[] excelBytes = ExcelGenerator.generateReport(list);
            httpServletResponse.getOutputStream().write(excelBytes);
            httpServletResponse.getOutputStream().flush();
        }
        catch (IOException e) {
            throw new RuntimeException("Error while generating report file: " + e.getMessage());
        }
    }

    @AllArgsConstructor
    class Counter{
        int successCount = 0;
        int failedCount = 0;

        int getTotal(){return successCount + failedCount;}
    }

    @Override
    public JSONResponse uploadSongs(List<MultipartFile> files) {
        Counter counter = new Counter(0, 0);
        List<String> failedMessages = new ArrayList<>();

        for(MultipartFile file : files){
            parseFile(file, counter, failedMessages);
        }

        return new JSONResponse(counter.successCount, counter.failedCount, failedMessages);
    }


    /**
     * Валідація пісні для створення
     * @param songSaveDto
     */
    private void validateSong(SongSaveDto songSaveDto) {
        int year = songSaveDto.getYear();
        validateYear(year);
        songExists(songSaveDto);
    }

    /**
     * Валідація пісні для оновлення
     * @param songSaveDto
     */
    private void validateSongForUpdate(SongSaveDto songSaveDto) {
        int year = songSaveDto.getYear();
        validateYear(year);
    }

    /**
     * Перевірка року випуску пісні як параметру
     * @param songQueryDto
     */
    private void validateSongQuery(SongQueryDto songQueryDto) {
        int year = songQueryDto.getYear();
        validateYear(year);
    }

    /**
     * Перевірка року випуску пісні
     * @param year
     */
    private void validateYear(int year) {
        if (year < 1500) {
            throw new IllegalArgumentException("Year must be later than 1500");
        } else if (year > LocalDate.now().getYear()) {
            throw new IllegalArgumentException("Year cannot be in the future");
        }
    }

    /**
     * Пошук пісні за id
     * @param id
     * @return
     * @throws SongNotFoundException
     */
    private SongData findSong(Long id){
        return songRepository.findById(id)
                .orElseThrow(() -> new SongNotFoundException(
                        "Song with id %d not found".formatted(id))
                );
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

    /**
     * Перевірка наявності параметрів для пагінації
     * @param songQueryDto
     */
    private void validatePagination(SongQueryDto songQueryDto) {
        if(songQueryDto.getPage() == null || songQueryDto.getSize() == null){
            throw new PaginationException("Page and size are required for pagination");
        }
    }

    /**
     * Перевірка існування пісні за назвою та виконавцем
     * @param songSaveDto
     */
    private void songExists(SongSaveDto songSaveDto) {
        if(songRepository.findByName(songSaveDto.getName()) != null
                && songRepository.findByArtist(songSaveDto.getArtist()) != null) {
            throw new IllegalArgumentException("Song with name " + songSaveDto.getName() + " and artist " + songSaveDto.getArtist() + " already exists");
        }
    }

    /**
     * Парсинг одного JSON файлу
     * @param file
     * @param counter
     * @param failedMessages
     */
    private void parseFile(MultipartFile file, Counter counter, List<String> failedMessages) {

        checkJSON(file);

        JSONParser jsonParser = new JSONParser();
        try (InputStream is = file.getInputStream()) {
            jsonParser.parseJSON(is);
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + e.getMessage(), e);
        }

        List<SongSaveDto> list = jsonParser.getSongs();
        for (SongSaveDto songSaveDto : list) {
            try {
                processSong(songSaveDto);
                counter.successCount++;
            } catch (Exception e) {
                counter.failedCount++;
                failedMessages.add("Record " + counter.getTotal() + " (" + songSaveDto.getName() + "): " + e.getMessage());
            }
        }
    }

    /**
     * Перевірка та збереження пісні з файлу
     * @param songSaveDto
     */
    @Transactional
    protected void processSong(SongSaveDto songSaveDto) {
        PlaylistData playlist = playlistRepository.findById(songSaveDto.getPlaylistId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Playlist with id " + songSaveDto.getPlaylistId() + " not found"
                ));
        validateSong(songSaveDto);
        SongData song = SongMapper.toData(songSaveDto);
        song.setPlaylist(playlist);
        songRepository.save(song);
    }

    /**
     * Перевірка чи є файл JSON
     * @param file
     */
    private void checkJSON(MultipartFile file){
        if (!file.getOriginalFilename().endsWith(".json")){
            throw new IllegalArgumentException("Invalid file format, only JSON allowed");
        }
    }

}

