package ozone.com.eproz.service;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ozone.com.eproz.dto.RoleDtoRequest;
import ozone.com.eproz.dto.RoleDtoResponse;
import ozone.com.eproz.dto.PageResponse;
import ozone.com.eproz.exception.DuplicateResourceException;
import ozone.com.eproz.exception.ResourceNotFoundException;
import ozone.com.eproz.repository.RoleRepository;
import ozone.com.eproz.model.RoleModel;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleServices {

    private final RoleRepository roles;

    public PageResponse<RoleDtoResponse> findAll(Pageable pageable) {
        return PageResponse.from(roles.findAll(pageable).map(RoleDtoResponse::from));
    }

    public RoleDtoResponse findById(UUID id) {
        return roles.findById(id)
                .map(RoleDtoResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Role", id));
    }

    @Transactional
    public RoleDtoResponse create(RoleDtoRequest request) {
        if (roles.existsByName(request.name())) {
            throw new DuplicateResourceException("Role name already exists");
        }

        return RoleDtoResponse.from(roles.save(new RoleModel(request.name())));
    }

    @Transactional
    public RoleDtoResponse update(UUID id, RoleDtoRequest request) {
        RoleModel role = roles.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", id));
        if (roles.existsByNameAndIdNot(request.name(), id)) {
            throw new DuplicateResourceException("Role name already exists");
        }

        role.setName(request.name());
        return RoleDtoResponse.from(role);
    }
}