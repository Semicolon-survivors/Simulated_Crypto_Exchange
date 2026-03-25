import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.Instant;

@Controller
@SuppressWarnings("unused")
public class MessageController {

    // Clients send to /app/chat.send
    // Subscribers listening on /topic/chat will receive the response
    @MessageMapping("/chat.send")
    @SendTo("/topic/chat")
    public ChatMessage send(SendMessageRequest req) {
        String from = req != null && req.from() != null ? req.from() : "anonymous";
        String text = req != null && req.text() != null ? req.text() : "";
        return new ChatMessage(from, text, Instant.now());
    }
}
