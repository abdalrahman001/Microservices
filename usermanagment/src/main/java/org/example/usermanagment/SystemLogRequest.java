package org.example.usermanagment;

public class SystemLogRequest {
    private String severity; // Must be one of "INFO", "WARNING", "ERROR"
    private String serviceName;
    private String message;

    public SystemLogRequest() {
    }

    public SystemLogRequest(String severity, String serviceName, String message) {
        this.severity = severity;
        this.serviceName = serviceName;
        this.message = message;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
