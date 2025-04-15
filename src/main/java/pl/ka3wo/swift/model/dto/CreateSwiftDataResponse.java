package pl.ka3wo.swift.model.dto;

public class CreateSwiftDataResponse extends ApiResponse {

  private static final String DEFAULT_MESSAGE = "Successfully added new SWIFT data";

  public CreateSwiftDataResponse() {
    super(DEFAULT_MESSAGE);
  }

  public CreateSwiftDataResponse(String message) {
    super(message);
  }
}
