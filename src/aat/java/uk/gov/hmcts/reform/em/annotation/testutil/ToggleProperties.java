package uk.gov.hmcts.reform.em.annotation.testutil;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "toggle")
public class ToggleProperties {

    private boolean enableMetadataEndpoint;

    public boolean isEnableMetadataEndpoint() {
        return enableMetadataEndpoint;
    }

    public void setEnableMetadataEndpoint(boolean enableMetadataEndpoint) {
        this.enableMetadataEndpoint = enableMetadataEndpoint;
    }

}
