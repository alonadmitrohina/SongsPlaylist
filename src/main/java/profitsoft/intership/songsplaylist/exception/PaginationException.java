package profitsoft.intership.songsplaylist.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class PaginationException extends RuntimeException {
    public PaginationException(String message) {
        super(message);
    }
}
