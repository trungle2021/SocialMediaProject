package s;

import jakarta.persistence.*;

import java.util.Collection;

@Entity
public class Roles {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;
    @Basic
    @Column(name = "role_type", nullable = true, length = 20)
    private String roleType;
    @OneToMany(mappedBy = "rolesByRoleId")
    private Collection<Accounts> accountsById;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Roles roles = (Roles) o;

        if (id != null ? !id.equals(roles.id) : roles.id != null) return false;
        if (roleType != null ? !roleType.equals(roles.roleType) : roles.roleType != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (roleType != null ? roleType.hashCode() : 0);
        return result;
    }

    public Collection<Accounts> getAccountsById() {
        return accountsById;
    }

    public void setAccountsById(Collection<Accounts> accountsById) {
        this.accountsById = accountsById;
    }
}