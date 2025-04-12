package pl.ka3wo.swift.model.mapper;

import java.util.function.Function;
import org.springframework.stereotype.Service;
import pl.ka3wo.swift.model.SwiftDataBranch;
import pl.ka3wo.swift.model.dto.SwiftDataBranchDto;

@Service
public class SwiftDataBranchMapper implements Function<SwiftDataBranch, SwiftDataBranchDto> {
  @Override
  public SwiftDataBranchDto apply(SwiftDataBranch branch) {
    return new SwiftDataBranchDto(
        branch.address(),
        branch.bankName(),
        branch.countryISO2(),
        branch.isHeadquarter(),
        branch.swiftCode());
  }
}
