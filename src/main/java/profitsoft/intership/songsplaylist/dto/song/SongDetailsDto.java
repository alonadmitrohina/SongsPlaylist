package profitsoft.intership.songsplaylist.dto.song;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import profitsoft.intership.songsplaylist.dto.playlist.PlaylistDetailsDto;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SongDetailsDto {

    private Long id;

    private String name;

    private String artist;

    private int year;

    private List<String> genres;

    private PlaylistDetailsDto playlist;

}
