import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class BaseTest {
    public int random() {
        Random random = new Random();
        return random.nextInt(15) + 5;
    }

    public List<Plane> constructFixedSizeHanger(int size) {
        List<Plane> planes = new ArrayList<>();
        for (int i = size; i > 0; i--) {
            planes.add(new Plane().setName("Aircraft "+i).setLength(i));
        }
        return planes;
    }
}
