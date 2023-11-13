package async;

public interface Callback<R>{
    void runResultOnUiThread(R result);
}
