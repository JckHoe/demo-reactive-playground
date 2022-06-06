import io.reactivex.Single;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class HangerServiceV2 {
    private final List<Plane> planes = new ArrayList<>();
    private final int limit;

    public HangerServiceV2(int size, int limit) {
        this.limit = limit;
        for (int i = 1; i <= size; i++) {
            planes.add(new Plane().setName("Aircraft "+i).setLength(i));
        }
    }

    private HangerResponse retrievePlanes(Integer cursor) {
        List<Plane> toReturn = new ArrayList<>();

        if (cursor == null) {
            cursor = planes.size();
        }

        for (int limit = this.limit; cursor > 0 && limit > 0; cursor--, limit--) {
            toReturn.add(planes.get(cursor - 1));
        }

        return new HangerResponse()
                .setPlanes(toReturn)
                .setCursor(cursor);
    }

    public Single<HangerResponse> getPlanes() {
        return Single.just(retrievePlanes(null))
                .delay(350, TimeUnit.MILLISECONDS)
                .doOnSuccess(resp -> log.info("HangerResponse = {}", resp));
    }

    public Single<HangerResponse> getPlanesByCursor(Integer cursor) {
        return Single.just(retrievePlanes(cursor))
                .delay(350, TimeUnit.MILLISECONDS)
                .doOnSuccess(resp -> log.info("HangerResponse = {}", resp));
    }

}
