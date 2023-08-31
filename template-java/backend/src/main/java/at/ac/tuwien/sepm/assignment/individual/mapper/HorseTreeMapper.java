package at.ac.tuwien.sepm.assignment.individual.mapper;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseTreeDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.entity.HorseTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.Map;

@Component
public class HorseTreeMapper {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    /**
     * Convert a horse entity object to a {@link HorseTreeDto}.
     *
     * @param horseTree the horse tree to convert
     * @return the converted {@link HorseTreeDto}
     */
    public HorseTreeDto entityToDto(HorseTree horseTree) {
        LOG.trace("entityToDto({})", horseTree);
        if (horseTree == null) {
            return null;
        }

        return new HorseTreeDto(
                horseTree.getId(),
                horseTree.getName(),
                horseTree.getDateOfBirth(),
                horseTree.getSex(),
                this.entityToDto(horseTree.getMother()),
                this.entityToDto(horseTree.getFather()));
    }


}
