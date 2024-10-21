package ch.seg.inf.unibe.gameserver.db.logic.model;

import ch.seg.inf.unibe.gameserver.db.persistence.DataAccessLayer;

import java.util.List;

/**
 * The key ID to identify the object in the SQL database.
 */
public abstract class IdentifiableElement {

    private int id = -1;

    public int getID() {
        if (id == -1) { // lazy initialize ID
            this.id = DataAccessLayer.getInstance().getNextID();
        }
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return toString(0);
    }

    public abstract String toString(int indent);

    public String ref(IdentifiableElement obj) {
        if (obj != null) {
            return obj.getClass().getSimpleName() + "@" + obj.getID();
        } else {
            return null + "";
        }
    }

    public String refs(List<? extends IdentifiableElement> list) {
        if (list != null) {
            return "[" + String.join(", ", list.stream().map(this::ref).toArray(String[]::new)) + "]";
        } else {
            return null + "";
        }
    }

    public String sub(IdentifiableElement contained, int indent) {
        if (contained != null) {
            return contained.toString(indent);
        } else {
            return null + "";
        }
    }

    public String subs(List<? extends IdentifiableElement> contained, int indent) {
        if (contained != null) {
            StringBuilder toString = new StringBuilder();
            toString.append("[\n");

            indent += 4;

            for (IdentifiableElement element : contained) {
                toString.append(" ".repeat(indent));
                toString.append(element.toString(indent)).append(",\n");
            }

            toString.append(" ".repeat(indent - 4));
            toString.append("]");
            return toString.toString();
        } else {
            return null + "";
        }
    }
}
