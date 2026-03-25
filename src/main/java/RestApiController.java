import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api")
@SuppressWarnings("unused")
public class RestApiController {

    private final SimpMessagingTemplate messagingTemplate;

    @SuppressWarnings("unused")
    RestApiController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/ping")
    public Map<String, String> ping() {
        return Map.of("status", "ok");
    }

    @GetMapping("/time")
    public Map<String, String> time() {
        return Map.of("now", Instant.now().toString());
    }

    // Publish a message over WebSocket to all /topic/chat subscribers
    @PostMapping("/chat")
    public ResponseEntity<ChatMessage> publish(@RequestBody SendMessageRequest request) {
        String from = request != null && request.from() != null ? request.from() : "anonymous";
        String text = request != null && request.text() != null ? request.text() : "";
        ChatMessage message = new ChatMessage(from, text, Instant.now());
        messagingTemplate.convertAndSend("/topic/chat", message);
        return ResponseEntity.accepted().body(message);
    }
}
