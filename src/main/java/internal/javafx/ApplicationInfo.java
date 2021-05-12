package internal.javafx;

import lombok.Builder;
import lombok.Data;

import javax.annotation.Nullable;
import java.net.URL;

@Data
@Builder
public final class ApplicationInfo {

    private final String title;
    private final URL fxml;
    @Nullable
    private final URL css;

}
