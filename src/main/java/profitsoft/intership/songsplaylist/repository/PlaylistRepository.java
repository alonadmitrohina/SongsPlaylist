package profitsoft.intership.songsplaylist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import profitsoft.intership.songsplaylist.data.PlaylistData;

import java.util.Optional;

public interface PlaylistRepository extends JpaRepository<PlaylistData, Long> {

    PlaylistData findByName(String name);

    @Query("SELECT p FROM Playlist p WHERE p.name = :name")
    Optional<PlaylistData> findExactByName(@Param("name") String name);
}
