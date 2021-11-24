package fate.spring.data.neo4j.servants;

import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

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

	@Query("MATCH (servant:Servant) " +
			"MATCH (servant)-[q]->(classe:SClasse) " +
			"MATCH (servant)-[t]->(power:SRanking) " +
			"RETURN servant, classe, power, q, t")
	List<ServantsDTO> getAllServants();

	@Query("OPTIONAL MATCH (p1:Servant {name: $name1 }), (p2:Servant {name: $name2 }) " +
			"CALL apoc.algo.dijkstra(p1, p2, 'CONHECE', 'l', 1) " +
			"YIELD path " +
			"WITH COLLECT(nodes(path)) as no " +
			"RETURN no")
	Collection<ServantsDTO> getGrafoConhecidos(String name1, String name2);
}
