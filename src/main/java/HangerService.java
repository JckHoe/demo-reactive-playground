import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

// Mutiny Version
@Slf4j
public class HangerService {
    private final List<Plane> planes = new ArrayList<>();
    private final int limit;

    public HangerService(int size, int limit) {
        this.limit = limit;
        for (int i = 1; i <= size; i++) {
            planes.add(new Plane().setName("Aircraft "+i).setLength(i));
        }
    }

    private HangerResponse retrieveUsers(Integer cursor) {
        List<Plane> planesToReturn = new ArrayList<>();

        if (cursor == null) {
            cursor = planes.size();
        }

        for (int limit = this.limit; cursor > 0 && limit > 0; cursor--, limit--) {
            planesToReturn.add(planes.get(cursor - 1));
        }

        return new HangerResponse()
                .setPlanes(planesToReturn)
                .setCursor(cursor);
    }

    public Uni<HangerResponse> getUsers() {
        return Uni.createFrom().item(retrieveUsers(null))
                .onItem()
                .delayIt()
                .by(Duration.ofMillis(350))
                .invoke(resp -> log.info("HangerResponse = {}", resp));
    }

    public Uni<HangerResponse> getUserByCursor(Integer cursor) {
        return Uni.createFrom().item(retrieveUsers(cursor))
                .onItem()
                .delayIt()
                .by(Duration.ofMillis(350))
                .invoke(resp -> log.info("HangerResponse = {}", resp));
    }

}
