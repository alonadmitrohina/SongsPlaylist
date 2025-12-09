package profitsoft.intership.songsplaylist.config;

import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import profitsoft.intership.songsplaylist.dao.SongDao;
import profitsoft.intership.songsplaylist.repository.PlaylistRepository;
import profitsoft.intership.songsplaylist.repository.SongRepository;
import profitsoft.intership.songsplaylist.service.playlist.PlaylistService;
import profitsoft.intership.songsplaylist.service.playlist.PlaylistServiceImpl;
import profitsoft.intership.songsplaylist.service.song.SongService;
import profitsoft.intership.songsplaylist.service.song.SongServiceImpl;

@Configuration
@EnableTransactionManagement
public class ApplicationConfig {

    @Bean
    public SongDao songDao(EntityManager entityManager) {
        return new SongDao(entityManager);
    }

    @Bean
    public PlaylistService playlistService(PlaylistRepository playlistRepository) {
        return new PlaylistServiceImpl(playlistRepository);
    }

    @Bean
    public SongService songService(SongRepository songRepository, PlaylistRepository playlistRepository, SongDao songDao) {
        return new SongServiceImpl(songRepository, playlistRepository,  songDao);
    }
}
