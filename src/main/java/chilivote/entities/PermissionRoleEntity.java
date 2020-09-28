package chilivote.entities;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "permissionsRoles")
public class PermissionRoleEntity implements Serializable {
    private static final long serialVersionUID = 5313493413859894403L;

    @Id
	@ManyToOne
	private RoleEntity role;

	@Id
	@ManyToOne
    private PermissionEntity permission;

    public PermissionRoleEntity(RoleEntity role, PermissionEntity permission) {
        this.role = role;
        this.permission = permission;
    }

    @Override
	public boolean equals(Object o) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}
		PermissionRoleEntity that = (PermissionRoleEntity) o;
		return Objects.equals( role, that.role ) &&
				Objects.equals( permission, that.permission );
	}

	@Override
	public int hashCode() {
		return Objects.hash( role, permission );
    }
}