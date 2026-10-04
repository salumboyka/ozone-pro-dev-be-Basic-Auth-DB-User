package ozone.com.eproz.model;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;
import ozone.com.eproz.enums.GenderEnum;

@Entity
@Table(name = "staff", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email"),
})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "email"})
public class StaffModel extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "email", nullable = false, length = 50, unique = true)
    private String email;

    @Column(name = "fname", nullable = false, length = 120)
    private String fname;

    @Column(name = "mname", nullable = false, length = 120)
    private String mname;

    @Column(name = "lname", nullable = false, length = 120)
    private String lname;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 120)
    private GenderEnum gender = GenderEnum.MALE;

    @Column(name = "age", nullable = false, length = 120)
    private int age;

    @Column(name = "address", nullable = false, length = 120)
    private String address;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "date_of_join", nullable = false)
    private LocalDate dateOfjoin;

    @Column(name = "position", nullable = false, length = 120)
    private String position;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private UserModel user;

    public StaffModel(String email, String fname, String mname, String lname, GenderEnum gender, int age,
                     String address, String status, LocalDate dateOfjoin, String position) {
        this.email = email;
        this.fname = fname;
        this.mname = mname;
        this.lname = lname;
        this.gender = gender;
        this.age = age;
        this.address = address;
        this.status = status;
        this.dateOfjoin = dateOfjoin;
        this.position = position;
    }

    public void update(String email, int age, String mname, String lname, GenderEnum gender, String fname,
                       String address, String status, LocalDate dateOfjoin, String position) {
        this.email = email;
        this.fname = fname;
        this.mname = mname;
        this.lname = lname;
        this.gender = gender;
        this.age = age;
        this.address = address;
        this.status = status;
        this.dateOfjoin = dateOfjoin;
        this.position = position;
    }

    public void setUser(UserModel user) {
        if (this.user != null && this.user.getStaff() == this) {
            this.user.setStaff(null);
        }
        this.user = user;
        if (user != null && user.getStaff() != this) {
            user.setStaff(this);
        }
    }
}
