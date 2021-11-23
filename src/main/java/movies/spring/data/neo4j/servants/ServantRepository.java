package movies.spring.data.neo4j.servants;

import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * This repository is indirectly used in the {@code movies.spring.data.neo4j.api.MovieController} via a dedicated movie service.
 * It is not a public interface to indicate that access is either through the rest resources or through the service.
 *
 * @author Michael Hunger
 * @author Mark Angrish
 * @author Michael J. Simons
 */
interface ServantRepository extends Repository<Servant, Integer> {

	@Query("MATCH (servant:Servant) " +
			"WHERE servant.name CONTAINS $name " +
			"MATCH (servant)-[r]->(classe:SClasse) " +
			"MATCH (servant)-[p]->(power:SRanking) " +
			"RETURN servant, classe, power, r, p")
	List<ServantsDTO> findSearchResults(@Param("name") String name);

	@Query("MATCH (s:Servant) " +
			"WHERE s.servant_id = $id " +
			"MATCH (s)-[q]->(c:SClasse) " +
			"MATCH (s)-[t]->(u:SRanking) " +
			"MATCH (servant:Servant) " +
			"MATCH (servant)-[r]->(classe:SClasse) " +
			"MATCH (servant)-[p]->(power:SRanking) " +
			"WHERE classe = c AND power = u AND s.servant_id <> servant.servant_id " +
			"RETURN servant, classe, power, r, p")
	List<ServantsDTO> findServantSubstitutes(@Param("id") int id);
}
