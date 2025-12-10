package profitsoft.intership.songsplaylist.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import profitsoft.intership.songsplaylist.SongsPlaylistApplication;
import profitsoft.intership.songsplaylist.data.PlaylistData;
import profitsoft.intership.songsplaylist.data.SongData;
import profitsoft.intership.songsplaylist.dto.playlist.PlaylistSaveDto;
import profitsoft.intership.songsplaylist.dto.song.SongInfoDto;
import profitsoft.intership.songsplaylist.dto.song.SongQueryDto;
import profitsoft.intership.songsplaylist.dto.song.SongSaveDto;
import profitsoft.intership.songsplaylist.mapper.PlaylistMapper;
import profitsoft.intership.songsplaylist.mapper.SongMapper;
import profitsoft.intership.songsplaylist.repository.PlaylistRepository;
import profitsoft.intership.songsplaylist.repository.SongRepository;
import profitsoft.intership.songsplaylist.utils.response.JSONResponse;
import profitsoft.intership.songsplaylist.utils.response.PaginationResponse;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = SongsPlaylistApplication.class)
@AutoConfigureMockMvc
public class SongControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PlaylistRepository playlistRepository;

    private SongSaveDto testSong;
    PlaylistData testPlaylistData;

    @AfterEach
    public void afterEach() {
        songRepository.deleteAll();
        playlistRepository.deleteAll();
    }

    @BeforeEach
    public void beforeEach() {
        setTestPlaylist(0);
        setTestSong(testPlaylistData.getId(), 0);
    }

    @Test
    public void createSongTest() throws Exception {
        String json = objectMapper.writeValueAsString(testSong);
        MvcResult mvcResult = mvc.perform(post("/api/songs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isCreated())
                .andReturn();

        String strId = mvcResult.getResponse().getContentAsString();
        long songId = Long.parseLong(strId.replaceAll("\\D+", ""));
        assertThat(songId).isGreaterThanOrEqualTo(1);

        SongData songData = songRepository.findById(songId).orElse(null);
        checkSongData(songData);
    }

    @Test
    public void songCreateValidationTestName() throws Exception {
        songRepository.save(setSongData());

        SongSaveDto duplicateSong = new SongSaveDto();
        duplicateSong.setName(testSong.getName());
        duplicateSong.setArtist(testSong.getArtist());
        duplicateSong.setYear(2018);
        duplicateSong.setGenres(List.of("genre1"));
        duplicateSong.setPlaylistId(testPlaylistData.getId());

        String jsonDuplicate = objectMapper.writeValueAsString(duplicateSong);

        mvc.perform(post("/api/songs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDuplicate))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value("Song with name " + duplicateSong.getName() +
                        " and artist " + duplicateSong.getArtist() + " already exists"));

    }

    @Test
    public void songCreateValidationTestYear() throws Exception {

        SongSaveDto invalidYear = new SongSaveDto();
        invalidYear.setName("Test Song");
        invalidYear.setArtist("Test Artist");
        invalidYear.setYear(2028);
        invalidYear.setGenres(List.of("genre1"));
        invalidYear.setPlaylistId(1L);

        String jsonInvalidYear = objectMapper.writeValueAsString(invalidYear);

        mvc.perform(post("/api/songs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalidYear))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value("Year cannot be in the future"))
                .andReturn();

    }


    @Test
    public void getSongTest() throws Exception {
        SongData songData = setSongData();
        songRepository.save(songData);
        mvc.perform(get("/api/songs/" + songData.getId()))
                .andExpect(status().isOk());

        SongData received = songRepository.findById(songData.getId()).orElse(null);
        checkSongData(received);
    }

    @Test
    public void updateSongTest() throws Exception {
        SongData songData = setSongData();
        songRepository.save(songData);

        testSong.setName("Test Updated Song");

        mvc.perform(put("/api/songs/" + songData.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testSong)))
                .andExpect(status().isCreated());

        SongData updated = songRepository.findById(songData.getId()).orElse(null);

        assertThat(updated).isNotNull();
        assertThat(updated.getName()).isEqualTo("Test Updated Song");
    }

    @Test
    public void deleteSongTest() throws Exception {
        SongData songData = setSongData();
        songRepository.save(songData);

        mvc.perform(delete("/api/songs/" + songData.getId()))
                .andExpect(status().isOk());

        assertThat(songRepository.findById(songData.getId()).isEmpty()).isTrue();
    }


    @Test
    public void getSongListTest() throws Exception {
        listTestPreparation();

        SongQueryDto songQueryDto = new SongQueryDto();
        songQueryDto.setPage(1);
        songQueryDto.setSize(10);
        songQueryDto.setPlaylistId(testPlaylistData.getId());

        MvcResult res = mvc.perform(post("/api/songs/_list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(songQueryDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.list").isArray())
                .andExpect(jsonPath("$.totalPages").value(3))
                .andReturn();

        String strResponse = res.getResponse().getContentAsString();

        PaginationResponse<SongInfoDto> paginationResponse = objectMapper.readValue(strResponse,
                new TypeReference<PaginationResponse<SongInfoDto>>() {});

        assertThat(paginationResponse.getList().size()).isEqualTo(10);
        for (SongInfoDto songInfoDto : paginationResponse.getList()) {
            assertThat(songInfoDto.getPlaylist().getId()).isEqualTo(testPlaylistData.getId());
        }

    }

    @Test
    public void reportSongListTest() throws Exception {
        listTestPreparation();
        SongQueryDto songQueryDto = new SongQueryDto();
        songQueryDto.setPlaylistId(testPlaylistData.getId());

        MvcResult result = mvc.perform(post("/api/songs/_report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                        .content(objectMapper.writeValueAsString(songQueryDto)))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        "Content-Type",
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                ))
                .andExpect(header().string(
                        "Content-Disposition",
                        Matchers.containsString("attachment")
                ))
                .andExpect(header().string(
                        "Content-Disposition",
                        Matchers.containsString(songQueryDto.toString())
                ))
                .andReturn();

        byte[] fileBytes = result.getResponse().getContentAsByteArray();

        assertThat(fileBytes.length).isNotZero();

        try (ByteArrayInputStream bais = new ByteArrayInputStream(fileBytes);
             Workbook workbook =WorkbookFactory.create(bais)) {

            Sheet sheet = workbook.getSheetAt(0);
            Row header = sheet.getRow(0);
            assertThat(header).isNotNull();
            assertThat(header.getCell(0).getStringCellValue()).isEqualTo("ID");
            assertThat(header.getCell(1).getStringCellValue()).isEqualTo("Name");
            assertThat(header.getCell(2).getStringCellValue()).isEqualTo("Artist");
            assertThat(header.getCell(3).getStringCellValue()).isEqualTo("Year");

            int rows = sheet.getPhysicalNumberOfRows() - 1;
            assertThat(rows).isEqualTo(30);
        }
    }

    @Test
    public void uploadSongsTest() throws Exception {
        List<Map<String, Object>> items = JSONItems();

        String jsonFileContent = objectMapper.writeValueAsString(items);

        MockMultipartFile file = new MockMultipartFile(
                "files",
                "json/testSongs.json",
                "application/json",
                jsonFileContent.getBytes()
        );

        MvcResult res = mvc.perform(
                        multipart("/api/songs/upload")
                                .file(file)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.successCount").exists())
                .andExpect(jsonPath("$.failedCount").exists())
                .andReturn();

        JSONResponse jsonResponse = objectMapper.readValue(res.getResponse().getContentAsString(), JSONResponse.class);
        assertThat(jsonResponse.getSuccessCount()).isEqualTo(10);
        assertThat(jsonResponse.getFailedCount()).isEqualTo(4);
        assertThat(jsonResponse.getFailedMessages().size()).isEqualTo(4);
    }

    private List<Map<String, Object>> JSONItems(){
        return List.of(
                // valid
                Map.of("name", "Song 01", "artist", "Artist A", "year", 2020, "genres", "pop, dance", "playlistId", testPlaylistData.getId()),
                Map.of("name", "Song 02", "artist", "Artist B", "year", 2021, "genres", "rock", "playlistId", testPlaylistData.getId()),
                Map.of("name", "Song 03", "artist", "Artist C", "year", 2019, "genres", "jazz", "playlistId", testPlaylistData.getId()),
                Map.of("name", "Song 04", "artist", "Artist D", "year", 2022, "genres", "pop", "playlistId", testPlaylistData.getId()),
                Map.of("name", "Song 05", "artist", "Artist E", "year", 2023, "genres", "electronic", "playlistId", testPlaylistData.getId()),
                Map.of("name", "Song 06", "artist", "Artist F", "year", 2020, "genres", "indie", "playlistId", testPlaylistData.getId()),
                Map.of("name", "Song 07", "artist", "Artist G", "year", 2018, "genres", "rap", "playlistId", testPlaylistData.getId()),
                Map.of("name", "Song 08", "artist", "Artist H", "year", 2017, "genres", "pop, disco", "playlistId", testPlaylistData.getId()),
                Map.of("name", "Song 09", "artist", "Artist I", "year", 2024, "genres", "synthwave", "playlistId", testPlaylistData.getId()),
                Map.of("name", "Song 10", "artist", "Artist J", "year", 2024, "genres", "pop", "playlistId", testPlaylistData.getId()),

                // invalid
                Map.of("name", "Bad Song 01", "artist", "Err A", "year", 2023, "genres", "pop", "playlistId", 999),
                Map.of("name", "Bad Song 02", "artist", "Err B", "year", 2022, "genres", "rock", "playlistId", 999),
                Map.of("name", "Bad Song 03", "artist", "Err C", "year", 2028, "genres", "jazz", "playlistId", testPlaylistData.getId()),
                Map.of("name", "Song 10", "artist", "Artist J", "year", 2021, "genres", "rap", "playlistId", testPlaylistData.getId())
        );

    }

    private void listTestPreparation(){
        SongData songData = setSongData();
        songRepository.save(songData);
        setTestingList(20);
        setTestPlaylist(1);
        setTestingList(30);
    }

    private void setTestingList(int songNumber){
        SongData songData;
        for (int i = 1; i <= songNumber; i++) {
            setTestSong(testPlaylistData.getId(), i);
            songData = setSongData();
            songRepository.save(songData);
        }
    }

    private void setTestPlaylist(int testingNumber){
        PlaylistSaveDto testPlaylist = new PlaylistSaveDto();
        testPlaylist.setName("Test Playlist " + testingNumber);
        testPlaylist.setDescription("Test Playlist Description  " + testingNumber);
        testPlaylistData = PlaylistMapper.toData(testPlaylist);
        playlistRepository.save(testPlaylistData);
    }


    private void setTestSong(Long playlistId, int testingNumber){
        testSong = new SongSaveDto();
        testSong.setName("Test Song " + testingNumber);
        testSong.setArtist("Test Artist " + testingNumber);
        testSong.setYear(1950 + testingNumber);
        testSong.setGenres(List.of("genre1", "genre2", "genre3"));
        testSong.setPlaylistId(playlistId);
    }

    private SongData setSongData() {
        SongData songData = SongMapper.toData(testSong);
        PlaylistData playlistData = playlistRepository.findById(testSong.getPlaylistId()).orElse(null);
        songData.setPlaylist(playlistData);
        return songData;
    }

    private void checkSongData(SongData songData) {
        assertThat(songData).isNotNull();
        assertThat(songData.getName()).isEqualTo(testSong.getName());
        assertThat(songData.getArtist()).isEqualTo(testSong.getArtist());
        assertThat(songData.getYear()).isEqualTo(testSong.getYear());
        assertThat(songData.getGenres()).isEqualTo(testSong.getGenres());
        assertThat(songData.getPlaylist().getId()).isEqualTo(testSong.getPlaylistId());
    }

}
