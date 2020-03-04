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
@Table(name = "roles")
public class Role implements Serializable 
{
    private static final long serialVersionUID = 8495817802073010928L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Size(max = 80)
    @Column(name = "name", length = 80)
    private String name;

    @OneToMany(
		mappedBy = "role",
		cascade = CascadeType.ALL,
		orphanRemoval = true
	)
    private List<PermissionRole> permissions = new ArrayList<>();

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PermissionRole> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<PermissionRole> permissions) {
        this.permissions = permissions;
    }
}