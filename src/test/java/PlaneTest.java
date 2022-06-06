import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class PlaneTest extends BaseTest {

    @Test
    public void nestedLoopFilteringOld() throws InterruptedException {
        log.info("nestedLoopFilteringOld - START");
        List<Plane> planeList1 = new ArrayList<>();
        planeList1.add(new Plane().setLength(2).setName("Aircraft X1"));
        planeList1.add(new Plane().setLength(5).setName("Aircraft X2")); // <-- same length
        planeList1.add(new Plane().setLength(3).setName("Aircraft X3"));
        planeList1.add(new Plane().setLength(4).setName("Aircraft X4"));
        planeList1.add(new Plane().setLength(1).setName("Aircraft X5"));

        List<Plane> planeList2 = new ArrayList<>();
        planeList2.add(new Plane().setLength(6).setName("Aircraft Z1"));
        planeList2.add(new Plane().setLength(7).setName("Aircraft Z3"));
        planeList2.add(new Plane().setLength(8).setName("Aircraft Z4"));
        planeList2.add(new Plane().setLength(9).setName("Aircraft Z5"));
        planeList2.add(new Plane().setLength(5).setName("Aircraft Z2")); // <-- same length

        // Optimize
        boolean found = false;
        for (Plane plane1 : planeList1) {
            if (found) {
                break;
            }
            for (Plane plane2 : planeList2) {
                Thread.sleep(1000L); // Per query cost 1 second for example
                if (plane1.getLength() == plane2.getLength()) {
                    log.info("Same Length Aircraft {} and {}", plane1.getName(), plane2.getName());
                    found = true;
                    break;
                }
            }
        }

        log.info("nestedLoopFilteringOld - END");
    }

    @Test
    public void nestedLoopFilteringAsync() {
        log.info("nestedLoopFilteringAsync - START");
        List<Plane> planeList1 = new ArrayList<>();
        planeList1.add(new Plane().setLength(2).setName("Aircraft X1"));
        planeList1.add(new Plane().setLength(5).setName("Aircraft X2")); // <-- same length
        planeList1.add(new Plane().setLength(3).setName("Aircraft X3"));
        planeList1.add(new Plane().setLength(4).setName("Aircraft X4"));
        planeList1.add(new Plane().setLength(1).setName("Aircraft X5"));

        List<Plane> planeList2 = new ArrayList<>();
        planeList2.add(new Plane().setLength(6).setName("Aircraft Z1"));
        planeList2.add(new Plane().setLength(7).setName("Aircraft Z3"));
        planeList2.add(new Plane().setLength(8).setName("Aircraft Z4"));
        planeList2.add(new Plane().setLength(9).setName("Aircraft Z5"));
        planeList2.add(new Plane().setLength(5).setName("Aircraft Z2")); // <-- same length

        Multi.createFrom().iterable(planeList1)
                .onItem().transformToUniAndMerge(p1 ->
                        Multi.createFrom().iterable(planeList2)
                                .onItem().transformToUniAndMerge(p2 -> Uni.createFrom().item("stub")
                                        .onItem().delayIt().by(Duration.ofMillis(1000)) // Per query cost 1 second for example
                                        .invoke(() -> {
                                            if (p1.getLength() == p2.getLength()) {
                                                log.info("Same Length Aircraft {} and {}", p1.getName(), p2.getName());
                                            }
                                        }))
                                .collect()
                                .last()
                )
                .collect()
                .last()
//                .subscribe().with(test -> log.info("Complete")); // Subscribe #1 way
                .await().indefinitely(); // Wait here for async code to finish #2 way
        log.info("nestedLoopFilteringAsync - END");
    }

    @Test
    public void fixedSizeHanger(){
        HangerService hangerService =new HangerService(10, 3);
        List<Plane> planes = new ArrayList<>();

        hangerService.getPlanes()
                .invoke(resp -> planes.addAll(resp.getPlanes()))
                .chain(resp -> hangerService.getPlanesByCursor(resp.getCursor()))
                .invoke(resp -> planes.addAll(resp.getPlanes()))
                .chain(resp -> hangerService.getPlanesByCursor(resp.getCursor()))
                .invoke(resp -> planes.addAll(resp.getPlanes()))
                .chain(resp -> hangerService.getPlanesByCursor(resp.getCursor()))
                .invoke(resp -> planes.addAll(resp.getPlanes()))
                .await().indefinitely();

        Assertions.assertEquals(List.of(
                new Plane().setName("Aircraft 10").setLength(10),
                new Plane().setName("Aircraft 9").setLength(9),
                new Plane().setName("Aircraft 8").setLength(8),
                new Plane().setName("Aircraft 7").setLength(7),
                new Plane().setName("Aircraft 6").setLength(6),
                new Plane().setName("Aircraft 5").setLength(5),
                new Plane().setName("Aircraft 4").setLength(4),
                new Plane().setName("Aircraft 3").setLength(3),
                new Plane().setName("Aircraft 2").setLength(2),
                new Plane().setName("Aircraft 1").setLength(1)
        ), planes);
    }

    @Test
    public void dynamicSizedHanger(){
        int size = random();
        HangerService hangerService =new HangerService(size, 3);
        List<Plane> planes = new ArrayList<>();

        /*
        * Add missing code here
        * */

        Assertions.assertEquals(constructFixedSizeHanger(size), planes);
    }
}
