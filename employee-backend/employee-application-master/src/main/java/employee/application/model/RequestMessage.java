package employee.application.model;

import employee.application.model.interfaces.ApiResponse;

public record RequestMessage(String message) implements ApiResponse {

}
