package movies.spring.data.neo4j.servants;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;
import org.neo4j.driver.types.TypeSystem;
import org.springframework.data.neo4j.core.DatabaseSelectionProvider;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Michael Hunger
 * @author Mark Angrish
 * @author Jennifer Reif
 * @author Michael J. Simons
 */
@Service
public class ServantService {

	private final ServantRepository servantRepository;

	private final Neo4jClient neo4jClient;

	private final Driver driver;

	private final DatabaseSelectionProvider databaseSelectionProvider;

	ServantService(ServantRepository servantRepository,
				 Neo4jClient neo4jClient,
				 Driver driver,
				 DatabaseSelectionProvider databaseSelectionProvider) {

		this.servantRepository = servantRepository;
		this.neo4jClient = neo4jClient;
		this.driver = driver;
		this.databaseSelectionProvider = databaseSelectionProvider;
	}

	public ServantDetailsDto fetchDetailsById(int id) {
		return this.neo4jClient
				.query("MATCH (servant:Servant {servant_id: $id}) " +
						"MATCH (servant)-[:É_DA_CLASSE]->(classe:SClasse) " +
						"MATCH (servant)-[:É_DA_FORÇA]->(power:SRanking)" +
						//"WITH movie, COLLECT({ name: person.name, job: REPLACE(TOLOWER(TYPE(r)), '_in', ''), role: HEAD(r.roles) }) as cast " +
						"RETURN servant { .name, .servant_id, .classe, .power } " +
						"LIMIT 1"
				)
				.in(database())
				.bindAll(Map.of("id", id))
				.fetchAs(ServantDetailsDto.class)
				.mappedBy(this::toServantDetails)
				.one()
				.orElse(null);
	}

	public List<ServantResultDto> searchServantsByName(String name) {
		return this.servantRepository.findSearchResults(name)
				.stream()
				.map(ServantResultDto::new)
				.collect(Collectors.toList());
	}

	public List<ServantResultDto> searchSubstituteServants(int id) {
		return this.servantRepository.findServantSubstitutes(id)
				.stream()
				.map(ServantResultDto::new)
				.collect(Collectors.toList());
	}

	private Session sessionFor(String database) {
		if (database == null) {
			return driver.session();
		}
		return driver.session(SessionConfig.forDatabase(database));
	}

	private String database() {
		return databaseSelectionProvider.getDatabaseSelection().getValue();
	}

	private ServantDetailsDto toServantDetails(TypeSystem ignored, org.neo4j.driver.Record record) {
		var servant = record.get("servant");
		return new ServantDetailsDto(
				servant.get("name").asString(), servant.get("servant_id").asInt(), servant.get("classe").asString(), servant.get("power").asString()
		);
	}
}
