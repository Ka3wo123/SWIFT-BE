package pl.ka3wo.swift.model.dto;

public class DeleteSwiftDataResponse extends ApiResponse{

    private static final String DEFAULT_MESSAGE = "Successfully deleted SWIFT data";

    public DeleteSwiftDataResponse() {
        super(DEFAULT_MESSAGE);
    }
    public DeleteSwiftDataResponse(String message) {
        super(message);
    }
}
