package ozone.com.eproz.conroler;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ozone.com.eproz.dto.AttachmentDtoRequest;
import ozone.com.eproz.dto.AttachmentDtoResponse;
import ozone.com.eproz.dto.PageResponse;
import ozone.com.eproz.dto.TrainingDtoRequest;
import ozone.com.eproz.dto.TrainingDtoResponse;
import ozone.com.eproz.service.TrainingServices;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/training")
public class TrainingController {

	private final TrainingServices trainingServices;

	@PostMapping(consumes = "application/json")
	public ResponseEntity<TrainingDtoResponse> createTraining(
			@Valid @RequestBody TrainingDtoRequest request,
			UriComponentsBuilder url) {
		TrainingDtoResponse created = trainingServices.createTraining(request);
		URI location = url.path("/api/v1/training/{id}").buildAndExpand(created.id()).toUri();
		return ResponseEntity.created(location).body(created);
	}

	@PostMapping(consumes = "multipart/form-data")
	public ResponseEntity<TrainingDtoResponse> createTrainingWithAttachment(
			@Valid @RequestPart(value = "request", required = false) TrainingDtoRequest request,
			@Valid @RequestPart(value = "data", required = false) TrainingDtoRequest data,
			@ModelAttribute TrainingDtoRequest formRequest,
			@RequestPart("attachmentFile") MultipartFile attachmentFile,
			UriComponentsBuilder url) {
		if (request == null) {
			request = data;
		}
		if (request == null) {
			request = formRequest;
		}
		if (request == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Training details are required");
		}
		TrainingDtoRequest requestWithFile = new TrainingDtoRequest(
				request.title(), request.description(), request.provider(), request.attachment(),
				request.startDate(), request.endDate(), request.staffId(), attachmentFile);
		TrainingDtoResponse created = trainingServices.createTraining(requestWithFile);
		URI location = url.path("/api/v1/training/{id}").buildAndExpand(created.id()).toUri();
		return ResponseEntity.created(location).body(created);
	}

	@GetMapping
	public PageResponse<TrainingDtoResponse> findAll(
			@RequestParam(required = false) UUID staffId,
			@PageableDefault(size = 10, sort = "title", direction = Sort.Direction.ASC) Pageable pageable) {
		return trainingServices.findAll(staffId, sanitizePageable(pageable, "title"));
	}

	private Pageable sanitizePageable(Pageable pageable, String defaultProperty) {
		if (pageable == null) {
			return PageRequest.of(0, 10, Sort.by(defaultProperty).ascending());
		}

		List<Sort.Order> safeOrders = pageable.getSort().stream()
				.filter(order -> "title".equals(order.getProperty()))
				.map(order -> order.isAscending()
						? Sort.Order.asc(defaultProperty)
						: Sort.Order.desc(defaultProperty))
				.collect(Collectors.toList());

		if (safeOrders.isEmpty()) {
			return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(defaultProperty).ascending());
		}

		return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(safeOrders));
	}

	@GetMapping("/{id}")
	public TrainingDtoResponse findById(@PathVariable UUID id) {
		return trainingServices.findById(id);
	}

	@PutMapping("/{id}")
	public TrainingDtoResponse updateTraining(
			@PathVariable UUID id,
			@Valid @RequestBody TrainingDtoRequest request) {
		return trainingServices.updateTraining(id, request);
	}

	@PostMapping(value = "/{id}/attachment", consumes = "multipart/form-data")
	public AttachmentDtoResponse uploadAttachment(
			@PathVariable UUID id,
			@RequestPart("file") MultipartFile file) {
		return trainingServices.uploadAttachment(id, new AttachmentDtoRequest(file));
	}
}
