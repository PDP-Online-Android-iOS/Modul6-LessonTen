package dev.ogabek.java.manager;

public interface StorageHandler {
    public void onSuccess(String imgUrl);
    public void onError(Exception exception);
}
