package tacos.web.api;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import tacos.Taco;
import tacos.data.TacoRepository;

@RestController
@RequestMapping(path = "/design", produces = "application/json")
@CrossOrigin(origins = "*")
public class DesignTacoController {

	private TacoRepository tacoRepository;

	@Autowired
	public DesignTacoController(TacoRepository tacoRepository) {
		this.tacoRepository = tacoRepository;
	}

	@GetMapping("/recent")
	public Resources<TacoResource> recentTacos() {

		Page<Taco> tacos = tacoRepository.findAll(PageRequest.of(0, 12, Sort.by("createdAt").descending()));

		List<TacoResource> tacoResources = TacoResource.tacoAssembler.toResources(tacos.getContent());
		
		Resources<TacoResource> resources = new Resources<>(tacoResources);
		
		
		resources.add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(DesignTacoController.class).recentTacos())
										   .withRel("recents"));

		return resources;
	}

	@GetMapping("/{id}")
	public ResponseEntity<Taco> tacoById(@PathVariable("id") Long id) {

		Optional<Taco> taco = tacoRepository.findById(id);

		if (taco.isPresent()) {
			return new ResponseEntity<>(taco.get(), HttpStatus.OK);
		}

		return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
	}

	@PostMapping(consumes = "application/json")
	@ResponseStatus(HttpStatus.CREATED)
	public Taco postTaco(@RequestBody Taco taco) {
		return tacoRepository.save(taco);

	}

}
