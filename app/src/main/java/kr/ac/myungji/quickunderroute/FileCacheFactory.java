import android.content.Context;
import java.io.File;
import java.util.HashMap;

public class FileCacheFactory {

    private static boolean initialized = false;
    private static FileCacheFactory instance = new FileCacheFactory();

    public static void initialize(Context context, String file_dir) {
        if (!initialized) {
            synchronized (instance) {
                if (!initialized) {
                    instance.init(context,file_dir);
                    initialized = true;
                }
            }
        }
    }

    public static FileCacheFactory getInstance() {
        if (!initialized) {
            throw new IllegalStateException(
                    "Not initialized. You must call FileCacheFactory.initialize() before getInstance()");
        }
        return instance;
    }

    private HashMap<String, FileCache> mCacheMap = new HashMap<String, FileCache>();
    private File mCacheBaseDir;

    private FileCacheFactory() {
    }

    private void init(Context context) {
        mCacheBaseDir = context.getCacheDir();
    }

    private void init(Context context, String file_dir) {
//    cacheBaseDir = context.getCacheDir();
        mCacheBaseDir = new File(file_dir);
    }

    public FileCache create(String cacheName, int maxKbSizes) {
        synchronized (mCacheMap) {
            FileCache cache = mCacheMap.get(cacheName);
            File cacheDir = new File(mCacheBaseDir, cacheName);
            if (cache != null) {
                try {
                    cache = new FileCacheImpl(cacheDir, maxKbSizes);
                    mCacheMap.put(cacheName, cache);
                } catch (Exception e) {
                    String.format("FileCache[%s] Aleady exists", cacheName);
                }
            }


            return cache;
        }
    }

    public FileCache get(String cacheName) {
        synchronized (mCacheMap) {
            FileCache cache = mCacheMap.get(cacheName);
            if (cache == null) {
                try {

                }catch (Exception e)
                {
                    String.format("FileCache[%s] not founds.", cacheName);
                }
            }
            return cache;
        }
    }

    public void destroy(String cacheName)
    {
        FileCache cache = mCacheMap.get(cacheName);

        File file = new File(mCacheBaseDir+File.separator+cacheName);
        if(file.exists())
        {
            file.delete();
        }
    }

    public void clear(){
        mCacheMap.clear();
    }

    public boolean has(String cacheName) {
        return mCacheMap.containsKey(cacheName);
    }
}