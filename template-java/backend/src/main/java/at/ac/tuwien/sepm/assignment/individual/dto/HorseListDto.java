package at.ac.tuwien.sepm.assignment.individual.dto;

import at.ac.tuwien.sepm.assignment.individual.type.Sex;
import java.time.LocalDate;

/**
 * Class for Horse DTOs
 * Contains all common properties
 */
public record HorseListDto(
    Long id,
    String name,
    String description,
    LocalDate dateOfBirth,
    Sex sex,
    OwnerDto owner
) {

  //simply returns null if no owner provided, otherwise we get the id
  public Long ownerId() {
    return owner == null
        ? null
        : owner.id();
  }

  //return the full name of the owner
  public String ownerName() {
    return owner == null
            ? ""
            : (owner.firstName() + owner.lastName());
  }


}
