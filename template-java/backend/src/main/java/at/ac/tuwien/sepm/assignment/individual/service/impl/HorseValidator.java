package at.ac.tuwien.sepm.assignment.individual.service.impl;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import at.ac.tuwien.sepm.assignment.individual.persistence.HorseDao;
import at.ac.tuwien.sepm.assignment.individual.persistence.OwnerDao;
import at.ac.tuwien.sepm.assignment.individual.type.Sex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class HorseValidator {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final Pattern specialCharacters = Pattern.compile("[@_!#$%^&*()<>?/|}{~:§]");

  private final HorseDao horseDao;
  private final OwnerDao ownerDao;

  public HorseValidator(HorseDao horseDao, OwnerDao ownerDao) {
    this.horseDao = horseDao;
    this.ownerDao = ownerDao;
  }


  public void validateForUpdate(HorseDetailDto updatedHorse) throws ValidationException, ConflictException, NotFoundException {
    LOG.trace("validateForUpdate({})", updatedHorse);
    List<String> validationErrors = new ArrayList<>();

    if (updatedHorse.id() == null) {
      validationErrors.add("No ID given");
    }

    LOG.info("name...");
    if (updatedHorse.name() == null || updatedHorse.name().isBlank()) {
      validationErrors.add("Every horse must have a name, that consists of at least one non-whitespace character.");
    } else {
      if (updatedHorse.name().length() > 100) {
        validationErrors.add("Horse name too long: longer than 100 characters");
      } else if (specialCharacters.matcher(updatedHorse.name()).find()) {
        validationErrors.add("Horse names may only include the following Characters: Ä, Ö, Ü, A-Z, ä, ö, ü, a-z, 0-9, -, ' '.");
      }
    }

    LOG.info("description...");
    if (updatedHorse.description() != null) {
      if (updatedHorse.description().isBlank()) {
        validationErrors.add("Horse description is given but blank");
      }
      if (updatedHorse.description().length() > 4095) {
        validationErrors.add("Horse description too long: longer than 4095 characters");
      }
    }

    LOG.info("date of birth...");
    if (updatedHorse.dateOfBirth() == null) {
      validationErrors.add("Every horse must have a birthday.");
    } else {
      if (updatedHorse.dateOfBirth().atStartOfDay().isAfter(LocalDate.now().atStartOfDay())) {
        validationErrors.add("The date of birth cannot be in the future");
      }
    }

    LOG.info("sex...");
    if (updatedHorse.sex() == null) {
      validationErrors.add("Every horse must have a biological gender");
    }

    LOG.info("owner...");
    if (updatedHorse.ownerId() != null && updatedHorse.ownerId() != 0) {
      try {
        ownerDao.getById(updatedHorse.ownerId());
      } catch (NotFoundException e) {
        validationErrors.add("The owner reference of the horse is not in the database");
      }
    }

    /*LOG.info("father...");
    if (updatedHorse.id() != null && (updatedHorse.id().equals(updatedHorse.father().id()))) {
      throw new ValidationException("The father reference of the horse cannot be parsed to itself", validationErrors);
    }
    if (updatedHorse.father() != null && updatedHorse.father().id() != 0) {
      try {
        horseDao.getById(updatedHorse.father().id());
      } catch (NotFoundException e) {
        throw new ValidationException("The father reference of the horse is not in the database", validationErrors);
      }
      assert updatedHorse.dateOfBirth() != null;
      if (updatedHorse.dateOfBirth().isBefore(horseDao.getById(updatedHorse.father().id()).getDateOfBirth())) {
        throw new ValidationException("The father of the horse cannot be younger than the horse", validationErrors);
      }
      if (horseDao.getById(updatedHorse.father().id()).getSex() != Sex.MALE) {
        throw new ValidationException("The father of the horse is not female", validationErrors);
      }
    }

     */

    /*LOG.info("mother...");
    if (updatedHorse.id() != null && (updatedHorse.id().equals(updatedHorse.mother().id()))) {
      throw new ValidationException("The mother reference of the horse cannot be parsed to itself", validationErrors);
    }
    if (updatedHorse.mother() != null && updatedHorse.mother().id() != 0) {
      try {
        horseDao.getById(updatedHorse.mother().id());
      } catch (NotFoundException e) {
        throw new ValidationException("The mother reference of the horse is not in the database", validationErrors);
      }
      assert updatedHorse.dateOfBirth() != null;
      if (updatedHorse.dateOfBirth().isBefore(horseDao.getById(updatedHorse.mother().id()).getDateOfBirth())) {
        throw new ValidationException("The mother of the horse cannot be younger than the horse", validationErrors);
      }
      if (horseDao.getById(updatedHorse.mother().id()).getSex() != Sex.FEMALE) {
        throw new ValidationException("The mother of the horse is not female", validationErrors);
      }
    }

     */

    // TODO this is not complete…

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Updated horse is not valid", validationErrors);
    }
  }

  public void validateForCreate(HorseDetailDto newHorse) throws ValidationException, ConflictException {
    LOG.trace("validateForCreate({})", newHorse);
    List<String> validationErrors = new ArrayList<>();

    LOG.info("Validating attributes of horse");
    LOG.info("name...");
    if (newHorse.name() == null || newHorse.name().isBlank()) {
      validationErrors.add("Every horse must have a name, that consists of at least one non-whitespace character.");
    } else {
      if (newHorse.name().length() > 100) {
        validationErrors.add("Horse name too long: longer than 100 characters");
      } else if (specialCharacters.matcher(newHorse.name()).find()) {
        validationErrors.add("Horse names may only include the following Characters: Ä, Ö, Ü, A-Z, ä, ö, ü, a-z, 0-9, -, ' '.");
      }
    }

    LOG.info("description...");
    if (newHorse.description() != null) {
      if (newHorse.description().isBlank()) {
        validationErrors.add("Horse description is given but blank");
      }
      if (newHorse.description().length() > 4095) {
        validationErrors.add("Horse description too long: longer than 4095 characters");
      }
    }

    LOG.info("date of birth...");
    if (newHorse.dateOfBirth() == null) {
      validationErrors.add("Every horse must have a birthday.");
    } else {
      if (newHorse.dateOfBirth().atStartOfDay().isAfter(LocalDate.now().atStartOfDay())) {
        validationErrors.add("The date of birth cannot be in the future");
      }
    }

    LOG.info("sex...");
    if (newHorse.sex() == null) {
      validationErrors.add("Every horse must have a biological gender");
    }

    LOG.info("owner...");
    if (newHorse.ownerId() != null && newHorse.ownerId() != 0) {
      try {
        ownerDao.getById(newHorse.ownerId());
      } catch (NotFoundException e) {
        validationErrors.add("The owner reference of the horse is not in the database");
      }
    }

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Created horse is not valid", validationErrors);
    }

    // should validate date of birth for mother and father, but you can make sure you get the right horses in the frontend
    // when they are suggested in the list of male and female horses, you add another parameter filtering the date (bornbefore)

    /*LOG.info("Validating parameter: mother...");
    if (newHorse.id() != null && Objects.equals(newHorse.id(), newHorse.mother())) {
      throw new ValidationException("The mother reference of the horse cannot be parsed to itself", validationErrors);
    }
    if (newHorse.mother() != null && newHorse.mother() != 0) {
      try {
        horseDao.getById(newHorse.mother());
      } catch (NotFoundException e) {
        throw new ValidationException("The mother reference of the horse is not in the database", validationErrors);
      }
      if (newHorse.dateOfBirth().isBefore(horseDao.getById(newHorse.mother()).getDateOfBirth())) {
        throw new ValidationException("The mother of the horse cannot be younger than the horse", validationErrors);
      }
      if (horseDao.getById(newHorse.mother()).getSex() != Sex.FEMALE) {
        throw new ValidationException("The mother of the horse is not female", validationErrors);
      }
    }

    LOG.info("Validating parameter: father...");
    if (newHorse.id() != null && newHorse.id().equals(newHorse.father())) {
      throw new ValidationException("The father reference of the horse cannot be parsed to itself", validationErrors);
    }
    if (newHorse.father() != null && newHorse.father() != 0) {
      try {
        horseDao.getById(newHorse.father());
      } catch (NotFoundException e) {
        throw new ValidationException("The father reference of the horse is not in the database", validationErrors);
      }
      if (newHorse.dateOfBirth().isBefore(horseDao.getById(newHorse.father()).getDateOfBirth())) {
        throw new ValidationException("The father of the horse cannot be younger than the horse", validationErrors);
      }
      if (horseDao.getById(newHorse.father()).getSex() != Sex.MALE) {
        throw new ValidationException("The father of the horse is not female", validationErrors);
      }
      if (!validationErrors.isEmpty()) {
        throw new ValidationException("Validation of horse for update failed", validationErrors);
      }
    }
    */
  }

}
