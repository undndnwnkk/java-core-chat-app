package model;

public class ServerResponse {
    private boolean success;
    private String token;
    private String message;
    private String payload;


    public ServerResponse() {
    }

    public ServerResponse(boolean success, String token, String message, String payload) {
        this.success = success;
        this.token = token;
        this.message = message;
        this.payload = payload;
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

}
