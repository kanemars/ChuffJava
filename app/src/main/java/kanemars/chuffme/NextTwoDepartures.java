package kanemars.chuffme;

import android.text.Html;
import android.text.Spanned;
import kanemars.KaneHuxleyJavaConsumer.Models.TrainService;

public class NextTwoDepartures {
    private TrainService first, second;

    NextTwoDepartures(TrainService firstTrainService, TrainService secondTrainService) {
        first = firstTrainService;
        second = secondTrainService;
    }

    Spanned toSpanned() {
        return fromHtml(String.format("<b>%s</b> %s; <b>%s</b> %s", first.std, first.etd, second.std, second.etd));
    }

    public String toString () {
        return String.format("%s %s; %s %s", first.std, first.etd, second.std, second.etd);
    }

    boolean areTrainsOnTime() {
        return first.etd.equals("On time") && second.etd.equals("On time");
    }

    @SuppressWarnings("deprecation")
    private static Spanned fromHtml(String html){
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }
}
