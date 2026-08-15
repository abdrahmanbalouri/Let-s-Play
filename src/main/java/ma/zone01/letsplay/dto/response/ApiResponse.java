package ma.zone01.letsplay.dto.response;

public class ApiResponse {
    private String message;
    private Object data;

    public ApiResponse(String message, Object data) {
        this.message = message;
        this.data = data;
    }

    public static ApiResponse success(String message, Object data) {
        return new ApiResponse(message, data);
    }

    public static ApiResponse success(String message) {
        return new ApiResponse(message, null);
    }

    public String getMessage() { return message; }
    public Object getData()    { return data; }
}
