package com.caseycapozzi.portfolioapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsgsResponse {
    
    @JsonProperty("value")
    private Value value;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Value {
        @JsonProperty("timeSeries")
        private List<TimeSeries> timeSeries;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TimeSeries {
        @JsonProperty("sourceInfo")
        private SourceInfo sourceInfo;

        @JsonProperty("variable")
        private Variable variable; // Added this missing piece

        @JsonProperty("values")
        private List<Values> values;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Variable {
        @JsonProperty("variableCode")
        private List<VariableCode> variableCode;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VariableCode {
        private String value; // This holds "00060" or "00065"
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SourceInfo {
        private String siteName;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Values {
        @JsonProperty("value")
        private List<ValuePoint> valuePoints;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ValuePoint {
        private String value;     
        private String dateTime;  
    }
}