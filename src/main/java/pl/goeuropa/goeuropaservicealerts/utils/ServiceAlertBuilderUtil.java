package pl.goeuropa.goeuropaservicealerts.utils;

import com.google.transit.realtime.GtfsRealtime;
import pl.goeuropa.goeuropaservicealerts.model.serviceAlerts.NaturalLanguageString;
import pl.goeuropa.goeuropaservicealerts.model.serviceAlerts.ServiceAlert;
import pl.goeuropa.goeuropaservicealerts.model.serviceAlerts.SituationAffects;
import pl.goeuropa.goeuropaservicealerts.model.serviceAlerts.TimeRange;

import java.util.List;

/**
 * convenience methods to convert from Service Alert Object to GTFS-RT.
 */
public class ServiceAlertBuilderUtil {

    private static boolean REMOVE_AGENCY_ID = true;

    public static void fillFeedMessage(GtfsRealtime.FeedMessage.Builder feed, List<ServiceAlert> alerts, long time) {

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
                    if (range.getFrom() != 0)
                        timeRange.setStart(range.getFrom() / 1000);

                    if (range.getTo() != 0)
                        timeRange.setEnd(range.getTo() / 1000);

                }
            }

            if (serviceAlert.getAllAffects() != null) {
                for (SituationAffects affects : serviceAlert.getAllAffects()) {
                    GtfsRealtime.EntitySelector.Builder entitySelector = alert.addInformedEntityBuilder();
                    if (affects.getAgencyId() != null)
                        entitySelector.setAgencyId(affects.getAgencyId());

                    if (affects.getRouteId() != null)
                        entitySelector.setRouteId(normalizeId(affects.getRouteId()));

                    if (affects.getTripId() != null) {
                        GtfsRealtime.TripDescriptor.Builder trip = entitySelector.getTripBuilder();
                        trip.setTripId(normalizeId(affects.getTripId()));
                        entitySelector.setTrip(trip);
                    }
                    if (affects.getStopId() != null)
                        entitySelector.setStopId(normalizeId(affects.getStopId()));

                }
            }
        }
    }

    public static void fillTranslations(List<NaturalLanguageString> input,
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

    protected static String normalizeId(String id) {
        if (REMOVE_AGENCY_ID) {
            int index = id.indexOf('_');
            if (index != -1) {
                id = id.substring(index + 1);
            }
        }
        return id;
    }

    private static GtfsRealtime.Alert.Cause toCause(String reason) {
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

    private static GtfsRealtime.Alert.Effect toEffect(String conclusion) {
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
