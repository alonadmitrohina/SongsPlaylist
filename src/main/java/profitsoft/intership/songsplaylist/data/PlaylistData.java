package profitsoft.intership.songsplaylist.data;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "Playlist")
@Table(name = "playlists")
@Getter @Setter
@NoArgsConstructor
@ToString
public class PlaylistData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;
}
