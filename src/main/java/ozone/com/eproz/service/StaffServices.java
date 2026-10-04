package ozone.com.eproz.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;

import lombok.RequiredArgsConstructor;
import ozone.com.eproz.dto.PageResponse;
import ozone.com.eproz.dto.StaffDtoRequest;
import ozone.com.eproz.dto.StaffDtoResponse;
import ozone.com.eproz.exception.ResourceNotFoundException;
import ozone.com.eproz.model.StaffModel;
import ozone.com.eproz.model.UserModel;
import ozone.com.eproz.repository.StaffRepository;
import ozone.com.eproz.repository.UserRepository;

@Service 
@RequiredArgsConstructor

@Transactional (readOnly = true)

public class StaffServices {

    private final StaffRepository staffs;
    private final UserRepository users;

    @Transactional
    public StaffDtoResponse createStaff(StaffDtoRequest userDtoRequest) {
        StaffModel staffModel = new StaffModel(
            userDtoRequest.email(),
            userDtoRequest.fname(),
            userDtoRequest.mname(),
            userDtoRequest.lname(),
            userDtoRequest.gender(),
            userDtoRequest.age(),
            userDtoRequest.address(),
            userDtoRequest.status(),
            userDtoRequest.dateOfjoin(),
            userDtoRequest.position()
        );
        staffModel.setUser(getUserOrNull(userDtoRequest.userId()));
        return StaffDtoResponse.from(staffs.save(staffModel));
    }
    

    public PageResponse<StaffDtoResponse> findAll(String fname, String lname, Pageable pageable) {
        
        return PageResponse.from(staffs.search(fname, lname, pageable).map(StaffDtoResponse::from));

    }

    public StaffDtoResponse findById(UUID id) {
        return StaffDtoResponse.from(getOrThrow(id));
    }

    @Transactional 
    public StaffDtoResponse updateStaff(UUID id, StaffDtoRequest u) {
        StaffModel staffModel = getOrThrow(id);

        staffModel.update(u.email(), u.age(), u.mname(), u.lname(), u.gender(), u.fname(), u.address(),
                u.status(), u.dateOfjoin(), u.position());
        staffModel.setUser(getUserOrNull(u.userId()));
        return StaffDtoResponse.from(staffModel);
    }


    private StaffModel getOrThrow(UUID id) {
        return staffs.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    private UserModel getUserOrNull(UUID userId) {
        return userId == null ? null : users.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
    }

    

}
