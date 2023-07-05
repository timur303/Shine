package kg.kadyrbekov.exception;

import io.swagger.annotations.ApiModelProperty;

public class Error {
    @ApiModelProperty(required = true, example = "Reason ")
    private String reason;


    public Error() {
    }

    public Error(String reason) {
        this.reason = reason;
    }


    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
