package at.ac.tuwien.sepm.assignment.individual.service;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.entity.HorseTree;
import at.ac.tuwien.sepm.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;
import java.util.stream.Stream;

/**
 * Service for working with horses.
 */
public interface HorseService {
  /**
   * Lists all horses stored in the system.
   *
   * @return list of all stored horses
   */
  Stream<HorseListDto> allHorses();


  /**
   * Updates the horse with the ID given in {@code horse}
   * with the data given in {@code horse}
   * in the persistent data store.
   *
   * @param horse the horse to update
   * @return he updated horse
   * @throws NotFoundException if the horse with given ID does not exist in the persistent data store
   * @throws ValidationException if the update data given for the horse is in itself incorrect (description too long, no name, …)
   * @throws ConflictException if the update data given for the horse is in conflict the data currently in the system (owner does not exist, …)
   */
  HorseDetailDto update(HorseDetailDto horse) throws NotFoundException, ValidationException, ConflictException;

  /**
   * Create a new horse in the persistent data store.
   *
   * @param newHorse the data for the new owner
   * @throws ValidationException if the data given for the horse to be created is in itself incorrect (description too long, no name, …)
   * @throws ConflictException if the update data given for the horse is in conflict the data currently in the system (owner does not exist, …)
   */
  HorseDetailDto create(HorseDetailDto newHorse) throws ValidationException, ConflictException;

  /**
   * Deletes the horse with the given id.
   *
   * @param id The id of the horse to be deleted.
   * @throws NotFoundException When no horse with the given id exists.
   */
  void delete(Long id) throws NotFoundException;

  /**
   * Get the horse with given ID, with more detail information.
   * This includes the owner of the horse, and its parents.
   * The parents of the parents are not included.
   *
   * @param id the ID of the horse to get
   * @return the horse with ID {@code id}
   * @throws NotFoundException if the horse with the given ID does not exist in the persistent data store
   */
  HorseDetailDto getById(long id) throws NotFoundException;

  /**
   * Search for horses matching the criteria in {@code searchParameters}.
   * <p>
   * A horse is considered matched, if its name contains {@code searchParameters.name} as a substring.
   * The returned stream of horse never contains more than {@code searchParameters.maxAmount} elements,
   *  even if there would be more matches in the persistent data store.
   * </p>
   *
   * @param searchParameters object containing the search parameters to match
   * @return a stream containing owners matching the criteria in {@code searchParameters}
   * @throws NotFoundException if no horses are found in the database.
   */
  Stream<HorseListDto> search(HorseSearchDto searchParameters) throws NotFoundException;

  /**
   * Family tree of ancestors for a horse.
   *
   * @param id    of the horse we are getting the tree from.
   * @param depth of the tree (number of ancestors).
   * @throws ValidationException will be thrown if there is a problem while accessing database.
   * @throws NotFoundException if no horses are found in the database.
   */
  HorseTree getTree(Long id, Integer depth) throws ValidationException, NotFoundException;
}
