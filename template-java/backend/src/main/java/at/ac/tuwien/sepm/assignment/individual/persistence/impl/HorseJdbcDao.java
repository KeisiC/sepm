package at.ac.tuwien.sepm.assignment.individual.persistence.impl;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.entity.HorseTree;
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
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import static java.sql.Types.NULL;

@Repository
public class HorseJdbcDao implements HorseDao {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static final String TABLE_NAME = "horse";
  private static final String OWNER_ID = "owner_id";

  private static final String SQL_SELECT_ALL = "SELECT * FROM " + TABLE_NAME;
  private static final String SQL_SELECT_BY_ID = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
  private static final String SQL_UPDATE = "UPDATE " + TABLE_NAME
      + " SET name = ?"
      + "  , description = ?"
      + "  , date_of_birth = ?"
      + "  , sex = ?"
      + "  , owner_id = ?"
      + "  , father_id = ?"
      + "  , mother_id = ?"
      + " WHERE id = ?";

  private static final String SQL_CREATE = "INSERT INTO " + TABLE_NAME
          + " (name, description, date_of_birth, sex, owner_id, father_id, mother_id) VALUES(?, ?, ?, ?, ?, ?, ?)";

  private static final String SQL_DELETE = "DELETE FROM " + TABLE_NAME + " WHERE id=?";

  private static final String SQL_SELECT_SEARCH = "SELECT * FROM " + TABLE_NAME
          + " WHERE ( ? IS NULL OR LOWER(name) LIKE LOWER(?) )"
          + " AND ( ? IS NULL OR LOWER(description) LIKE LOWER(?) )"
          + " AND ( ? IS NULL OR date_of_birth < ? )"
          + " AND ( ? IS NULL OR LOWER(sex) LIKE LOWER(?) )"
          + " AND ( ? IS NULL OR owner_id = ? )";

  private static final String SQL_TREE = "WITH RECURSIVE ancestor(id, name, description, date_of_birth, sex, owner_id, mother_id, father_id, depth) AS ( "
          + "SELECT *, 1 as depth FROM " + TABLE_NAME + " WHERE id = ? "
          + "UNION ALL SELECT " + TABLE_NAME + ".*, (ancestor.depth + 1) FROM " + TABLE_NAME + ", ancestor "
          + "WHERE (ancestor.mother_id = " + TABLE_NAME + ".id OR ancestor.father_id = " + TABLE_NAME + ".id) AND ancestor.depth < ? "
          + ") SELECT ancestor.*, " + OWNER_ID + " FROM ancestor;";

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
        horse.fatherId(),
        horse.motherId(),
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
        .setFatherId(horse.fatherId())
        .setMotherId(horse.motherId())
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

      if (horse.fatherId() != null && horse.fatherId() != 0) {
        stmt.setLong(6, horse.fatherId());
      } else {
        stmt.setNull(6, NULL);
      }

      if (horse.motherId() != null && horse.motherId() != 0) {
        stmt.setLong(7, horse.motherId());
      } else {
        stmt.setNull(7, NULL);
      }

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
            .setMotherId(horse.fatherId())
            .setFatherId(horse.motherId())
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

