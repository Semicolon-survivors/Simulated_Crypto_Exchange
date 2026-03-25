import java.time.Instant;

public record ChatMessage(String from, String text, Instant sentAt) { }
