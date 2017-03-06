package kanemars.KaneHuxleyJavaConsumer.Models;

import java.io.Serializable;

import static kanemars.KaneHuxleyJavaConsumer.StationCodes.GetCrs;

public class Journey implements Serializable{
    public String source;
    public String destination;
    public String crsSource, crsDestination;

    public Journey(String source, String destination)  {
        setJourney(source, destination);
    }

    public void setJourney(String source, String destination) {
//        if (source.equalsIgnoreCase(destination)) {
//            throw new JourneyException("Source and destination should be different");
//        }
        this.source = source;
        this.destination = destination;
        crsSource = GetCrs (source);
        crsDestination = GetCrs(destination);
    }

    @Override
    public String toString() {
        return source + " to " + destination;
    }
}

