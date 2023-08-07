package at.ac.tuwien.sepm.assignment.individual.service.impl;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepm.assignment.individual.dto.OwnerDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepm.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepm.assignment.individual.mapper.HorseMapper;
import at.ac.tuwien.sepm.assignment.individual.persistence.HorseDao;
import at.ac.tuwien.sepm.assignment.individual.service.HorseService;
import at.ac.tuwien.sepm.assignment.individual.service.OwnerService;
import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class HorseServiceImpl implements HorseService {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final HorseDao dao;
  private final HorseMapper mapper;
  private final HorseValidator horseValidator;
  private final OwnerService ownerService;
  private final HorseService horseService;

  public HorseServiceImpl(HorseDao dao, HorseMapper mapper, HorseValidator horseValidator, OwnerService ownerService) {
    this.dao = dao;
    this.mapper = mapper;
    this.horseValidator = horseValidator;
    this.ownerService = ownerService;
    this.horseService = this;
  }

  @Override
  public Stream<HorseListDto> allHorses() {
    LOG.trace("allHorses()");
    var horses = dao.getAll();
    var ownerIds = horses.stream()
        .map(Horse::getOwnerId)
        .filter(Objects::nonNull)
        .collect(Collectors.toUnmodifiableSet());
    Map<Long, OwnerDto> ownerMap;
    try {
      ownerMap = ownerService.getAllById(ownerIds);
    } catch (NotFoundException e) {
      throw new FatalException("Horse, that is already persisted, refers to non-existing owner", e);
    }
    return horses.stream()
        .map(horse -> mapper.entityToListDto(horse, ownerMap));
  }


  @Override
  public HorseDetailDto update(HorseDetailDto horse) throws NotFoundException, ValidationException, ConflictException {
    LOG.trace("update({})", horse);
    horseValidator.validateForUpdate(horse);
    var updatedHorse = dao.update(horse);
    return mapper.entityToDetailDto(
        updatedHorse,
        ownerMapForSingleId(updatedHorse.getOwnerId()),
        fatherMapForSingleId(updatedHorse.getFatherId()),
        motherMapForSingleId(updatedHorse.getMotherId())
    );
  }

  @Override
  public HorseDetailDto create(HorseDetailDto horse) throws ValidationException, ConflictException {
    LOG.trace("create({})", horse);
    horseValidator.validateForCreate(horse);
    var createdHorse = dao.create(horse);
    return mapper.entityToDetailDto(
        createdHorse,
            ownerMapForSingleId(createdHorse.getOwnerId()),
            fatherMapForSingleId(createdHorse.getFatherId()),
            motherMapForSingleId(createdHorse.getMotherId())
    );
  }

  @Override
  public void delete(Long id) throws NotFoundException {
    LOG.trace("delete({})", id);
    Horse horse = dao.getById(id);
    mapper.entityToDetailDto(horse,
            ownerMapForSingleId(horse.getOwnerId()),
            fatherMapForSingleId(horse.getFatherId()),
            motherMapForSingleId(horse.getMotherId())
    );

    List<Horse> horseList = dao.getAll();
    for (Horse currHorse : horseList) {
      // remove the horse as a father from the list
      if (Objects.equals(currHorse.getFatherId(), horse.getId())) {
        dao.update(new HorseDetailDto(currHorse.getId(), currHorse.getName(), currHorse.getDescription(), currHorse.getDateOfBirth(),
                currHorse.getSex(), ownerService.getById(currHorse.getOwnerId()), null, horseService.getById(currHorse.getMotherId())));
      }
      // remove the horse as a mother from the list
      if (Objects.equals(currHorse.getMotherId(), horse.getId())) {
        dao.update(new HorseDetailDto(currHorse.getId(), currHorse.getName(), currHorse.getDescription(), currHorse.getDateOfBirth(),
                currHorse.getSex(), ownerService.getById(currHorse.getOwnerId()), horseService.getById(currHorse.getFatherId()), null));
      }
    }

    // if the horse to be deleted is a parent, delete that reference from the database as well
    /*List<Horse> horseList = dao.getAll();
    for (Horse currHorse : horseList) {
      if (Objects.equals(currHorse.getFatherId(), horse.getId()) || Objects.equals(currHorse.getMotherId(), horse.getId())) {
        dao.update(new HorseDetailDto(currHorse.getId(), currHorse.getName(), currHorse.getDescription(), currHorse.getDateOfBirth(),
                currHorse.getSex(), ownerService.getById(currHorse.getOwnerId()), null, null));
      }
    }

     */
    dao.delete(id);
  }

  @Override
  public HorseDetailDto getById(long id) throws NotFoundException {
    LOG.trace("details({})", id);
    Horse horse = dao.getById(id);
    return mapper.entityToDetailDto(
        horse,
        ownerMapForSingleId(horse.getOwnerId()),
        //fathers
        fatherMapForSingleId(horse.getFatherId()),
        //mothers
        motherMapForSingleId(horse.getMotherId())
    );
  }


  private Map<Long, OwnerDto> ownerMapForSingleId(Long ownerId) {
    try {
      return ownerId == null
          ? null
          : Collections.singletonMap(ownerId, ownerService.getById(ownerId));
    } catch (NotFoundException e) {
      throw new FatalException("Owner %d referenced by horse not found".formatted(ownerId));
    }
  }

  private Map<Long, HorseDetailDto> fatherMapForSingleId(Long fatherId) {
    try {
      return fatherId == null
              ? null
              : Collections.singletonMap(fatherId, horseService.getById(fatherId));
    } catch (NotFoundException e) {
      throw new FatalException("Horse %d referenced by horse not found".formatted(fatherId));
    }
  }

  private Map<Long, HorseDetailDto> motherMapForSingleId(Long motherId) {
    try {
      return motherId == null
              ? null
              : Collections.singletonMap(motherId, horseService.getById(motherId));
    } catch (NotFoundException e) {
      throw new FatalException("Horse %d referenced by horse not found".formatted(motherId));
    }
  }


}
