package employee.application.model;

import employee.application.model.interfaces.ApiResponse;

public record UserToken(String token) implements ApiResponse {

}
