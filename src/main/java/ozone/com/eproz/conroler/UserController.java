package ozone.com.eproz.conroler;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ozone.com.eproz.dto.PageResponse;
import ozone.com.eproz.dto.UserDtoRequest;
import ozone.com.eproz.dto.UserDtoResponse;
import ozone.com.eproz.service.UserServices;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserServices userServices;

    @GetMapping
    public PageResponse<UserDtoResponse> findAll(@PageableDefault(size = 10, sort = "username", direction = Sort.Direction.ASC) Pageable pageable) {
        return userServices.findAll(sanitizePageable(pageable, "username"));
    }

    private Pageable sanitizePageable(Pageable pageable, String defaultProperty) {
        if (pageable == null) {
            return PageRequest.of(0, 10, Sort.by(defaultProperty).ascending());
        }

        List<Sort.Order> safeOrders = pageable.getSort().stream()
                .filter(order -> "username".equals(order.getProperty()))
                .map(order -> order.isAscending()
                        ? Sort.Order.asc(defaultProperty)
                        : Sort.Order.desc(defaultProperty))
                .collect(Collectors.toList());

        if (safeOrders.isEmpty()) {
            return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(defaultProperty).ascending());
        }

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(safeOrders));
    }

    @PostMapping
    public ResponseEntity<UserDtoResponse> register(
            @Valid @RequestBody UserDtoRequest request,
            UriComponentsBuilder url) {
        UserDtoResponse created = userServices.register(request);
        URI location = url.path("/api/v1/users/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }
}