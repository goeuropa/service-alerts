package pl.goeuropa.servicealerts.utils;

import com.google.transit.realtime.GtfsRealtime;
import com.google.transit.realtime.GtfsRealtime.Alert;
import pl.goeuropa.servicealerts.model.NaturalLanguageString;
import pl.goeuropa.servicealerts.model.ServiceAlert;
import pl.goeuropa.servicealerts.model.SituationAffects;
import pl.goeuropa.servicealerts.model.TimeRange;

import java.util.List;

/**
 * convenience methods to convert from Service Alert Object to GTFS-RT.
 */
public class AlertBuilderUtil {


    public static void fillFeedMessage(GtfsRealtime.FeedMessage.Builder feed, List<ServiceAlert> alerts, String zoneId) {

        for (ServiceAlert serviceAlert : alerts) {

            GtfsRealtime.FeedEntity.Builder entity = feed.addEntityBuilder();

            entity.setId(Integer.toString(feed.getEntityCount()));
            Alert.Builder alert = entity.getAlertBuilder();

            alert.setCause(toCause(serviceAlert.getCause()));

            alert.setEffect(toEffect(serviceAlert.getEffect()));

            fillTranslations(serviceAlert.getSummaries(),
                    alert.getHeaderTextBuilder());
            fillTranslations(serviceAlert.getDescriptions(),
                    alert.getDescriptionTextBuilder());
            fillTranslations(serviceAlert.getUrls(),
                    alert.getUrlBuilder());

            if (serviceAlert.getActiveWindows() != null) {
                for (TimeRange range : serviceAlert.getActiveWindows()) {
                    GtfsRealtime.TimeRange.Builder timeRange = alert.addActivePeriodBuilder();
                    if (range.getFrom() != null)
                        timeRange.setStart(range.getLongFrom(zoneId) / 1000);

                    if (range.getTo() != null)
                        timeRange.setEnd(range.getLongTo(zoneId) / 1000);

                }
            }

            if (serviceAlert.getAllAffects() != null) {
                for (SituationAffects affects : serviceAlert.getAllAffects()) {
                    GtfsRealtime.EntitySelector.Builder entitySelector = alert.addInformedEntityBuilder();
                    if (serviceAlert.getAgencyId() != null)
                        entitySelector.setAgencyId(serviceAlert.getAgencyId());

                    if (affects.getRouteId() != null)
                        entitySelector.setRouteId(affects.getRouteId());

                    if (affects.getTripId() != null) {
                        GtfsRealtime.TripDescriptor.Builder trip = entitySelector.getTripBuilder();
                        trip.setTripId(affects.getTripId());
                        entitySelector.setTrip(trip);
                    }
                    if (affects.getStopId() != null)
                        entitySelector.setStopId(affects.getStopId());

                }
            }
        }
    }

    static void fillTranslations(List<NaturalLanguageString> input,
                                 GtfsRealtime.TranslatedString.Builder output) {
        if (input != null) {
            for (NaturalLanguageString nls : input) {
                GtfsRealtime.TranslatedString.Translation.Builder translation = output.addTranslationBuilder();
                translation.setText(nls.getValue());
                if (nls.getLang() != null) {
                    translation.setLanguage(nls.getLang());
                }
            }
        }
    }

    static Alert.Cause toCause(String reason) {
        return switch (reason) {
            case "OTHER_CAUSE" -> Alert.Cause.OTHER_CAUSE;
            case "TECHNICAL_PROBLEM" -> Alert.Cause.TECHNICAL_PROBLEM;
            case "STRIKE" -> Alert.Cause.STRIKE;
            case "DEMONSTRATION" -> Alert.Cause.DEMONSTRATION;
            case "ACCIDENT" -> Alert.Cause.ACCIDENT;
            case "HOLIDAY" -> Alert.Cause.HOLIDAY;
            case "WEATHER" -> Alert.Cause.WEATHER;
            case "MAINTENANCE" -> Alert.Cause.MAINTENANCE;
            case "CONSTRUCTION" -> Alert.Cause.CONSTRUCTION;
            case "POLICE_ACTIVITY" -> Alert.Cause.POLICE_ACTIVITY;
            case "MEDICAL_EMERGENCY" -> Alert.Cause.MEDICAL_EMERGENCY;
            default -> Alert.Cause.UNKNOWN_CAUSE;
        };
    }

    static Alert.Effect toEffect(String conclusion) {
        return switch (conclusion) {
            case "ADDITIONAL_SERVICE" -> Alert.Effect.ADDITIONAL_SERVICE;
            case "DETOUR" -> Alert.Effect.DETOUR;
            case "MODIFIED_SERVICE" -> Alert.Effect.MODIFIED_SERVICE;
            case "NO_SERVICE" -> Alert.Effect.NO_SERVICE;
            case "OTHER_EFFECT" -> Alert.Effect.OTHER_EFFECT;
            case "REDUCED_SERVICE" -> Alert.Effect.REDUCED_SERVICE;
            case "SIGNIFICANT_DELAYS" -> Alert.Effect.SIGNIFICANT_DELAYS;
            case "STOP_MOVED" -> Alert.Effect.STOP_MOVED;
            default -> Alert.Effect.UNKNOWN_EFFECT;
        };
    }
}
