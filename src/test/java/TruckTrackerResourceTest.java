import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.penoybalut.tot.core.FoodTruckInfo;
import com.penoybalut.tot.db.FoodTruckDAO;
import com.penoybalut.tot.resources.TruckTrackerResource;
import com.penoybalut.tot.service.FoodTruckService;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Created by ibo on 5/4/2014.
 *
 * This test wont really work as the app doesnt persist any data beyond the life of the app
 * Just added as an example/reference. Commented out temporarily
 */

public class TruckTrackerResourceTest {
    /*
    static {
        Logger.getLogger("com.sun.jersey").setLevel(Level.OFF);
    }

    private static final FoodTruckDAO dao = mock(FoodTruckDAO.class);
    private static final FoodTruckService ftserv = mock(FoodTruckService.class);

    @ClassRule
    public static final ResourceTestRule resources = ResourceTestRule.builder()
            .addResource(new TruckTrackerResource(dao, ftserv))
            .build();

    private final FoodTruckInfo foodTruck = new FoodTruckInfo("sample", "sample", "sample", "sample", "sample",
            "sample", "sample", "sample", "sample", "sample", "sample", "sample", "sample", "sample", "sample",
            "sample", "sample", "sample", "sample", "sample", "sample", "sample");

    //private final FoodTruckInfo foodTruck = new ObjectMapper().readValue(fixture("fixtures/FoodTruckInfo.json"), FoodTruckInfo.class);

    @Before
    public void setup() throws IOException {
        Optional<FoodTruckInfo> ftinfo = dao.findById(eq(1L));
        when(ftinfo.get()).thenReturn(foodTruck);
    }

    @Test
    public void testGetPerson() {
        assertThat(resources.client().resource("/foodtruck/1").get(FoodTruckInfo.class))
                .isEqualTo(foodTruck);
        verify(dao).findById(1L);
    }
    */
}
