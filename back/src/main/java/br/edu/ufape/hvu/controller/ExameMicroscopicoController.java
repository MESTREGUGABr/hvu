package br.edu.ufape.hvu.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;

import br.edu.ufape.hvu.model.ExameMicroscopico;
import br.edu.ufape.hvu.facade.Facade;
import br.edu.ufape.hvu.controller.dto.request.ExameMicroscopicoRequest;
import br.edu.ufape.hvu.controller.dto.response.ExameMicroscopicoResponse;
import br.edu.ufape.hvu.exception.IdNotFoundException;


 
@RestController
@RequestMapping("/api/v1/")
public class ExameMicroscopicoController {
	@Autowired
	private Facade facade;
	@Autowired
	private ModelMapper modelMapper;

	@PreAuthorize("hasAnyRole('MEDICOLAPA', 'SECRETARIOLAPA')")
	@GetMapping("exameMicroscopico")
	public List<ExameMicroscopicoResponse> getAllExameMicroscopico() {
		return facade.getAllExameMicroscopico()
			.stream()
			.map(ExameMicroscopicoResponse::new)
			.toList();
	}

	@PreAuthorize("hasAnyRole('MEDICOLAPA', 'SECRETARIOLAPA')")
	@PostMapping("exameMicroscopico")
	public ExameMicroscopicoResponse createExameMicroscopico(@Valid @RequestBody ExameMicroscopicoRequest newObj) {
		return new ExameMicroscopicoResponse(facade.saveExameMicroscopico(newObj.convertToEntity()));
	}

	@PreAuthorize("hasAnyRole('MEDICOLAPA', 'SECRETARIOLAPA')")
	@GetMapping("exameMicroscopico/{id}")
	public ExameMicroscopicoResponse getExameMicroscopicoById(@PathVariable Long id) {
		try {
			return new ExameMicroscopicoResponse(facade.findExameMicroscopicoById(id));
		} catch (IdNotFoundException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
		}
	}

	@PreAuthorize("hasAnyRole('MEDICOLAPA', 'SECRETARIOLAPA')")
	@PatchMapping("exameMicroscopico/{id}")
	public ExameMicroscopicoResponse updateExameMicroscopico(@PathVariable Long id, @Valid @RequestBody ExameMicroscopicoRequest obj) {
		try {
			//ExameMicroscopico o = obj.convertToEntity();
			ExameMicroscopico oldObject = facade.findExameMicroscopicoById(id);

			//etapa
			if (obj.getEtapa() != null) {
				oldObject.setEtapa(facade.findEtapaById(obj.getEtapa().getId()));
				obj.setEtapa(null);
			}



			// foto
			if (obj.getFoto() != null) {
				oldObject.setFoto(facade.findFotoById(obj.getFoto().getId()));
				obj.setFoto(null);
			}



			TypeMap<ExameMicroscopicoRequest, ExameMicroscopico> typeMapper = modelMapper
													.typeMap(ExameMicroscopicoRequest.class, ExameMicroscopico.class)
													.addMappings(mapper -> mapper.skip(ExameMicroscopico::setId));			
			
			
			typeMapper.map(obj, oldObject);	
			return new ExameMicroscopicoResponse(facade.updateExameMicroscopico(oldObject));
		} catch (IdNotFoundException ex) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
		}
		
	}

	@PreAuthorize("hasAnyRole('MEDICOLAPA', 'SECRETARIOLAPA')")
	@DeleteMapping("exameMicroscopico/{id}")
	public String deleteExameMicroscopico(@PathVariable Long id) {
		try {
			facade.deleteExameMicroscopico(id);
			return "";
		} catch (IdNotFoundException ex) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
		}
		
	}
	

}
