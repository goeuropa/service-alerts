package pl.goeuropa.servicealerts.model.serviceAlerts;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Data
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class ServiceAlert implements Serializable {

    private static final long serialVersionUID = 1L;

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
