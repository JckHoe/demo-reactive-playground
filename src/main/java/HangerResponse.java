import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class HangerResponse {
    private List<Plane> planes;
    private Integer cursor;
}
