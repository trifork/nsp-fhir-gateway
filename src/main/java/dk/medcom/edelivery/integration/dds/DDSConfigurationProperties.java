package dk.medcom.edelivery.integration.dds;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "dds")
public class DDSConfigurationProperties {

    private String registryUrl;
    // DDS_REPOSITORYUNIQUEID
    private String repositoryUniqueId;
    // DDS_HOMECOMMUNITYID
    private String homeCommunityId;

    private boolean addDgwsHeader = true;

    public String getRegistryUrl() {
        return registryUrl;
    }

    public void setRegistryUrl(String registryUrl) {
        this.registryUrl = registryUrl;
    }

    public String getRepositoryUniqueId() {
        return repositoryUniqueId;
    }

    public void setRepositoryUniqueId(String repositoryUniqueId) {
        this.repositoryUniqueId = repositoryUniqueId;
    }

    public String getHomeCommunityId() {
        return homeCommunityId;
    }

    public void setHomeCommunityId(String homeCommunityId) {
        this.homeCommunityId = homeCommunityId;
    }

    public boolean isAddDgwsHeader() {
        return addDgwsHeader;
    }

    public void setAddDgwsHeader(boolean addDgwsHeader) {
        this.addDgwsHeader = addDgwsHeader;
    }
}
