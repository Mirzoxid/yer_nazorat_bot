package uz.pr.yer_nazorat_bot.utils;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Date;

public class FileResourceUtil {

    private static final RestTemplate REST_TEMPLATE = new RestTemplate();


    public static Result uploadResource(String uploadUrl, String clientId, ResourceType fileResourceType, String originalFilename, byte[] fileBytes) {
        try {
            MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
            map.add("clientId", clientId);
            map.add("fileResourceType", fileResourceType.name());
            map.add("file", new ByteArrayResource(fileBytes) {
                @Override
                public String getFilename() {
                    return originalFilename;
                }
            });
            // Now you can send your file along.
            return REST_TEMPLATE.postForObject(new URI(uploadUrl), map, Result.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static Result uploadBase64(String uploadUrl, String clientId, ResourceType fileResourceType, String originalFilename, String base64) throws Exception {
            MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
            map.add("clientId", clientId);
            map.add("fileResourceType", fileResourceType.name());
            map.add("fileName", originalFilename);
            map.add("file", base64);
            // Now you can send your file along.
            return REST_TEMPLATE.postForObject(new URI(uploadUrl), map, Result.class);
    }

    public enum ResourceType {
        TG_FILES
    }

    public static class  Result {
        private String id;

        private Long ordered;

        private Date createdDate;

        private String storedName;

        private String uploadedName;

        private String extension;

        private String fileResourceType;

        private Long fileResourceSize;

        private String fileResourceUrl;

        private String storedFullName;

        private String uploadedFullName;

        public String getId() {
            return id;
        }

        public Result setId(String id) {
            this.id = id;
            return this;
        }

        public Long getOrdered() {
            return ordered;
        }

        public Result setOrdered(Long ordered) {
            this.ordered = ordered;
            return this;
        }

        public Date getCreatedDate() {
            return createdDate;
        }

        public Result setCreatedDate(Date createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public String getStoredName() {
            return storedName;
        }

        public Result setStoredName(String storedName) {
            this.storedName = storedName;
            return this;
        }

        public String getUploadedName() {
            return uploadedName;
        }

        public Result setUploadedName(String uploadedName) {
            this.uploadedName = uploadedName;
            return this;
        }

        public String getExtension() {
            return extension;
        }

        public Result setExtension(String extension) {
            this.extension = extension;
            return this;
        }

        public String getFileResourceType() {
            return fileResourceType;
        }

        public Result setFileResourceType(String fileResourceType) {
            this.fileResourceType = fileResourceType;
            return this;
        }

        public Long getFileResourceSize() {
            return fileResourceSize;
        }

        public Result setFileResourceSize(Long fileResourceSize) {
            this.fileResourceSize = fileResourceSize;
            return this;
        }

        public String getFileResourceUrl() {
            return fileResourceUrl;
        }

        public Result setFileResourceUrl(String fileResourceUrl) {
            this.fileResourceUrl = fileResourceUrl;
            return this;
        }

        public String getUploadedFullName() {
            return uploadedFullName;
        }

        public Result setUploadedFullName(String uploadedFullName) {
            this.uploadedFullName = uploadedFullName;
            return this;
        }

        public String getStoredFullName() {
            return storedFullName;
        }

        public void setStoredFullName(String storedFullName) {
            this.storedFullName = storedFullName;
        }
    }

}
