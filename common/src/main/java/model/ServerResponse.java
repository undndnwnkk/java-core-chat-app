package model;

public class ServerResponse {
    private boolean success;
    private String token;
    private String message;
    private String payload;
    private boolean broadcast;


    public ServerResponse() {
    }

    public ServerResponse(boolean success, String token, String message, String payload, boolean broadcast) {
        this.success = success;
        this.token = token;
        this.message = message;
        this.payload = payload;
        this.broadcast = broadcast;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public boolean isBroadcast() {
        return broadcast;
    }

    public void setBroadcast(boolean broadcast) {
        this.broadcast = broadcast;
    }
}
