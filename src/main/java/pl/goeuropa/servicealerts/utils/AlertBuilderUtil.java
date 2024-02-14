package pl.goeuropa.servicealerts.utils;

import com.google.transit.realtime.GtfsRealtime;
import pl.goeuropa.servicealerts.model.serviceAlerts.NaturalLanguageString;
import pl.goeuropa.servicealerts.model.serviceAlerts.ServiceAlert;
import pl.goeuropa.servicealerts.model.serviceAlerts.SituationAffects;
import pl.goeuropa.servicealerts.model.serviceAlerts.TimeRange;

import java.util.List;

/**
 * convenience methods to convert from Service Alert Object to GTFS-RT.
 */
public class AlertBuilderUtil {

    public static void fillFeedMessage(GtfsRealtime.FeedMessage.Builder feed, List<ServiceAlert> alerts) {

        for (ServiceAlert serviceAlert : alerts) {

            GtfsRealtime.FeedEntity.Builder entity = feed.addEntityBuilder();

            entity.setId(Integer.toString(feed.getEntityCount()));
            GtfsRealtime.Alert.Builder alert = entity.getAlertBuilder();

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
                        timeRange.setStart(range.getLongFrom() / 1000);

                    if (range.getTo() != null)
                        timeRange.setEnd(range.getLongTo() / 1000);

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

    static GtfsRealtime.Alert.Cause toCause(String reason) {
        return switch (reason) {
            case "OTHER_CAUSE" -> GtfsRealtime.Alert.Cause.OTHER_CAUSE;
            case "TECHNICAL_PROBLEM" -> GtfsRealtime.Alert.Cause.TECHNICAL_PROBLEM;
            case "STRIKE" -> GtfsRealtime.Alert.Cause.STRIKE;
            case "DEMONSTRATION" -> GtfsRealtime.Alert.Cause.DEMONSTRATION;
            case "ACCIDENT" -> GtfsRealtime.Alert.Cause.ACCIDENT;
            case "HOLIDAY" -> GtfsRealtime.Alert.Cause.HOLIDAY;
            case "WEATHER" -> GtfsRealtime.Alert.Cause.WEATHER;
            case "MAINTENANCE" -> GtfsRealtime.Alert.Cause.MAINTENANCE;
            case "CONSTRUCTION" -> GtfsRealtime.Alert.Cause.CONSTRUCTION;
            case "POLICE_ACTIVITY" -> GtfsRealtime.Alert.Cause.POLICE_ACTIVITY;
            case "MEDICAL_EMERGENCY" -> GtfsRealtime.Alert.Cause.MEDICAL_EMERGENCY;
            default -> GtfsRealtime.Alert.Cause.UNKNOWN_CAUSE;
        };
    }

    static GtfsRealtime.Alert.Effect toEffect(String conclusion) {
        return switch (conclusion) {
            case "ADDITIONAL_SERVICE" -> GtfsRealtime.Alert.Effect.ADDITIONAL_SERVICE;
            case "DETOUR" -> GtfsRealtime.Alert.Effect.DETOUR;
            case "MODIFIED_SERVICE" -> GtfsRealtime.Alert.Effect.MODIFIED_SERVICE;
            case "NO_SERVICE" -> GtfsRealtime.Alert.Effect.NO_SERVICE;
            case "OTHER_EFFECT" -> GtfsRealtime.Alert.Effect.OTHER_EFFECT;
            case "REDUCED_SERVICE" -> GtfsRealtime.Alert.Effect.REDUCED_SERVICE;
            case "SIGNIFICANT_DELAYS" -> GtfsRealtime.Alert.Effect.SIGNIFICANT_DELAYS;
            case "STOP_MOVED" -> GtfsRealtime.Alert.Effect.STOP_MOVED;
            default -> GtfsRealtime.Alert.Effect.UNKNOWN_EFFECT;
        };
    }
}
