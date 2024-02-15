/**
 * Copyright (C) 2011 Brian Ferris <bdferris@onebusaway.org>
 * Copyright (C) 2011 Google, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pl.goeuropa.servicealerts.model.servicealerts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Data
public class TimeRange implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank @NotNull
    String from;
    @NotBlank @NotNull
    String to;

    @JsonIgnore
    public long getLongFrom () {
        return LocalDateTime.parse(this.from, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                .toEpochSecond(ZoneOffset.UTC);
    }
    @JsonIgnore
    public long getLongTo () {
        return LocalDateTime.parse(this.to, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                .toEpochSecond(ZoneOffset.UTC);
    }
}
