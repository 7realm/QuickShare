package my.activity.demo.listmanager.persistence;

public class PersistenceSharedListException extends RuntimeException {
    private static final long serialVersionUID = -9029996838646779181L;

    public PersistenceSharedListException(String detailMessage) {
        super(detailMessage);
    }

    public PersistenceSharedListException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

}
