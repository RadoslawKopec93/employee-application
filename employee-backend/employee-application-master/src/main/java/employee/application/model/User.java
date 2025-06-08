package employee.application.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

import employee.application.model.enums.RoleType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Table(name = "users")
@Entity
@Data
@Setter
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String providerId;

    @Column(nullable = true)
    private String provider;

    private String email;

    @Column(nullable = true)
    private String password;

    @ManyToMany
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    private Set<Role> roles;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User)) {
            return false;
        }
        User user = (User) o;
        if ((providerId == null ? user.providerId == null : providerId.equals(user.providerId))
                && (provider == null ? user.provider == null : provider.equals(user.provider))
                && (email == null ? user.email == null : email.equals(user.email))
                && (password == null ? user.password == null : password.equals(user.password))) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        if (providerId != null) {
            return Objects.hash(providerId, provider);
        }

        return Objects.hash(email, password);
    }

    public Set<RoleType> getRolesTypes() {
        Iterator<Role> iterator = this.roles.iterator();
        Set<RoleType> set = new HashSet<>();
        while (iterator.hasNext()) {
            set.add(iterator.next().getRoleType());
        }
        return set;
    }
}
