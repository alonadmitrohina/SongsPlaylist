package profitsoft.intership.songsplaylist.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import profitsoft.intership.songsplaylist.SongsPlaylistApplication;
import profitsoft.intership.songsplaylist.data.PlaylistData;
import profitsoft.intership.songsplaylist.dto.playlist.PlaylistSaveDto;
import profitsoft.intership.songsplaylist.mapper.PlaylistMapper;
import profitsoft.intership.songsplaylist.repository.PlaylistRepository;

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
public class PlaylistControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PlaylistRepository playlistRepository;

    private PlaylistSaveDto testPlaylist;

    @AfterEach
    public void afterEach() {
        playlistRepository.deleteAll();
    }

    @BeforeEach
    public void beforeEach() {
        setTestPlaylist(0);
    }

    @Test
    public void createPlaylistTest() throws Exception {
        playlistRepository.save(PlaylistMapper.toData(testPlaylist));

        playlistRepository.deleteAll();
        MvcResult res = mvc.perform(post("/api/playlists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPlaylist)))
        .andExpect(status().isCreated())
                .andReturn();

        String strId = res.getResponse().getContentAsString(); // <- ТІЛО ВІДПОВІДІ
        long playlistId = Long.parseLong(strId.replaceAll("\\D+", ""));
        assertThat(playlistId).isGreaterThanOrEqualTo(1);

        PlaylistData playlistData = playlistRepository.findById(playlistId).orElse(null);
        checkPlaylistData(playlistData);
    }

    @Test
    public void validationTestBlankName() throws Exception {
        testPlaylist.setName("");
        playlistRepository.save(PlaylistMapper.toData(testPlaylist));

        mvc.perform(post("/api/playlists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPlaylist)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("name: Playlist name is mandatory"));
    }

    @Test
    public void validationTestDuplicate() throws Exception {
        playlistRepository.save(PlaylistMapper.toData(testPlaylist));

        PlaylistSaveDto duplicate = new PlaylistSaveDto();
        duplicate.setName(testPlaylist.getName());
        duplicate.setDescription("Some new description");

        mvc.perform(post("/api/playlists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value("Playlist already exists"));
    }

    @Test
    public void updatePlaylistTest() throws Exception {
        PlaylistData playlistData = PlaylistMapper.toData(testPlaylist);
        playlistRepository.save(playlistData);

        testPlaylist.setName("Test Update");
        mvc.perform(put("/api/playlists/" + playlistData.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPlaylist)))
                .andExpect(status().isCreated());

        PlaylistData updated = playlistRepository.findById(playlistData.getId()).orElse(null);
        assertThat(updated).isNotNull();
        assertThat(updated.getName()).isEqualTo("Test Update");
    }

    @Test
    public void validationUpdate() throws Exception {
        playlistRepository.save(PlaylistMapper.toData(testPlaylist));

        PlaylistSaveDto another = new PlaylistSaveDto();
        another.setName("Update v test");
        another.setDescription("Some new description");
        PlaylistData playlistData = playlistRepository.save(PlaylistMapper.toData(another));

        PlaylistSaveDto duplicateDto = new PlaylistSaveDto();
        duplicateDto.setName(testPlaylist.getName());
        duplicateDto.setDescription("Some new desc");

        mvc.perform(put("/api/playlists/" + playlistData.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value("Playlist already exists"));
    }

    @Test
    public void deletePlaylistTest() throws Exception {
        PlaylistData playlistData = PlaylistMapper.toData(testPlaylist);
        playlistRepository.save(playlistData);

        mvc.perform(delete("/api/playlists/" + playlistData.getId()))
                .andExpect(status().isOk());

        assertThat(playlistRepository.findById(playlistData.getId()).isEmpty()).isTrue();
    }

    @Test
    public void getPlaylistsTest() throws Exception {
        for(int i = 0; i < 10; i++) {
            setTestPlaylist(i);
            playlistRepository.save(PlaylistMapper.toData(testPlaylist));
        }

        MvcResult res = mvc.perform(get("/api/playlists"))
                .andExpect(status().isOk())
                .andReturn();

        String strResponse = res.getResponse().getContentAsString();
        List<Map<String, Object>> playlists = objectMapper.readValue(
                strResponse, new TypeReference<List<Map<String, Object>>>() {}
        );

        assertThat(playlists).isNotNull();
        assertThat(playlists.size()).isEqualTo(10);

        for (int i = 0; i < 10; i++) {
            Map<String, Object> first = playlists.get(i);
            assertThat(first.get("name")).isEqualTo("Test Playlist " + i);
            assertThat(first.get("description")).isEqualTo("Test Playlist Description " + i);
        }
    }

    private void checkPlaylistData(PlaylistData playlistData) {
        assertThat(playlistData).isNotNull();
        assertThat(playlistData.getName()).isEqualTo(testPlaylist.getName());
        assertThat(playlistData.getDescription()).isEqualTo(testPlaylist.getDescription());
    }

    private void setTestPlaylist(int testNumber){
        testPlaylist = new PlaylistSaveDto();
        testPlaylist.setName("Test Playlist " + testNumber);
        testPlaylist.setDescription("Test Playlist Description " + testNumber);
    }

}
