package profitsoft.intership.songsplaylist.utils.response;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Getter @Setter
@Jacksonized
@RequiredArgsConstructor
public class PaginationResponse<T>{
    private List<T> list;
    private int totalPages;
}
