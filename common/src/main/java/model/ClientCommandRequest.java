package model;

public class ClientCommandRequest {
    private String token;
    private CommandType type;
    private String args;

    public ClientCommandRequest() {
    }

    public ClientCommandRequest(String token, CommandType type, String args) {
        this.token = token;
        this.type = type;
        this.args = args;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public CommandType getType() {
        return type;
    }

    public void setType(CommandType type) {
        this.type = type;
    }

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }
}
