import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.*;

@Service
@SuppressWarnings({"FieldCanBeLocal", "ConstantConditions", "unused"})
class PriceFeedService {

    private static final Logger log = LoggerFactory.getLogger(PriceFeedService.class);

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;
    private final URI wsUri;

    private final HttpClient httpClient;
    private final ScheduledExecutorService scheduler;
    private volatile WebSocket webSocket;
    private volatile boolean running = false;

    private final long initialBackoffMillis = 2_000L;
    private final long maxBackoffMillis = 30_000L;
    private long currentBackoffMillis = initialBackoffMillis;

    PriceFeedService(
            SimpMessagingTemplate messagingTemplate,
            ObjectMapper objectMapper,
            @Value("${app.market.ws-url}") String wsUrl
    ) {
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = objectMapper;
        this.wsUri = URI.create(Objects.requireNonNull(wsUrl, "app.market.ws-url must be set"));
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "price-feed");
            t.setDaemon(true);
            return t;
        });
    }

    @PostConstruct
    @SuppressWarnings("unused")
    void start() {
        running = true;
        log.info("Starting market data WebSocket connector to {}", wsUri);
        attemptConnect(0);
    }

    @PreDestroy
    @SuppressWarnings("unused")
    void stop() {
        running = false;
        try {
            if (webSocket != null) {
                webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "shutdown");
                webSocket.abort();
            }
        } catch (Exception ignored) {}
        scheduler.shutdownNow();
        log.info("Stopped market data WebSocket connector");
    }

    private void attemptConnect(long delayMillis) {
        if (!running) return;
        scheduler.schedule(this::connect, Math.max(0, delayMillis), TimeUnit.MILLISECONDS);
    }

    private void connect() {
        if (!running) return;
        log.info("Connecting to market WebSocket: {}", wsUri);
        try {
            httpClient.newWebSocketBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .buildAsync(wsUri, new Listener())
                    .whenComplete((ws, ex) -> {
                        if (ex != null) {
                            log.warn("Market WS connection failed: {}", ex.toString());
                            scheduleReconnect();
                        } else {
                            this.webSocket = ws;
                            currentBackoffMillis = initialBackoffMillis;
                            log.info("Market WS connected");
                        }
                    });
        } catch (Exception e) {
            log.warn("Market WS connect exception: {}", e.toString());
            scheduleReconnect();
        }
    }

    private void scheduleReconnect() {
        if (!running) return;
        long delay = currentBackoffMillis;
        currentBackoffMillis = Math.min(currentBackoffMillis * 2, maxBackoffMillis);
        log.info("Reconnecting market WS in {} ms", delay);
        attemptConnect(delay);
    }

    private class Listener implements WebSocket.Listener {
        private final StringBuilder buffer = new StringBuilder();

        @Override
        public void onOpen(WebSocket webSocket) {
            WebSocket.Listener.super.onOpen(webSocket);
            webSocket.request(1);
        }

        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            buffer.append(data);
            if (last) {
                String msg = buffer.toString();
                buffer.setLength(0);
                try {
                    JsonNode node = objectMapper.readTree(msg);

                    // Try common fields for price and timestamp across public feeds.
                    Double price = null;
                    Long ts = System.currentTimeMillis();

                    // Binance trade: p (string price), E (event time)
                    if (node.has("p")) {
                        JsonNode p = node.get("p");
                        if (p.isTextual()) price = Double.parseDouble(p.asText());
                        else if (p.isNumber()) price = p.asDouble();
                    }
                    if (node.has("E") && node.get("E").isNumber()) {
                        ts = node.get("E").asLong();
                    }

                    // Fallbacks: price, c (last), or nested
                    if (price == null && node.has("price") && node.get("price").isNumber()) {
                        price = node.get("price").asDouble();
                    }
                    if (price == null && node.has("c")) {
                        JsonNode c = node.get("c");
                        if (c.isTextual()) price = Double.parseDouble(c.asText());
                        else if (c.isNumber()) price = c.asDouble();
                    }
                    // Try common nested fields
                    if (price == null && node.has("data") && node.get("data").has("p")) {
                        JsonNode p = node.get("data").get("p");
                        if (p.isTextual()) price = Double.parseDouble(p.asText());
                        else if (p.isNumber()) price = p.asDouble();
                    }
                    if ((ts == null || ts <= 0) && node.has("T") && node.get("T").isNumber()) {
                        ts = node.get("T").asLong();
                    }

                    if (price != null && price > 0) {
                        PriceTick tick = new PriceTick(price, ts, wsUri.getHost());
                        // Broadcast to frontend subscribers
                        messagingTemplate.convertAndSend("/topic/price", tick);
                    }
                } catch (Exception e) {
                    // Ignore malformed messages
                }
            }
            webSocket.request(1);
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public CompletionStage<?> onPing(WebSocket webSocket, java.nio.ByteBuffer message) {
            webSocket.request(1);
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public CompletionStage<?> onPong(WebSocket webSocket, java.nio.ByteBuffer message) {
            webSocket.request(1);
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
            log.info("Market WS closed: {} - {}", statusCode, reason);
            scheduleReconnect();
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public void onError(WebSocket webSocket, Throwable error) {
            log.warn("Market WS error: {}", error.toString());
            scheduleReconnect();
        }
    }
}
