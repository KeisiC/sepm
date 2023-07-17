package at.ac.tuwien.sepm.assignment.individual.persistence.impl;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.persistence.HorseDao;
import at.ac.tuwien.sepm.assignment.individual.type.Sex;
import java.lang.invoke.MethodHandles;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import static java.sql.Types.NULL;

@Repository
public class HorseJdbcDao implements HorseDao {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static final String TABLE_NAME = "horse";
  private static final String SQL_SELECT_ALL = "SELECT * FROM " + TABLE_NAME;
  private static final String SQL_SELECT_BY_ID = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
  private static final String SQL_UPDATE = "UPDATE " + TABLE_NAME
      + " SET name = ?"
      + "  , description = ?"
      + "  , date_of_birth = ?"
      + "  , sex = ?"
      + "  , owner_id = ?"
      + " WHERE id = ?";

  private static final String SQL_CREATE = "INSERT INTO " + TABLE_NAME
          + " (name, description, date_of_birth, sex, owner_id/*, mother_id, father_id*/) VALUES(?, ?, ?, ?, ?/*, ?, ?*/)";

  private static final String SQL_DELETE = "DELETE FROM " + TABLE_NAME + " WHERE id=?";

  private final JdbcTemplate jdbcTemplate;

  public HorseJdbcDao(
      JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public List<Horse> getAll() {
    LOG.trace("getAll()");
    return jdbcTemplate.query(SQL_SELECT_ALL, this::mapRow);
  }

  @Override
  public Horse getById(long id) throws NotFoundException {
    LOG.trace("getById({})", id);
    List<Horse> horses;
    horses = jdbcTemplate.query(SQL_SELECT_BY_ID, this::mapRow, id);

    if (horses.isEmpty()) {
      throw new NotFoundException("No horse with ID %d found".formatted(id));
    }
    if (horses.size() > 1) {
      // This should never happen!!
      throw new FatalException("Too many horses with ID %d found".formatted(id));
    }

    return horses.get(0);
  }


  @Override
  public Horse update(HorseDetailDto horse) throws NotFoundException {
    LOG.trace("update({})", horse);
    int updated = jdbcTemplate.update(SQL_UPDATE,
        horse.name(),
        horse.description(),
        horse.dateOfBirth(),
        horse.sex().toString(),
        horse.ownerId(),
        horse.id());
    if (updated == 0) {
      throw new NotFoundException("Could not update horse with ID " + horse.id() + ", because it does not exist");
    }

    return new Horse()
        .setId(horse.id())
        .setName(horse.name())
        .setDescription(horse.description())
        .setDateOfBirth(horse.dateOfBirth())
        .setSex(horse.sex())
        .setOwnerId(horse.ownerId())
        ;
  }


  @Override
  public Horse create(HorseDetailDto horse) {
    LOG.trace("create({})", horse);
    GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(con -> {
      java.sql.Date datetoLocalDate = new java.sql.Date(Date.from(horse.dateOfBirth().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()).getTime());

      PreparedStatement stmt = con.prepareStatement(SQL_CREATE, Statement.RETURN_GENERATED_KEYS);
      stmt.setString(1, horse.name());
      stmt.setString(2, horse.description());
      stmt.setDate(3, datetoLocalDate);
      stmt.setString(4, horse.sex().toString());

      if (horse.ownerId() != null && horse.ownerId() != 0) {
        stmt.setLong(5, horse.ownerId());
      } else {
        stmt.setNull(5, NULL);
      }

      /*if (horse.mother() != null && horse.mother() != 0) {
        stmt.setLong(6, horse.mother());
      } else {
        stmt.setNull(6, NULL);
      }

      if (horse.father() != null && horse.father() != 0) {
        stmt.setLong(7, horse.father());
      } else {
        stmt.setNull(7, NULL);
      }

       */

      return stmt;
    }, keyHolder);

    Number key = keyHolder.getKey();

    // check it out... it might be important
    /*if (key == null) {
      // This should never happen. If it does, something is wrong with the DB or the way the prepared statement is set up.
      throw new FatalException("Could not extract key for newly created owner. There is probably a programming error…");
    }

     */

    return new Horse()
            .setId(key.longValue())
            .setName(horse.name())
            .setDescription(horse.description())
            .setDateOfBirth(horse.dateOfBirth())
            .setSex(horse.sex())
            .setOwnerId(horse.ownerId())
            //.setMotherId(newHorse.mother())
            //.setFatherId(newHorse.father())
            ;
  }

  public void delete(Long id) throws NotFoundException {
    LOG.trace("Delete horse with id {}", id);

    int changes = jdbcTemplate.update(connection -> {
      PreparedStatement prepStat = connection.prepareStatement(SQL_DELETE);
      prepStat.setLong(1, id);
      return prepStat;
    });

    if (changes == 0) {
      throw new NotFoundException("Could not delete horse with ID " + id);
    }
  }

  private Horse mapRow(ResultSet result, int rownum) throws SQLException {
    return new Horse()
        .setId(result.getLong("id"))
        .setName(result.getString("name"))
        .setDescription(result.getString("description"))
        .setDateOfBirth(result.getDate("date_of_birth").toLocalDate())
        .setSex(Sex.valueOf(result.getString("sex")))
        .setOwnerId(result.getObject("owner_id", Long.class))
        ;
  }
}
