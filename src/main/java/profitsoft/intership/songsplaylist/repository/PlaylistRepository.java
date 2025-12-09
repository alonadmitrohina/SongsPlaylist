package profitsoft.intership.songsplaylist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import profitsoft.intership.songsplaylist.data.PlaylistData;

public interface PlaylistRepository extends JpaRepository<PlaylistData, Long> {

    PlaylistData findByName(String name);
}
