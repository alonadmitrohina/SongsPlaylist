package profitsoft.intership.songsplaylist.dto.playlist;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistSaveDto {

    @NotNull(message = "Playlist name is mandatory")
    @NotBlank(message = "Playlist name is mandatory")
    private String name;

    private String description;
}
