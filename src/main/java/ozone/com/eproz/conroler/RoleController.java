package ozone.com.eproz.conroler;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ozone.com.eproz.dto.PageResponse;
import ozone.com.eproz.dto.RoleDtoRequest;
import ozone.com.eproz.dto.RoleDtoResponse;
import ozone.com.eproz.service.RoleServices;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/roles")
public class RoleController {

    private final RoleServices roleServices;

    @PostMapping
    public ResponseEntity<RoleDtoResponse> create(
            @Valid @RequestBody RoleDtoRequest request,
            UriComponentsBuilder url) {
        RoleDtoResponse created = roleServices.create(request);
        URI location = url.path("/api/v1/roles/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public PageResponse<RoleDtoResponse> findAll(@PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return roleServices.findAll(sanitizePageable(pageable, "name"));
    }

    private Pageable sanitizePageable(Pageable pageable, String defaultProperty) {
        if (pageable == null) {
            return PageRequest.of(0, 10, Sort.by(defaultProperty).ascending());
        }

        List<Sort.Order> safeOrders = pageable.getSort().stream()
                .filter(order -> "name".equals(order.getProperty()))
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
    public RoleDtoResponse findById(@PathVariable UUID id) {
        return roleServices.findById(id);
    }

    @PutMapping("/{id}")
    public RoleDtoResponse update(@PathVariable UUID id, @Valid @RequestBody RoleDtoRequest request) {
        return roleServices.update(id, request);
    }
}