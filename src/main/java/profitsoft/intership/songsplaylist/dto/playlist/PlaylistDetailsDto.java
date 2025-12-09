package profitsoft.intership.songsplaylist.dto.playlist;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistDetailsDto {

    private Long id;

    private String name;

    private String description;

}
