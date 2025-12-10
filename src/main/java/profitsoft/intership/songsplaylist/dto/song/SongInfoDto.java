package profitsoft.intership.songsplaylist.dto.song;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import profitsoft.intership.songsplaylist.dto.playlist.PlaylistDetailsDto;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SongInfoDto {

    private Long id;

    private String name;

    private String artist;

    private Integer year;

    private PlaylistDetailsDto playlist;

}
