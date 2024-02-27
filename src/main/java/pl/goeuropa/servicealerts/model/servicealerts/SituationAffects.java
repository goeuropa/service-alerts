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

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public final class SituationAffects implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String routeId;

    private String tripId;

    private String stopId;

    private String routeType;
}
