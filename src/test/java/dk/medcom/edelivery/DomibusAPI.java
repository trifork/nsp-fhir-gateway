package dk.medcom.edelivery;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DomibusAPI {

    // Configure selected parts of a running Domibus instance
    public void configure(DomibusConfig config) {
        Map<String, String> authHeaders;
        try {
            authHeaders = login(config.url, config.password);
        } catch (Exception e) {
            authHeaders = login(config.url, config.defaultPassword);
            changePassword(config.url, authHeaders, config.defaultPassword, config.password);
        }
        updateMessageFilters(config.url, authHeaders);
        postPMode(config.url, "Scripted_update", config.pmodeLocation, authHeaders);
        uploadTrustStore(config.url, config.trustStoreLocation, config.trustStorePassword, authHeaders);
    }

    public Map<String, String> login(String url, String password) {
        System.out.println("Logging in to " + url + "...");
        var json = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", "admin", password);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(json, headers);
        ResponseEntity<String> response = new RestTemplate().postForEntity(url + "/domibus/rest/security/authentication", requestEntity, String.class);
        HashMap<String, String> authHeaders = createAuthHeaders(response);
        //System.out.println("Authentication headers: " + authHeaders);
        return authHeaders;
    }

    // Change the default admin password (otherwise you're not allowed to use the Domibus REST API)
    public void changePassword(String url, Map<String, String> authHeaders, String defaultPassword, String password) {
        System.out.print("Changing password for " + url + "...");
        var json = String.format("{\"currentPassword\":\"%s\",\"newPassword\":\"%s\"}", defaultPassword, password);
        HttpHeaders headers = applicationJsonHeaders(authHeaders);
        HttpEntity request = new HttpEntity(json, headers);
        new RestTemplate().put(url + "/domibus/rest/security/user/password", request);
        System.out.println(" Done.");
    }

    // Look up existing filters and save (necessary in order for plugins to take effect)
    public void updateMessageFilters(String url, Map<String, String> authHeaders) {
        System.out.println("Looking up existing message filters on " + url + "...");
        String messageFiltersURL = url + "/domibus/rest/messagefilters";
        ResponseEntity<String> existingMessageFilters = new RestTemplate().exchange(messageFiltersURL, HttpMethod.GET, new HttpEntity(null, applicationJsonHeaders(authHeaders)), String.class);
        System.out.println("Saving message filters on " + url + "...");
        new RestTemplate().put(messageFiltersURL, new HttpEntity(mapFilters(existingMessageFilters.getBody()), applicationJsonHeaders(authHeaders)));
    }

    @NotNull
    private HttpHeaders applicationJsonHeaders(Map<String, String> authHeaders) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAll(authHeaders);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private String mapFilters(String existingMessageFilters) {
        try {
            JSONObject existing = new JSONObject(existingMessageFilters.substring(existingMessageFilters.indexOf("\n")+1));
            JSONArray messageFilterEntries = (JSONArray) existing.get("messageFilterEntries");
            return messageFilterEntries.toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private HashMap<String, String> createAuthHeaders(ResponseEntity<String> response) {
        List<String> responseCookieValues = response.getHeaders().get("Set-Cookie").stream().collect(Collectors.toList());
        var xsrf = responseCookieValues.stream().filter(h -> h.startsWith("XSRF-TOKEN=")).findFirst()
                .map(s -> s.substring(s.indexOf("=") + 1, s.indexOf(";"))).stream().findFirst().get();
        var cookie = String.join(";", responseCookieValues);
        var authHeaders = new HashMap<String, String>();
        authHeaders.put("X-XSRF-TOKEN", xsrf);
        authHeaders.put("Cookie", cookie);
        return authHeaders;
    }

    public void uploadTrustStore(String url, String trustStoreFile, String trustStorePassword, Map<String, String> authHeaders) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("truststore", new FileSystemResource(trustStoreFile));
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, multipartHeaders(authHeaders));
        new RestTemplate().postForEntity(url + "/domibus/rest/truststore/save?password=" + trustStorePassword, requestEntity, String.class);
    }

    @NotNull
    private static HttpHeaders multipartHeaders(Map<String, String> authHeaders) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setAll(authHeaders);
        return headers;
    }

    public static void postPMode(String url, String description, String path, Map<String, String> authHeaders) {
        System.out.println("Updating PMode on " + url + "...");
        var restTemplate = new RestTemplate();

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(path));

        HttpEntity<MultiValueMap<String, Object>> requestEntity
                = new HttpEntity<>(body, multipartHeaders(authHeaders));

        restTemplate.postForEntity(url + "/domibus/rest/pmode?description=" + description, requestEntity, String.class);
    }

}
