package chilivote.Entities;
import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "permissionsRoles")
public class PermissionRole implements Serializable {
    private static final long serialVersionUID = 5313493413859894403L;

    @Id
	@ManyToOne
	private Role role;

	@Id
	@ManyToOne
    private Permission permission;

    public PermissionRole(Role role, Permission permission) {
        this.role = role;
        this.permission = permission;
    }

    public PermissionRole(){}
    
    @Override
	public boolean equals(Object o) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}
		PermissionRole that = (PermissionRole) o;
		return Objects.equals( role, that.role ) &&
				Objects.equals( permission, that.permission );
	}

	@Override
	public int hashCode() {
		return Objects.hash( role, permission );
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }
}