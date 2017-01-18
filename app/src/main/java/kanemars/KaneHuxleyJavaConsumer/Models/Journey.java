package kanemars.KaneHuxleyJavaConsumer.Models;

public class Journey {
    public String source;
    public String destination;

    public Journey(String source, String destination) {
        this.source = source;
        this.destination = destination;
    }

    @Override
    public String toString() {

        return source + " to " + destination;
    }
}
