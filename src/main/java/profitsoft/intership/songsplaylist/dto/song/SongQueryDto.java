package profitsoft.intership.songsplaylist.dto.song;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SongQueryDto {
    private Long playlistId;
    private String artist;
    private Integer year;

    @Min(value = 1, message = "Page must be >= 1")
    private Integer page = 1;

    @Min(value = 1, message = "Size must be >= 1")
    private Integer size = 20;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (playlistId != null) {
            sb.append(playlistId).append("_");
        }
        if (artist != null) {
            sb.append(artist.replaceAll("\\s+", "-")).append("_");
        }
        if (year != null) {
            sb.append(year);
        }
        return sb.toString();
    }
}
