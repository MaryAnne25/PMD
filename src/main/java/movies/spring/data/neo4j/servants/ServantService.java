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
						//"OPTIONAL MATCH (person:Person)-[r]->(movie) " +
						//"WITH movie, COLLECT({ name: person.name, job: REPLACE(TOLOWER(TYPE(r)), '_in', ''), role: HEAD(r.roles) }) as cast " +
						"RETURN servant { .name, .servant_id }"
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

	/**
	 * This is an example of when you might want to use the pure driver in case you have no need for mapping at all, neither in the
	 * form of the way the {@link org.springframework.data.neo4j.core.Neo4jClient} allows and not in form of entities.
	 *
	 * @return A representation D3.js can handle
	 */
	/*public Map<String, List<Object>> fetchMovieGraph() {

		var nodes = new ArrayList<>();
		var links = new ArrayList<>();

		try (Session session = sessionFor(database())) {
			var records = session.readTransaction(tx -> tx.run(""
				+ " MATCH (s:Servant) - [r:CONHECE] - (s:Servant)"
				+ " RETURN s.name AS movie"
			).list());
			records.forEach(record -> {
				var movie = Map.of("label", "movie", "title", record.get("movie").asString());

				var targetIndex = nodes.size();
				nodes.add(movie);

				record.get("actors").asList(Value::asString).forEach(name -> {
					var actor = Map.of("label", "actor", "title", name);

					int sourceIndex;
					if (nodes.contains(actor)) {
						sourceIndex = nodes.indexOf(actor);
					} else {
						nodes.add(actor);
						sourceIndex = nodes.size() - 1;
					}
					links.add(Map.of("source", sourceIndex, "target", targetIndex));
				});
			});
		}
		return Map.of("nodes", nodes, "links", links);
	}*/

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
				servant.get("name").asString(), servant.get("servant_id").asInt()
		);
	}
}
