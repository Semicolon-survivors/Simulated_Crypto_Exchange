import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.http.WebSocket;
import java.util.Arrays;
import java.util.concurrent.CompletionStage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PriceFeedServiceTest {

    @Test
    void parsesBinanceTradeAndPublishesToTopic() throws Exception {
        // Arrange
        SimpMessagingTemplate template = Mockito.mock(SimpMessagingTemplate.class);
        ObjectMapper mapper = new ObjectMapper();

        PriceFeedService svc = new PriceFeedService(template, mapper, "wss://example.org/ws");

        // Reflectively create the inner Listener instance: new PriceFeedService.Listener(svc)
        Class<?> listenerClass = Arrays.stream(PriceFeedService.class.getDeclaredClasses())
                .filter(c -> c.getSimpleName().equals("Listener"))
                .findFirst()
                .orElseThrow();
        Constructor<?> ctor = listenerClass.getDeclaredConstructor(PriceFeedService.class);
        ctor.setAccessible(true);
        Object listener = ctor.newInstance(svc);

        // Prepare onText method
        Method onText = listenerClass.getDeclaredMethod("onText", WebSocket.class, CharSequence.class, boolean.class);
        onText.setAccessible(true);

        WebSocket ws = Mockito.mock(WebSocket.class);
        Mockito.doNothing().when(ws).request(Mockito.anyLong());

        String binanceTradeJson = """
                {"e":"trade","E":170000,"p":"123.45","q":"0.01"}
                """;

        // Act
        Object result = onText.invoke(listener, ws, binanceTradeJson, true);

        // Assert the CompletionStage is returned
        assertThat(result).isInstanceOf(CompletionStage.class);

        // Capture what was published
        ArgumentCaptor<Object> payloadCaptor = ArgumentCaptor.forClass(Object.class);
        verify(template, timeout(1000)).convertAndSend(eq("/topic/price"), payloadCaptor.capture());

        Object payload = payloadCaptor.getValue();
        assertThat(payload).isInstanceOf(PriceTick.class);
        PriceTick tick = (PriceTick) payload;

        assertThat(tick.price()).isEqualTo(123.45d);
        assertThat(tick.ts()).isEqualTo(170000L);
        assertThat(tick.source()).isEqualTo("example.org");
    }
}
