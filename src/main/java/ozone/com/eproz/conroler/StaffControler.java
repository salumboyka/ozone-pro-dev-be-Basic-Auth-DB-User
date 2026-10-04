package ozone.com.eproz.conroler;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ozone.com.eproz.dto.PageResponse;
import ozone.com.eproz.dto.StaffDtoRequest;
import ozone.com.eproz.dto.StaffDtoResponse;
import ozone.com.eproz.service.StaffServices;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/staffs")

public class StaffControler {

    private final StaffServices staffServices;

    @PostMapping
    public ResponseEntity<StaffDtoResponse> createStaff(
            @Valid @RequestBody StaffDtoRequest request,
            UriComponentsBuilder url) {
        StaffDtoResponse createdStaff = staffServices.createStaff(request);
        URI location = url.path("/api/v1/staffs/{id}")
                .buildAndExpand(createdStaff.id())
                .toUri();
        return ResponseEntity.created(location).body(createdStaff);
    }

    @GetMapping
    public PageResponse<StaffDtoResponse> findAll(
            @RequestParam(required = false) String fname,
            @RequestParam(required = false) String lname,
            @PageableDefault(size = 10, sort = "fname", direction = Sort.Direction.ASC) Pageable pageable) {
        return staffServices.findAll(fname, lname, sanitizePageable(pageable, "fname"));
    }

    private Pageable sanitizePageable(Pageable pageable, String defaultProperty) {
        if (pageable == null) {
            return PageRequest.of(0, 10, Sort.by(defaultProperty).ascending());
        }

        List<Sort.Order> safeOrders = pageable.getSort().stream()
                .filter(order -> "fname".equals(order.getProperty()))
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
    public StaffDtoResponse findById(@PathVariable UUID id) {
        return staffServices.findById(id);
    }

    @PutMapping ("/{id}")
    public StaffDtoResponse updateStaff(@PathVariable UUID id, @Valid @RequestBody StaffDtoRequest u) {
        return staffServices.updateStaff(id, u);
    }

}
