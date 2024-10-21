package ch.seg.inf.unibe.gameserver.db.persistence.model;

import ch.seg.inf.unibe.gameserver.db.logic.model.IdentifiableElement;
import ch.seg.inf.unibe.gameserver.db.persistence.DatabaseAccess;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.seg.inf.unibe.gameserver.db.persistence.DatabaseAccessUtil.*;

/**
 * @param <E> IdentifiableElement implementation class
 * @param <C> Container class
 */
public abstract class IdentifiableElementDAO<E extends IdentifiableElement> {

    /**
     * Stores the mapping from SQL row index to the object.
     */
    private Map<Integer, E> sqlIDToJavaObjectIndex = new HashMap<>();

    public int getLoadedCount() {
        return sqlIDToJavaObjectIndex.size();
    }

    public E getLoaded(int id) {
        return sqlIDToJavaObjectIndex.get(id);
    }

    public boolean isLoaded(int id) {
        return sqlIDToJavaObjectIndex.containsKey(id);
    }

    public void loaded(E obj) {
        if (obj != null) {
            sqlIDToJavaObjectIndex.put(obj.getID(), obj);
        }
    }

    public void unloaded(E obj) {
        sqlIDToJavaObjectIndex.remove(obj.getID());
    }

    protected E checkedResults(List<E> elements, Class<E> type, int id) {
        // Utility for error handling...
        if (elements.size() == 1) {
            return elements.get(0);
        } else {
            String error = type.getSimpleName() + " with ID " + id + " could not be selected in database! Result: " + elements;
            System.out.println(RED + "SQL Exception: " + error + RESET);
            System.out.println(RED + "To reset the database, delete the database file: " + DatabaseAccess.getInstance().getDatabaseURL() + RESET);
            if (THROW_SQL_RUNTIME_EXCEPTIONS) {
                throw new RuntimeException(error);
            }
        }
        return null;
    }

    protected E checkedResult(E element, Class<E> type, int id) {
        // Utility for error handling...
        if (element != null) {
            return element;
        } else {
            String error = type.getSimpleName() + " with ID " + id + " could not be selected in database! Result: " + element;
            System.out.println(RED + "SQL Exception:" + error + RESET);
            System.out.println(RED + "To reset the database, delete the database file: " + DatabaseAccess.getInstance().getDatabaseURL() + RESET);
            if (THROW_SQL_RUNTIME_EXCEPTIONS) {
                throw new RuntimeException(error);
            }
        }
        return null;
    }

}
