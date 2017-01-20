package kanemars.KaneHuxleyJavaConsumer.Models;

import java.io.Serializable;

public class Journey implements Serializable{
    public String source;
    public String destination;

    public Journey(String source, String destination) throws JourneyException {
        if (source.equalsIgnoreCase(destination)) {
            throw new JourneyException("Source and destination should be different");
        }
        this.source = source;
        this.destination = destination;
    }

    @Override
    public String toString() {
        return source + " to " + destination;
    }
}

