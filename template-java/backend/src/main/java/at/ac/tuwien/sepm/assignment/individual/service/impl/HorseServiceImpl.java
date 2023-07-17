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

  public HorseServiceImpl(HorseDao dao, HorseMapper mapper, HorseValidator horseValidator, OwnerService ownerService) {
    this.dao = dao;
    this.mapper = mapper;
    this.horseValidator = horseValidator;
    this.ownerService = ownerService;
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
        ownerMapForSingleId(updatedHorse.getOwnerId()));
  }

  @Override
  public HorseDetailDto create(HorseDetailDto horse) throws ValidationException, ConflictException {
    LOG.trace("create({})", horse);
    horseValidator.validateForCreate(horse);
    var createdHorse = dao.create(horse);
    return mapper.entityToDetailDto(
        createdHorse,
        ownerMapForSingleId(createdHorse.getOwnerId()));
  }

  @Override
  public void delete(Long id) throws NotFoundException {
    LOG.trace("delete({})", id);
    Horse horse = dao.getById(id);
    mapper.entityToDetailDto(horse, ownerMapForSingleId(horse.getOwnerId()));

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
        ownerMapForSingleId(horse.getOwnerId()));
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

}