  @Override
  public Collection<Horse> search(HorseSearchDto searchParameters, Long id) {
    LOG.trace("search({})", searchParameters);

    if (searchParameters.name() == null
            && searchParameters.description() == null
            && searchParameters.bornBefore() == null
            && searchParameters.sex() == null
            && searchParameters.ownerName() == null) {
      return this.getAll();
    }

    List<Horse> horses;

    PreparedStatementCreator cr = connection -> {
      PreparedStatement stmt = connection.prepareStatement(SQL_SELECT_SEARCH);

      if (searchParameters.name() != null) {
        stmt.setString(1, "%" + searchParameters.name().trim() + "%");
        stmt.setString(2, "%" + searchParameters.name().trim() + "%");
      } else {
        stmt.setNull(1, NULL);
        stmt.setNull(2, NULL);
      }
      if (searchParameters.description() != null) {
        stmt.setString(3, "%" + searchParameters.description().trim() + "%");
        stmt.setString(4, "%" + searchParameters.description().trim() + "%");
      } else {
        stmt.setNull(3, NULL);
        stmt.setNull(4, NULL);
      }
      if (searchParameters.bornBefore() != null) {
        stmt.setObject(5, searchParameters.bornBefore());
        stmt.setObject(6, searchParameters.bornBefore());
      } else {
        stmt.setNull(5, NULL);
        stmt.setNull(6, NULL);
      }
      if (searchParameters.sex() != null) {
        stmt.setString(7, searchParameters.sex().toString().trim());
        stmt.setString(8, searchParameters.sex().toString().trim());
      } else {
        stmt.setNull(7, NULL);
        stmt.setNull(8, NULL);
      }
      // this is probably wrong... its a string and not a Long
      if (searchParameters.ownerName() != null) {
        stmt.setLong(9, id);
        stmt.setLong(10, id);
      } else {
        stmt.setNull(9, NULL);
        stmt.setNull(10, NULL);
      }
      return stmt;
    };

    horses = jdbcTemplate.query(cr, this::mapRow);

    return horses;
  }

  @Override
  public HorseTree getTree(Long id, Integer depth) throws NotFoundException {
    LOG.trace("getTree({}, {})", id, depth);

    this.getById(id);

    List<Horse> horses;
    try {
      PreparedStatementCreator cr = connection -> {
        PreparedStatement stmt = connection.prepareStatement(SQL_TREE);
        stmt.setLong(1, id);
        stmt.setLong(2, depth);
        return stmt;
      };

      horses = jdbcTemplate.query(cr, this::mapTreeRow);
    } catch (DataAccessException e) {
      throw new NotFoundException(e);
    }

    HashMap<Long, Horse> map = new HashMap<>();
    for (Horse horse : horses) {
      map.put(horse.getId(), horse);
    }

    return horsesToTree(map, id);
  }

  private HorseTree horsesToTree(Map<Long, Horse> horses, Long root) {
    LOG.debug("Mapping horses {} to tree {}...", horses, root);

    if (root == null) {
      return null;
    }
    Horse horse = horses.get(root);
    if (horse == null) {
      return null;
    }
    HorseTree tree = new HorseTree();
    tree.setId(horse.getId());
    tree.setName(horse.getName());
    tree.setDateOfBirth(horse.getDateOfBirth());
    tree.setSex(horse.getSex());
    tree.setMother(horsesToTree(horses, horse.getMotherId()));
    tree.setFather(horsesToTree(horses, horse.getFatherId()));
    return tree;
  }

  private Horse mapRow(ResultSet result, int rownum) throws SQLException {
    return new Horse()
        .setId(result.getLong("id"))
        .setName(result.getString("name"))
        .setDescription(result.getString("description"))
        .setDateOfBirth(result.getDate("date_of_birth").toLocalDate())
        .setSex(Sex.valueOf(result.getString("sex")))
        .setOwnerId(result.getObject("owner_id", Long.class))
        .setFatherId(result.getObject("father_id", Long.class))
        .setMotherId(result.getObject("mother_id", Long.class))
            ;
  }

  private Horse mapTreeRow(ResultSet resultSet, int i) throws SQLException {
    LOG.debug("Mapping horse tree {}...", resultSet);

    final Horse horse = new Horse();
    horse.setId(resultSet.getLong("id"));
    horse.setName(resultSet.getString("name"));
    horse.setDateOfBirth(resultSet.getDate("date_of_birth").toLocalDate());
    horse.setSex(Sex.valueOf(resultSet.getString("sex")));
    horse.setMotherId(resultSet.getLong("mother_id"));
    horse.setFatherId(resultSet.getLong("father_id"));
    return horse;
  }
}
