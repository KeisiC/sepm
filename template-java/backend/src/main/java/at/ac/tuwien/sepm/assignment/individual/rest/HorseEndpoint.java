package at.ac.tuwien.sepm.assignment.individual.rest;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepm.assignment.individual.service.HorseService;
import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(path = HorseEndpoint.BASE_PATH)
public class HorseEndpoint {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  static final String BASE_PATH = "/horses";

  private final HorseService service;

  public HorseEndpoint(HorseService service) {
    this.service = service;
  }

  @GetMapping
  public Stream<HorseListDto> searchHorses(HorseSearchDto searchParameters) {
    LOG.info("GET " + BASE_PATH + " " + searchParameters.toString());
    LOG.debug("request parameters: {}", searchParameters);
    //LOG.info("GET " + BASE_PATH);
    // TODO We have the request params in the DTO now, but don't do anything with them yet…
    //return service.allHorses();
    try {
      return service.search(searchParameters);
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "No horses with these parameters", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }

  @GetMapping("{id}")
  public HorseDetailDto getById(@PathVariable long id) {
    LOG.info("GET " + BASE_PATH + "/{}", id);
    try {
      return service.getById(id);
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Horse to get details of not found", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }


  @PutMapping("{id}")
  public HorseDetailDto update(@PathVariable long id, @RequestBody HorseDetailDto toUpdate) throws ValidationException, ConflictException {
    LOG.info("Updating horse...");
    LOG.info("PUT " + BASE_PATH + "/{}", toUpdate);
    LOG.debug("Body of request:\n{}", toUpdate);
    try {
      return service.update(toUpdate.withId(id));
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Horse to update not found", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }

  @PostMapping()
  @ResponseStatus(HttpStatus.CREATED)
  public HorseDetailDto create(@RequestBody HorseDetailDto horse) throws ValidationException, ConflictException {
    LOG.info("Creating new horse...");
    LOG.info("POST " + BASE_PATH + "/create");
    LOG.debug("Body of request:\n{}", horse);
    return service.create(horse);
  }

  @DeleteMapping("{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable("id") long id) throws NotFoundException {
    LOG.info("DELETE " + BASE_PATH + "/{}", id);
    LOG.debug("Id of entity to delete:\n{}", id);
    try {
      service.delete(id);
    } catch (NotFoundException e) {
      // this exception can only be thrown if deleting the entity is
      // requested directly from the backend, with a nonexistent ID
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Horse to delete not found", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }

  private void logClientError(HttpStatus status, String message, Exception e) {
    LOG.warn("{} {}: {}: {}", status.value(), message, e.getClass().getSimpleName(), e.getMessage());
  }
}
