package profitsoft.intership.songsplaylist.utils.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Setter
@Jacksonized
@RequiredArgsConstructor
public class RestResponse {

    private final String result;

}
