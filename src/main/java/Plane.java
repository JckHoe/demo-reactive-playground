import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Plane {
    private String name;
    private long length;
}
