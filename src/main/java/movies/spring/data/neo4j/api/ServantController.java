package movies.spring.data.neo4j.api;

import movies.spring.data.neo4j.servants.ServantResultDto;
import movies.spring.data.neo4j.servants.ServantService;
import movies.spring.data.neo4j.servants.ServantDetailsDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Michael J. Simons
 */
@RestController
class ServantController {

	private final ServantService servantService;

	ServantController(ServantService servantService) {
		this.servantService = servantService;
	}

	@GetMapping("/servant/{name}")
	public ServantDetailsDto findByName(@PathVariable("name") String name) {
		return servantService.fetchDetailsByName(name);
	}

	@GetMapping("/search")
	List<ServantResultDto> search(@RequestParam("q") String name) {
		return servantService.searchServantsByName(stripWildcards(name));
	}

	/*@GetMapping("/graph")
	public Map<String, List<Object>> getGraph() {
		return movieService.fetchMovieGraph();
	}*/

	private static String stripWildcards(String name) {
		String result = name;
		if (result.startsWith("*")) {
			result = result.substring(1);
		}
		if (result.endsWith("*")) {
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}
}
