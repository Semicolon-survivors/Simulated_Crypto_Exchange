import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ActuatorIntegrationIT {

    @LocalServerPort
    @SuppressWarnings("unused")
    int port;

    @Autowired
    TestRestTemplate rest;

    // Prevent external WS connections during tests
    @MockBean
    @SuppressWarnings("unused")
    PriceFeedService priceFeedService;

    @Test
    void healthEndpointIsUp() {
        ResponseEntity<String> res = rest.getForEntity("/actuator/health", String.class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).contains("\"status\":\"UP\"");
    }

    @Test
    void prometheusEndpointExposesMetrics() {
        ResponseEntity<String> res = rest.getForEntity("/actuator/prometheus", String.class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(res.getBody()).contains("jvm_memory_used_bytes");
    }
}
