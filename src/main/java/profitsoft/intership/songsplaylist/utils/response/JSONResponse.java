package profitsoft.intership.songsplaylist.utils.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JSONResponse {
    private int successCount;
    private int failedCount;
    private List<String> failedMessages;
}
