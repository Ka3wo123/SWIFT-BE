  package pl.ka3wo.swift.model.mapper;

  import org.springframework.stereotype.Service;
  import pl.ka3wo.swift.model.SwiftData;
  import pl.ka3wo.swift.model.dto.SwiftDataDto;

  import java.util.function.Function;

  @Service
  public class SwiftDataMapper implements Function<SwiftData, SwiftDataDto> {
    private final SwiftDataBranchMapper branchMapper;

    public SwiftDataMapper(SwiftDataBranchMapper mapper) {
      this.branchMapper = mapper;
    }

    @Override
    public SwiftDataDto apply(SwiftData swiftData) {
      return new SwiftDataDto(
          swiftData.getAddress(),
          swiftData.getBankName(),
          swiftData.getCountryISO2(),
          swiftData.getCountryName(),
          swiftData.getIsHeadquarter(),
          swiftData.getSwiftCode(),
          swiftData.getBranches().stream().map(branchMapper).toList());
    }
  }
