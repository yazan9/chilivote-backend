package chilivote.Entities;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "permissions")
public class Permission implements Serializable {
    private static final long serialVersionUID = 5313493413859894403L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Size(max = 80  )
    @Column(name = "name", length = 80)
    private String name;

    @Size(max = 255  )
    @Column(name = "description", length = 255)
    private String description;

    @OneToMany(
		mappedBy = "permission",
		cascade = CascadeType.ALL,
		orphanRemoval = true
	)
    private List<PermissionRole> roles = new ArrayList<>();
    
    public List<PermissionRole> getRoles() {
        return roles;
    }

    public void setRoles(List<PermissionRole> roles) {
        this.roles = roles;
    }    

    public long getPermissionId() {
        return id;
    }

    public void setPermissionId(long permissionId) {
        this.id = permissionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String permissionName) {
        this.name = permissionName;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String permissionDescription) {
        this.description = permissionDescription;
    }
}