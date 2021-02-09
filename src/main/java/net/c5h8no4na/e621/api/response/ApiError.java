package net.c5h8no4na.e621.api.response;

import lombok.Data;

@Data
public class ApiError {
    private Boolean success;
    private String reason;
}
