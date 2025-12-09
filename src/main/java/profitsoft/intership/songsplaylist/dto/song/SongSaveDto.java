package profitsoft.intership.songsplaylist.dto.song;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SongSaveDto {

    @NotNull(message = "Song name is mandatory")
    @NotBlank(message = "Song name is mandatory")
    private String name;

    @NotNull(message = "Artist is mandatory")
    @NotBlank(message = "Artist is mandatory")
    private String artist;

    @NotNull(message = "Year is mandatory")
    private Integer year;

    private List<String> genres;

    @NotNull(message = "Song must be in a playlist")
    private Long playlistId;

}
