package profitsoft.intership.songlists.data;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Entity(name = "Song")
@Table(name = "songs")
@Getter @Setter
@NoArgsConstructor
@ToString
public class SongData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String artist;

    @Column(nullable = false)
    private int year;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> genres;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="playlist_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer"})
    private PlaylistData playlist;
}