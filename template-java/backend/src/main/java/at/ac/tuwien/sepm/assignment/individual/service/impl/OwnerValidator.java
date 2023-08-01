package at.ac.tuwien.sepm.assignment.individual.service.impl;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.OwnerCreateDto;
import at.ac.tuwien.sepm.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class OwnerValidator {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Pattern specialCharacters = Pattern.compile("[@_!#$%^&*()<>?/|}{~:§]");

    private final Pattern emailRegex = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");

    public void validateForCreate(OwnerCreateDto newOwner) throws ValidationException, ConflictException {
        LOG.trace("validateForCreate({})", newOwner);
        List<String> validationErrors = new ArrayList<>();

        LOG.info("Validating attributes of owner");
        LOG.info("firstname...");
        if (newOwner.firstName() == null || newOwner.firstName().isBlank()) {
            validationErrors.add("Every owner must have a firstname, that consists of at least one non-whitespace character.");
        } else {
            if (newOwner.firstName().length() > 100) {
                validationErrors.add("Owner firstname too long: longer than 100 characters");
            } else if (specialCharacters.matcher(newOwner.firstName()).find()) {
                validationErrors.add("Owner firstname may only include the following Characters: Ä, Ö, Ü, A-Z, ä, ö, ü, a-z, 0-9, -, ' '.");
            }
        }

        LOG.info("lastname...");
        if (newOwner.lastName() == null || newOwner.lastName().isBlank()) {
            validationErrors.add("Every owner must have a lastname, that consists of at least one non-whitespace character.");
        } else {
            if (newOwner.lastName().length() > 100) {
                validationErrors.add("Owner lastname too long: longer than 100 characters");
            } else if (specialCharacters.matcher(newOwner.lastName()).find()) {
                validationErrors.add("Owner lastname may only include the following Characters: Ä, Ö, Ü, A-Z, ä, ö, ü, a-z, 0-9, -, ' '.");
            }
        }

        LOG.info("email...");
        if(newOwner.firstName() != null && newOwner.lastName() != null) {
        if (specialCharacters.matcher(newOwner.firstName()).find() || specialCharacters.matcher(newOwner.lastName()).find()) {
            validationErrors.add("Owner names may only include the following Characters: Ä, Ö, Ü, A-Z, ä, ö, ü, a-z, 0-9, -, ' '.");
        }
        if (newOwner.email() != null && !newOwner.email().trim().isEmpty() && !newOwner.email().trim().isBlank() && !emailRegex.matcher(newOwner.email().trim()).matches()) {
            validationErrors.add("The owner's email is invalid");}
        }
        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Validation of owner to create failed", validationErrors);
        }
    }
}
