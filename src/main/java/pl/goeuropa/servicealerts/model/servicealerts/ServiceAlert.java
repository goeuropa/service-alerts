package pl.goeuropa.servicealerts.model.servicealerts;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import pl.goeuropa.servicealerts.cache.CacheManager;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

@Data
@NoArgsConstructor
public class ServiceAlert implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Value("${alert-api.zone-id}")
    private static String ZONE_ID;

    @NotNull
    private String id;
    @NotNull
    private String agencyId;

    private long creationTime = LocalDateTime.now()
            .atZone(ZoneOffset.UTC)
            .toEpochSecond();
    @NotNull
    private List<TimeRange> activeWindows;
    @NotNull
    private String cause;
    @NotNull
    private String effect;
    @NotNull
    private List<NaturalLanguageString> summaries;
    @NotNull
    private List<NaturalLanguageString> urls;
    @NotNull
    private List<SituationAffects> allAffects;
    @NotNull
    private List<NaturalLanguageString> descriptions;

}
