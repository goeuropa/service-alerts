package pl.goeuropa.servicealerts.model.servicealerts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static java.time.LocalDateTime.*;

@Data
public class ServiceAlert implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id = uuid.toString().substring(0,5);
    @NotNull
    private String agencyId;
    @JsonIgnore
    private long creationTime = now()
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

    @JsonIgnore
    private static UUID uuid = UUID.randomUUID();

}
