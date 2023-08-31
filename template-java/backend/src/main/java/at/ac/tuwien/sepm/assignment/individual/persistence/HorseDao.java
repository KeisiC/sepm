package at.ac.tuwien.sepm.assignment.individual.persistence;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.entity.HorseTree;
import at.ac.tuwien.sepm.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;

import java.util.Collection;
import java.util.List;

/**
 * Data Access Object for horses.
 * Implements access functionality to the application's persistent data store regarding horses.
 */
public interface HorseDao {
  /**
   * Get all horses stored in the persistent data store.
   *
   * @return a list of all stored horses
   */
  List<Horse> getAll();


  /**
   * Update the horse with the ID given in {@code horse}
   *  with the data given in {@code horse}
   *  in the persistent data store.
   *
   * @param horse the horse to update
   * @return the updated horse
   * @throws NotFoundException if the Horse with the given ID does not exist in the persistent data store
   */
  Horse update(HorseDetailDto horse) throws NotFoundException;

  /**
   * Get a horse by its ID from the persistent data store.
   *
   * @param id the ID of the horse to get
   * @return the horse
   * @throws NotFoundException if the Horse with the given ID does not exist in the persistent data store
   */
  Horse getById(long id) throws NotFoundException;

  /**
   * Create a new horse in the persistent data store.
   *
   * @param horse the data to create the new horse from
   * @return the newly created owner
   */
  Horse create(HorseDetailDto horse);

  /**
   * Delete horse by its ID from the database
   *
   * @param id is the new horse to be deleted
   * @throws NotFoundException  When no {@link Horse} with the given id exists.
   */
  void delete(Long id) throws NotFoundException;

  /**
   * Get the horses which match the parameters from the search.
   *
   * @param searchParameters which contains the parameters from the search.
   * @return the list of horses, which match the parameters.
   * @throws FatalException will be thrown if something goes wrong while accessing the persistent data store.
   */
  Collection<Horse> search(HorseSearchDto searchParameters, Long id);

  /**
   * Family tree of ancestors for a horse.
   *
   * @param id    of the horse we are getting the tree from.
   * @param depth of the tree (number of ancestors).
   * @throws FatalException will be thrown if something goes wrong while accessing the persistent data store.
   * @throws NotFoundException will be thrown if the horse could not be found in the database.
   */
  HorseTree getTree(Long id, Integer depth) throws NotFoundException;
}
