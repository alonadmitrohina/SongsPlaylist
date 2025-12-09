package profitsoft.intership.songsplaylist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import profitsoft.intership.songsplaylist.data.SongData;


public interface SongRepository extends JpaRepository<SongData, Long>, JpaSpecificationExecutor<SongData> {

    SongData findByName(String name);
    SongData findByArtist(String artist);

}
