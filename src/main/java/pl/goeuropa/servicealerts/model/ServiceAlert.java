package pl.goeuropa.servicealerts.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import static java.time.LocalDateTime.now;

@Data
public class ServiceAlert implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    @NotNull
    private String agencyId;
    @JsonIgnore
    private long creationTime;
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
