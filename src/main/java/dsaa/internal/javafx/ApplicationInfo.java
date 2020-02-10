package dsaa.internal.javafx;

import java.net.URL;
import javax.annotation.Nullable;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public final class ApplicationInfo {
  private final String title;
  private final URL fxml;
  @Nullable
  private final URL css;
}
