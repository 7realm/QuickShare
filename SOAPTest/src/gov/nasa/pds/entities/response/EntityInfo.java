
package gov.nasa.pds.entities.response;

import gov.nasa.pds.entities.BaseObject;


public class EntityInfo extends BaseObject {

    protected long id;
    protected String name;

    public long getId() {
        return id;
    }

    public void setId(long value) {
        this.id = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }
}
